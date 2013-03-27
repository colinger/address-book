(ns address-book.handler
  (:use [compojure.core]
        [ring.adapter.jetty]
        [korma.core]
        [sandbar.auth]
        [sandbar.form-authentication]
        [sandbar.stateful-session] 
        [sandbar.validation]
        [hiccup.core]) ;; 
  (:import (java.io File))
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clj-json.core :as json]
            [address-book.address :as address]
            [address-book.models :as model]
            [address-book.service :as service]
            [address-book.auth :as auth]
            [address-book.middleware :as mdw]
            [address-book.utils.string :as summary]
	        [address-book.utils.number :as number]
            [address-book.utils.thumbnails :as thumbnails]
            [net.cgrand.enlive-html :as enlive]
            (ring.middleware [multipart-params :as mp])
            (clojure.contrib [duck-streams :as ds])))
;;
(defn redirect-to
  "A shortcut for a '302 Moved' HTTP redirect."
  [location]
  {:status 302
   :headers {"Location" location}
   })
;;static html
(enlive/deftemplate index  "index.html" [])
(enlive/deftemplate notFound  "404/404.html" [])
(def login-home (enlive/html-resource "admin/login.html"))
(enlive/deftemplate  add-game-page "admin/add.html" [])
;;
(defn extract-body [html]
  (enlive/at html [#{:html :body}] enlive/unwrap))
;;layout
(enlive/deftemplate layout "layout.html" [title styles scripts content]
  [#{:title}] (enlive/content title)
  [:link.style] (enlive/clone-for [style styles]
                                  (enlive/set-attr :href style))
  [:script.import] (enlive/clone-for [script scripts]
                                     (enlive/set-attr :src script))
  [:div.content_detail] (enlive/substitute (extract-body content))
  )
(enlive/deftemplate admin-layout "admin/layout.html" [title styles scripts content]
  [#{:title}] (enlive/content title)
  [:link.style] (enlive/clone-for [style styles]
                                  (enlive/set-attr :href style))
  [:script.import] (enlive/clone-for [script scripts]
                                     (enlive/set-attr :src script))
  [:div.content] (enlive/substitute (extract-body content))
  )
;;show all games			 
(defn show-a-game [game]
  (enlive/at (enlive/html-resource "game.html")
             [:span.title] (enlive/content (:name game))
             [:p.date] (enlive/html-content (str "<a href=\"#\">" "黄药师" "</a>" " 发布于 " (summary/date-format (:create_date game))))
             [:div.description] (enlive/html-content (:description game))
			 [:ul.tags] (enlive/clone-for [tag (:tags game)]
				[:a.tag] (enlive/set-attr :href (str "/tags/" (:name tag)))
				[:span] (enlive/content (:name tag))))) 
;;    
(defn edit-a-game [game]
  (enlive/at (enlive/html-resource "admin/deploy.html")
             [:input#id] (enlive/set-attr :value (:id game))
             [:div#titlewrap :input] (enlive/set-attr :value (:name game))
             [:div#postdivrich :textarea] (enlive/html-content (:description game))
			 [:ul.tags] (enlive/clone-for [tag (:tags game)]
				[:a.tag] (enlive/set-attr :href (str "/tags/" (:name tag)))
				[:span] (enlive/content (:name tag)))))
;;
(defn json-response [data & [status]]
    {:status (or status 200)
        :headers {"Content-Type" "application/json;charset=UTF-8"}
        :body (json/generate-string data)})
(defn render [location]
  (apply str location))
;;upload file
(enlive/deftemplate upload-show "success.html" [content]
  [:script] (enlive/html-content content))
  
(defn upload-file
  [file callback]
  (let [new-name (file :filename)];;(str (java.util.UUID/randomUUID) "."  "jpg")
  ;;(println file)
  ;;(println callback)
  (ds/copy (file :tempfile) (ds/file-str (str "public/images/" new-name)))
    (do
      (thumbnails/thumbnail new-name);;generate thumbnail
      (upload-show (str "window.parent.CKEDITOR.tools.callFunction(" callback ",'" (str "/images/" new-name) "',''" ")")))
  ))
;;game details
(defn game-details
  "Game details page handler"
  [id]
  (let [gameId (Integer/parseInt id)]
 ;;     (->> 
        (first (select model/games (where {:id gameId}) (with model/tags)))
 ;;       show-a-game
 ;;       )
    )
  )
;;-----------------------------security
(defn login [params]
  (println (:username params) (:password params))
  (if (or (empty? (:username params))(empty?(:password params)))
    (redirect-to "/admin/login") 
    (do (println (session-get :current-user "unkonow"))
      (session-put! :current-user params)
      (render (add-game-page)))))
;;autheticate
(def security-policy
  [#".*\.(css|js|png|jpg|gif|ico)$" :any
   #"/bootstrap.*" :any
   #"/bdsitemap.txt" :any
   #"/robots.txt" :any
   #"/login.*" :any
   #"/logout.*" :any
   #"/tags/.*" :any
   #"/mobile/.*" :any
   #"/board/.*" :any
   #"/search.*" :any
   #"/permission-denied.*" :any
   #"/show.*" :any
   #"/game.*" :any
   #"/uploader.*" :any
   #"/admin.*" :user
   #"/api/.*" :any
   #"/" #{:any}])
;;css
(def game-css ["/css/game.css"])
(def game-admin-css ["/css/game.css" "/css/admin/game.css" "/bootstrap/less/bootstrap.less"])
;;js
(def game-admin-js ["/ckeditor/ckeditor.js" "/js/admin/ckeditor.js" "/js/jquery-1.4.2.min.js" "/js/admin/tag.js" "/bootstrap/js/less-1.3.3.min.js"])  
;;routes
(def TITLE "步步为赢 | 游戏 | 玩家 | 切磋")
(defroutes app-routes
  (GET "/" {params :params} (layout "步步为赢 | 游戏 | 玩家 | 切磋" nil nil (service/show-all-games params)))  
  (GET "/game/:id" {params :params} 
       (let [game (game-details (:id params))]
		 (if (empty? game)
			(render (notFound));;(redirect-to "/")
			(layout (str (:name game) " | " "步步为赢") game-css nil (show-a-game game)))))
  (GET "/board/" {params :params} (layout TITLE nil nil (service/show-all-board-games params)))
  (GET "/mobile/" {params :params} (layout TITLE nil nil (service/show-all-mobile-games params)))
  (GET "/tags/:name" {params :params}
		  (layout (str (:name params) " | 步步为赢 | 游戏 | 玩家 | 切磋") nil nil (service/show-all-games-have-tags params)))
  (GET "/search" {params :params}
       (let [name (:q params)]
          (layout (str name " | 搜索结果 | 步步为赢 | 游戏 | 切磋") nil nil (service/search-all-games params))))        
  ;;--------------------------admin
  (GET "/admin" {params :params} (admin-layout "后台管理" nil nil (service/admin-show-all-games params)))
  (GET "/admin/game" [] 
       (if (= "unkown" (session-get :current-user "unknow"))
         (admin-layout "后台管理-登录" nil nil login-home)
         (render (add-game-page))))
  (GET "/admin/game/edit/:id" [id]
       (let [game (game-details id)]
         (admin-layout (str (:name game) " | " "游戏必杀技") game-admin-css game-admin-js(edit-a-game game)))) 
  (POST "/admin/game" {params :params} (do (model/save-or-update-game params)
                                     (redirect-to "/admin")))
  (GET "/admin/game/del/:id" [id]
       (do (model/game-delete id)
         (redirect-to "/admin")))
  (POST "/admin/game/tag" {params :params}
        ;;save tags
		(json-response
        (model/save-tag-for-game params)
        ))
  (mp/wrap-multipart-params 
      (POST "/uploader" {params :params} (do (println params)(upload-file (:upload params) (:CKEditorFuncNum params)))))
  (form-authentication-routes (fn [_ c] (html c)) (auth/form-authentication-adapter))  
  ;;---------------------------------------------------------
  ;;API
  (GET "/api/game" [] (render (index)))
  (POST "/api/game" {params :params}
        (let [title (get params :name)
              desc (get params :descritption)]
          (if (or (nil? title) (nil? desc))
            (json-response {:status "failed" :message "Please check you missed parameters"})
            (try
              (model/save-or-update-game params)
              (json-response {:status "success" :message "It will be presented after auditing"})
              (catch Exception e (json-response {:status "failed" :message "Please try again later"}))))))
  (route/files "/" {:root "public"})        
  (route/not-found (render (notFound))))

(def webapp (-> app-routes 
              (with-security security-policy form-authentication)              
              wrap-stateful-session
              mdw/wrap-request-logging
              handler/site
              ))

(defn start-app 
  [port] (future (run-jetty (var webapp) {:port port :join? false})))

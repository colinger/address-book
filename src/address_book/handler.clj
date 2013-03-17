(ns address-book.handler
  (:use [compojure.core]
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
            [address-book.auth :as auth]
            [address-book.middleware :as mdw]
            [address-book.utils.string :as summary]
            [net.cgrand.enlive-html :as enlive]
            (ring.middleware [multipart-params :as mp])
            (clojure.contrib [duck-streams :as ds])))
;;
(defn things[] (select model/games))

(defn redirect-to
  "A shortcut for a '302 Moved' HTTP redirect."
  [location]
  {:status 302
   :headers {"Location" location}
   })
;;static html
(def index (enlive/html-resource "index.html"))
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
  [:div.content] (enlive/substitute (extract-body content)))
;;show all games
(defn show-all-games [things]
  (enlive/at (enlive/html-resource "show.html")
             [:div.post] (enlive/clone-for [game things]
                                    [:a.title] (enlive/do->
                                                 (enlive/set-attr :href (str "/game/" (:id game)))
                                                 (enlive/content (:name game))) 
									[:p.date] (enlive/html-content (str "<a href=\"#\">" "黄药师" "</a>" " 发布于 " (summary/date-format (:create_date game))))				
                                    [:p.detail](enlive/html-content (summary/summary-post (:description game) 120))
									[:a.more] (enlive/set-attr :href (str "/game/" (:id game))))))
(defn show-a-game [game]
  (enlive/at (enlive/html-resource "game.html")
             [:span.title] (enlive/content (:name game))
             [:p.date] (enlive/html-content (str "<a href=\"#\">" "黄药师" "</a>" " 发布于 " (summary/date-format (:create_date game))))
             [:div.content] (enlive/html-content (:description game))
			 [:ul.tags] (enlive/clone-for [tag (:tags game)]
				[:a.tag] (enlive/set-attr :href (str "/tags/" (:name tag)))
				[:span] (enlive/content (:name tag))))) 
;;
(defn admin-list-games [things]
  (enlive/at (enlive/html-resource "admin/edit.html")
             [:div.post] (enlive/clone-for [thing things]
                              [:a.title] (enlive/do->
                                           (enlive/set-attr :href (str "/admin/game/edit/" (:id thing)))
                                           (enlive/content (:name thing)))
                              [:a.del] (enlive/set-attr :href (str "/admin/game/del/" (:id thing)))
                              [:div.content] (enlive/html-content (summary/summary-post (:description thing) 120)))))      
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
    (upload-show (str "window.parent.CKEDITOR.tools.callFunction(" callback ",'" (str "/images/" new-name) "',''" ")"))
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
   #"/login.*" :any
   #"/logout.*" :any
   #"/tags/.*" :any
   #"/search.*" :any
   #"/permission-denied.*" :any
   #"/show.*" :any
   #"/game.*" :any
   #"/uploader.*" :any
   #"/admin.*" :user
   #"/" #{:any}])
;;css
(def game-css ["/css/game.css"])
(def game-admin-css ["/css/game.css" "/css/admin/game.css"])
;;js
(def game-admin-js ["/ckeditor/ckeditor.js" "/js/admin/ckeditor.js" "/js/jquery-1.4.2.min.js" "/js/admin/tag.js"])  
;;routes
(defroutes app-routes
  (GET "/" [] (layout "九阴真经秘籍 | 玩家 | 切磋" nil nil (show-all-games (things))))   
  (POST "/admin/game" {params :params} (do (model/save-or-update-game params)
                                     (redirect-to "/admin")))
  (GET "/game/:id" [id] 
       (let [game (game-details id)]
         (layout (str (:name game) " | " "九阴秘籍") game-css nil (show-a-game game))))
  (GET "/tags/:name" [name]
	   (let [games (model/game-has-tag name)]
		  (layout "九阴真经秘籍 | 玩家 | 切磋" nil nil (show-all-games games)))) 
  (POST "/search" {params :params}
       (let [name (:q params)]
          (layout "搜索结果 | 九阴真经秘籍" nil nil (show-all-games (model/search-game name)))))        
  ;;--------------------------admin
  (GET "/admin" [] (layout "后台管理" nil nil (admin-list-games (things))))
  (GET "/admin/game" [] 
       (if (= "unkown" (session-get :current-user "unknow"))
         (layout "后台管理-登录" nil nil login-home)
         (render (add-game-page))))
  (GET "/admin/game/edit/:id" [id]
       (let [game (game-details id)]
         (layout (str (:name game) " | " "游戏必杀技") game-admin-css game-admin-js(edit-a-game game))))
  (GET "/admin/game/del/:id" [id]
       (do (model/game-delete id)
         (layout "后台管理" nil nil (admin-list-games (things)))))
  (POST "/admin/game/tag" {params :params}
        ;;save tags
		(json-response
        (model/save-tag-for-game params)
        ))
  (mp/wrap-multipart-params 
      (POST "/uploader" {params :params} (do (println params)(upload-file (:upload params) (:CKEditorFuncNum params)))))
  (form-authentication-routes (fn [_ c] (html c)) (auth/form-authentication-adapter))  
   ;;---------------------------------------------------------
  
  (route/files "/" {:root "public"})        
  (route/not-found "Page Not Found"))

(def webapp (-> app-routes
           (with-security security-policy form-authentication)              
           wrap-stateful-session
           mdw/wrap-request-logging))

;;(defn start-app []
;;  (future (run-jetty (var app) {:port 3000})))

(def app
  (handler/site webapp))

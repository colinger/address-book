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
(enlive/deftemplate layout "layout.html" [title content]
                    [#{:title :h1}] (enlive/content title)
                    [:div.content] (enlive/substitute (extract-body content)))
;;show all games
(defn show-all-games [things]
  (enlive/at (enlive/html-resource "show.html")
             [:div.post] (enlive/clone-for [thing things]
                                    [:a.title] (enlive/do->
                                                 (enlive/set-attr :href (str "/game/" (:id thing)))
                                                 (enlive/content (:name thing))) 
                                    [:div.content](enlive/html-content (summary/summary-post (:description thing) 300)))))
(defn show-a-game [game]
  (enlive/at (enlive/html-resource "game.html")
             [:span.title] (enlive/content (:name game))
             [:div.content] (enlive/html-content (:description game)))) 
;;
(defn admin-list-games [things]
  (enlive/at (enlive/html-resource "admin/edit.html")
             [:div.post] (enlive/clone-for [thing things]
                              [:a.title] (enlive/do->
                                           (enlive/set-attr :href (str "/admin/game/edit/" (:id thing)))
                                           (enlive/content (:name thing)))
                              [:div.content] (enlive/html-content (summary/summary-post (:description thing) 300)))))      
(defn edit-a-game [game]
  (enlive/at (enlive/html-resource "admin/deploy.html")
             [:input#id] (enlive/set-attr :value (:id game))
             [:div#titlewrap :input] (enlive/set-attr :value (:name game))
             [:div#postdivrich :textarea] (enlive/html-content (:description game))))                                     
;;
;;(defn json-response [data & [status]]
;;    {:status (or status 200)a
;;        :headers {"Content-Type" "application/json;charset=UTF-8"}
;;        :body (json/generate-string data)})
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
        (first (select model/games (where {:id gameId})))
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
   #"/permission-denied.*" :any
   #"/show.*" :any
   #"/game.*" :any
   #"/uploader.*" :any
   #"/admin.*" :user
   #"/" #{:any}])
;;(defn authenicate [request]
;;routes
(defroutes app-routes
  (GET "/" [] (layout "游戏必杀技 | 玩家 | 切磋" (show-all-games (things))))         
  ;;(GET "/show" [] (layout "游戏必杀技 | 玩家 | 交流" (show-all-games (things))))
  (POST "/admin/game" {params :params} (do (model/save-or-update-game params)
                                     (redirect-to "/admin")))
  (GET "/game/:id" [id] 
       (let [game (game-details id)]
         (layout (str (:name game) " | " "游戏必杀技") (show-a-game game))))
  (GET "/admin" [] (layout "后台管理" (admin-list-games (things))))
  (GET "/admin/game" [] 
       (if (= "unkown" (session-get :current-user "unknow"))
         (layout "后台管理-登录" login-home)
         (render (add-game-page))))
  (GET "/admin/game/edit/:id" [id]
       (let [game (game-details id)]
         (layout (str (:name game) " | " "游戏必杀技") (edit-a-game game))))
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

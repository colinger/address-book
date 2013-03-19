(ns address-book.service
  (:use [compojure.core])
  (:import (java.io File))
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clj-json.core :as json]
            [address-book.models :as model]
            [address-book.middleware :as mdw]
            [address-book.utils.string :as summary]
			[address-book.utils.number :as number]
            [net.cgrand.enlive-html :as enlive]
            (clojure.contrib [duck-streams :as ds])))
;;
(defn show-all-mobile-games [params]
  (let [cur-page (number/parse-int(get params :page "1"))
        things (model/all-mobile-games-pagination cur-page)
		games (rest things)
		totals (:cnt (first things))
		pages (int (+ 1 (quot totals 10.0)))]
  (println "mobile current page " cur-page " totals: " totals " pages " pages "type:手游")		
  (enlive/at (enlive/html-resource "show.html")
             [:div.post] (enlive/clone-for [game games]
                                    [:a.title] (enlive/do->
                                                 (enlive/set-attr :href (str "/game/" (:id game)))
                                                 (enlive/content (:name game))) 
									[:p.date] (enlive/html-content (str "<a href=\"#\">" "黄药师" "</a>" " 发布于 " (summary/date-format (:create_date game))))				
                                    [:p.detail](enlive/html-content (summary/summary-post (:description game) 120))
									[:a.more] (enlive/set-attr :href (str "/game/" (:id game))))
			 [:div.pagination] (cond (= pages cur-page) (enlive/html-content (str "<a style=\"display:block\" href=\"?page=" (- cur-page 1) "\" class=\"right prev\">上一页</a>"))
									 (= 1 cur-page) (enlive/html-content (str "<a style=\"display:block\" href=\" ?page=" (+ 1 cur-page) "\" class=\"right next\">下一页</a>"))	
									 (and (> cur-page 1) (< cur-page pages)) (enlive/html-content (str "<a style=\"display:block\" href=\" ?page=" (- cur-page 1) "\" class=\"right next\">下一页</a>" "<a style=\"display:block\" href=\"?page=" (+ 1 cur-page) "\" class=\"right prev\">上一页</a>"))
			 ))))				 
;;
(defn show-all-games [params]
  (let [cur-page (number/parse-int(get params :page "1"))
        things (model/all-desk-games-pagination cur-page)
		games (rest things)
		totals (:cnt (first things))
		pages (int (+ 1 (quot totals 10.0)))]
  (println "desk current page " cur-page " totals: " totals " pages " pages "type:桌游")		
  (enlive/at (enlive/html-resource "show.html")
             [:div.post] (enlive/clone-for [game games]
                                    [:a.title] (enlive/do->
                                                 (enlive/set-attr :href (str "/game/" (:id game)))
                                                 (enlive/content (:name game))) 
									[:p.date] (enlive/html-content (str "<a href=\"#\">" "黄药师" "</a>" " 发布于 " (summary/date-format (:create_date game))))				
                                    [:p.detail](enlive/html-content (summary/summary-post (:description game) 120))
									[:a.more] (enlive/set-attr :href (str "/game/" (:id game))))
			 [:div.pagination] (cond (= pages cur-page) (enlive/html-content (str "<a style=\"display:block\" href=\"?page=" (- cur-page 1) "\" class=\"right prev\">上一页</a>"))
									 (= 1 cur-page) (enlive/html-content (str "<a style=\"display:block\" href=\" ?page=" (+ 1 cur-page) "\" class=\"right next\">下一页</a>"))	
									 (and (> cur-page 1) (< cur-page pages)) (enlive/html-content (str "<a style=\"display:block\" href=\" ?page=" (- cur-page 1) "\" class=\"right next\">下一页</a>" "<a style=\"display:block\" href=\"?page=" (+ 1 cur-page) "\" class=\"right prev\">上一页</a>"))
			 ))))
;;
(defn search-all-games [params]
  (let [cur-page (number/parse-int(get params :page "1"))
        content (get params :q "")
        things (model/search-games-pagination content cur-page)
		games (rest things)
		totals (:cnt (first things))
		pages (int (+ 1 (quot totals 10.0)))]
  (println "current page " cur-page " totals: " totals " pages " pages )		
  (enlive/at (enlive/html-resource "show.html")
             [:div.post] (enlive/clone-for [game games]
                                    [:a.title] (enlive/do->
                                                 (enlive/set-attr :href (str "/game/" (:id game)))
                                                 (enlive/content (:name game))) 
									[:p.date] (enlive/html-content (str "<a href=\"#\">" "黄药师" "</a>" " 发布于 " (summary/date-format (:create_date game))))				
                                    [:p.detail](enlive/html-content (summary/summary-post (:description game) 120))
									[:a.more] (enlive/set-attr :href (str "/game/" (:id game))))
			 [:div.pagination] (cond (and (not= 1 pages) (= pages cur-page)) (enlive/html-content (str "<a style=\"display:block\" href=\"?q=" content "&page=" (- cur-page 1) "\" class=\"right prev\">上一页</a>"))
									 (and (= 1 cur-page) (not= pages cur-page)) (enlive/html-content (str "<a style=\"display:block\" href=\"?q=" content "&page=" (+ 1 cur-page) "\" class=\"right next\">下一页</a>"))	
									 (and (> cur-page 1) (< cur-page pages)) (enlive/html-content (str "<a style=\"display:block\" href=\"?q=" content "&page=" (- cur-page 1) "\" class=\"right next\">下一页</a>" "<a style=\"display:block\" href=\"?q=" content "&page=" (+ 1 cur-page) "\" class=\"right prev\">上一页</a>"))
			 ))))	
(ns address-book.config
  (:use korma.db))

(defdb mydb (mysql
              {:port 3306
               :host "localhost"
               :user "root"
               :password "colin123@go"
               :db "game_skill"}))

;;TYPE
(def CELLPHONE-GAME "手游") 
(def DESK-GAME "桌游")

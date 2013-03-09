(ns address-book.config
  (:use korma.db))

(defdb mydb (mysql
              {:port 3306
               :host "localhost"
               :user "root"
               :password "admin"
               :db "game_skill"}))

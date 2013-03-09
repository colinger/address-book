(ns address-book.model.game
  (:use address-book.config)
  (:use korma.core))

(defentity games
           (database mydb))

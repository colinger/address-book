(ns address-book.models
  (:use address-book.config)
  (:use korma.core))

(declare games tags)
;;tags
(defentity tags
  (many-to-many games :games2tags {:lfk :tags_id
                                   :rfk :games_id})
  (database mydb))
;;games
(defentity games
  (many-to-many tags :games2tags {:lfk :games_id
                                  :rfk :tags_id})
  (database mydb))
;;games_tags
(defentity games2tags
  (table :games2tags)
  (database mydb))
(defentity users
  (database mydb))
;;-----------------------------
;;tags
;;-----------------------------
(defn create-tag [name]
  (insert tags (values {:name name})))
(defn get-tag-by-name [name]
  (first (select tags (where {:name name}))))
(defn get-tag-id-by-name [name]
  (:id (get-tag-by-name name)))
(defn produce-tag-id [tag-name]
  (let [tag-id (get-tag-id-by-name tag-name)]
    (if (nil? tag-id)
      (:GENERATED_KEY (create-tag tag-name))
	  tag-id)))
(defn set-tag-for-game[game-id tag-name]
  (let [tag-id (produce-tag-id tag-name)] 
    (insert games2tags (values {:games_id game-id
                                :tags_id tag-id}))))
;;-----------------------------
;;games
;;-----------------------------
(declare create-game)
(declare update-game)
(defn save-or-update-game [attrs]
  (let [id (get attrs :id)]
    (if (nil? id)
      (create-game attrs)
      (update-game attrs))))
(defn create-game [attrs]
  (let [new-attrs (merge {:create_date (java.util.Date.) :visit_count 0} attrs)]
    (println new-attrs)
    (insert games (values new-attrs))))
(defn update-game [attrs]
  (let [game-id (:id attrs)]
    (update games 
            (set-fields {:name (:name attrs)
                         :description (:description attrs)})
            (where {:id [= game-id]}))))

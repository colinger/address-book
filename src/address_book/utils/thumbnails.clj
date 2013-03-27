(ns address-book.utils.thumbnails
	(:import (java.io File))
	(import [net.coobird.thumbnailator Thumbnails])
	(import [net.coobird.thumbnailator.geometry Positions]))
			 
(defn thumbnail [name]
	(let [image (Thumbnails/fromFilenames (java.util.ArrayList. [(str "public/images/" name)]))
        thumb (.size image 140 100)
        thumb (.crop thumb Positions/TOP_CENTER)
        ]
    (.toFile thumb (str "public/images/thumbnail/" "thumbnail_" name))))
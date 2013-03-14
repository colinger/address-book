(ns address-book.utils.string)

;;cut post content
(defn summary-post [content length]
    (if (= "" content)
      ""
      (let [res (.replaceAll (.replaceAll content "\\&[a-zA-Z]{1,10};" "") "<[^>]*>" "")
            res (.replaceAll res "[(/>)<]" "")
            len (.length res)]
        (if (<= len length)
          res
          (str (.substring res 0 length)"......")))))
;;
(defn date-format [time]
	(.format (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm") time))
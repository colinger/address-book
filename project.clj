(defproject address_book "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "1.1.5"]
                 [ring-json-params "0.1.0"]
                 [clj-json "0.5.3"]
                 [mysql/mysql-connector-java "5.1.21"]
                 [hiccup "1.0.2"]
                 [isaacsu/sandbar "0.4.1"]
;;                 [sandbar/sandbar "0.4.0-SNAPSHOT"]
                 [korma "0.3.0-RC4"]
                 [enlive "1.1.1"]
                 [clj-time "0.4.4"]
;;                 [ring/ring-jetty-adapter "0.2.5"]
;;                 [ring/ring-devel "0.2.5"]
                  ]
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler address-book.handler/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]]}})

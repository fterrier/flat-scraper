(ns flat-scraper.core
  (:require [clojure.string :as str]
            [net.cgrand.enlive-html :as html]
            [org.httpkit.client :as http]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


(defn get-listings-with-duration []
  )

;; (defn -main
;;   []
;;   (let [titles (extract-titles (get-dom))]
;;     (println titles)))

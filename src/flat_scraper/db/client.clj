(ns flat-scraper.db.client
  (:require [flat-scraper.db.datastore :as datastore]
            [clj-time.coerce :as c]))


(defn- serialize-listings [listings]
  (pr-str (map #(assoc % :date-scraped (c/to-long (:date-scraped %))) 
               listings)))

(defn- deserialize-listings [listings-str]
  (let [listings (read-string listings-str)]
    (map #(assoc % :date-scraped (c/from-long (:date-scraped %))) listings)))

(defn retrieve-listings [db provider]
  (let [data (datastore/read db (str "listings:" (name provider)))]
    (when data
      (deserialize-listings data))))

(defn save-listings [db provider listings]
  ;; TODO not very efficient this
  (datastore/write db (str "listings:" (name provider)) 
                   (serialize-listings listings)))

(comment 
  (save-listings (flat-scraper.db.datastore/create-db {}) :test [{:id 123 :date-scraped (clj-time.core/now)}])
  (retrieve-listings (flat-scraper.db.datastore/create-db {}) :test))

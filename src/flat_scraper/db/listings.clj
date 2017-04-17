(ns flat-scraper.db.listings
  (:require [clj-time.coerce :as c]
            [db.datastore :as datastore]))

(defn- serialize-listings [listings]
  (pr-str (map #(assoc % :date-scraped (c/to-long (:date-scraped %))) 
               listings)))

(defn- deserialize-listings [listings-str]
  (let [listings (read-string listings-str)]
    (map #(assoc % :date-scraped (c/from-long (:date-scraped %))) listings)))

(defn retrieve-listings [db provider listing-hash]
  (let [data (datastore/read db (str "listings:" (name provider) ":" listing-hash))]
    (when data
      (deserialize-listings data))))

(defn save-listings [db provider listing-hash listings]
  ;; TODO not very efficient this
  (datastore/write db (str "listings:" (name provider) ":" listing-hash) 
                   (serialize-listings listings)))

(comment 
  (save-listings (db.datastore/create-db {}) :test "abc" [{:id 123 :date-scraped (clj-time.core/now)}])
  (retrieve-listings (db.datastore/create-db {}) :test "abc"))

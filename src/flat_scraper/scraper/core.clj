(ns flat-scraper.scraper.core
  (:require [clj-time.core :as t]
            [flat-scraper.db.client :as client]
            [flat-scraper.google.distance :as google-distance]
            [flat-scraper.scraper.providers.zoopla :as zoopla]))

(defn- merge-duration [listing] 
  (let [{:keys [result]} @(google-distance/retrieve-duration "AIzaSyCCt4snXtT3ncLGvg9e-MNwNtPoJguNY_g" (:address listing) "103 Wigmore Street, London")]
    (merge listing result)))

(defn- merge-listings 
  "Merges a new listing vector with an existing one, 
  conserving the order of both lists."
  [existing new]
  (let [new-ids (into #{} (map :id new))
        existing-without-new (remove #(contains? new-ids (:id %)) existing)]
    (concat new existing-without-new)))

(defn- add-date [listing date]
  (assoc listing :date-scraped date))

(defn get-zoopla-fresh-listings-and-save [db]
  (let [now (t/now)
        new-listings (->> (zoopla/get-zoopla-listings)
                          (map #(add-date % now)))
        existing-listings (client/retrieve-listings db :zoopla)
        listings-to-save (take 100 (merge-listings existing-listings new-listings))]
    (client/save-listings db :zoopla listings-to-save)))

(comment
  (get-zoopla-fresh-listings-and-save 
   (flat-scraper.db.datastore/create-db {})))

(comment
  (get-listings-with-duration))

;(retrieve-duration "AIzaSyCCt4snXtT3ncLGvg9e-MNwNtPoJguNY_g" "Bethnal Green Road, London E2" "103 Wigmore Street, London")

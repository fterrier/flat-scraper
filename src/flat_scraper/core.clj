(ns flat-scraper.core
  (:require [flat-scraper.db.client :as client]
            [flat-scraper.google.distance :as google-distance]
            [flat-scraper.scraper.zoopla :as zoopla]
            [clj-time.core :as t]))

(defn- merge-duration [listing] 
  (let [{:keys [result]} @(google-distance/retrieve-duration "AIzaSyCCt4snXtT3ncLGvg9e-MNwNtPoJguNY_g" (:address listing) "103 Wigmore Street, London")]
    (merge listing result)))

(defn- merge-listings 
  "Merges a new listing vector with an existing one, 
  conserving the order of both lists."
  [existing new]
  (let [existing-ids (into #{} (map :id existing))
        new-without-existing (remove #(contains? existing-ids (:id %)) new)]
    (concat new-without-existing existing)))

(defn- add-date [listing date]
  (assoc listing :date-scraped date))

(defn get-zoopla-fresh-listings-and-save [db]
  (let [new-listings (zoopla/get-zoopla-listings)
        existing-listings (->> (client/retrieve-listings db :zoopla)
                               (map #(add-date % (t/now))))]
    (client/save-listings db :zoopla (merge-listings existing-listings new-listings))))

(comment
  (get-zoopla-fresh-listings-and-save 
   (flat-scraper.db.datastore/create-db {})))

(comment
  (get-listings-with-duration))

;(retrieve-duration "AIzaSyCCt4snXtT3ncLGvg9e-MNwNtPoJguNY_g" "Bethnal Green Road, London E2" "103 Wigmore Street, London")


;; (defn -main
;;   []
;;   (let [titles (extract-titles (get-dom))]
;;     (println titles)))

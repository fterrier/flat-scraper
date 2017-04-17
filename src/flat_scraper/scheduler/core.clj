(ns flat-scraper.scheduler.core)

(defn run []
  ;; 1. get cursor from redis of all client urls
  ;; -- in a pipeline with threads
  ;; 2. call flat-scraper.core/retrieve-listings client url
  ;; 3. notify client
  ;; 4. mark each as notified
)

;; (defn- merge-duration [listing] 
;;   (let [{:keys [result]} @(google-distance/retrieve-duration "AIzaSyCCt4snXtT3ncLGvg9e-MNwNtPoJguNY_g" (:address listing) "103 Wigmore Street, London")]
;;     (merge listing result)))

;; (defn- merge-listings 
;;   "Merges a new listing vector with an existing one, 
;;   conserving the order of both lists."
;;   [existing new]
;;   (let [new-ids (into #{} (map :id new))
;;         existing-without-new (remove #(contains? new-ids (:id %)) existing)]
;;     (concat new existing-without-new)))

;; (defn handle-scrape-request [db config {:keys [url client]}]
;;   (let [now (t/now)
;;         new-listings (->> (zoopla/get-zoopla-listings)
;;                           (map #(add-date % now)))
;;         existing-listings (client/retrieve-listings db :zoopla)
;;         listings-to-save (take 100 (merge-listings existing-listings new-listings))]
;;     (client/save-listings db :zoopla listings-to-save)))

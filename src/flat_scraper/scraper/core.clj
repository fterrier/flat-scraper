(ns flat-scraper.scraper.core
  (:require [clj-time.core :as t]
            [flat-scraper.db.listings :as client]
            [flat-scraper.google.distance :as google-distance]
            [flat-scraper.scraper.providers.zoopla :as zoopla]))

(defn- add-date [listing date]
  (assoc listing :date-scraped date))

(defn- get-provider [providers url]
  ;; TODO finish implementing
  {:provider :zoopla
   :scrape-fn zoopla/get-zoopla-listings})

(defn scrape 
  "Given a list of providers and a url, scrapes the url and return a list of 
  listings and the given provider or an error"
  [providers date url]
  (let [{:keys [provider scrape-fn]} (get-provider providers url)
        listings (->> url
                      (zoopla/get-zoopla-listings)
                      (map #(add-date % date)))]
    ;; TODO error case
    [{:provider provider
      :listings listings} nil]))

(comment
  (scrape [] (t/now)
   "http://www.zoopla.co.uk/to-rent/property/london/islington/?beds_min=1&include_shared_accommodation=false&price_frequency=per_month&price_max=1750&q=Islington%2C%20London&radius=3&search_source=refine&page_size=25&pn=1&view_type=list"))

;; (comment
;;   (get-listings-with-duration))

;(retrieve-duration "AIzaSyCCt4snXtT3ncLGvg9e-MNwNtPoJguNY_g" "Bethnal Green Road, London E2" "103 Wigmore Street, London")

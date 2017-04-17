(ns flat-scraper.scraper.providers.zoopla
  (:require [clojure.string :as str]
            [net.cgrand.enlive-html :as html]
            [org.httpkit.client :as http]))

(defn- get-dom [url]
  (html/html-snippet
   (:body @(http/get url {:insecure? true}))))
 
(defn- extract-results [dom]
  (html/select dom [:.listing-results-wrapper]))

(def ^:dynamic *price-selector* [:.listing-results-price.text-price])
(def ^:dynamic *address-selector* [:a.listing-results-address])
(def ^:dynamic *title-selector* [:h2.listing-results-attr :> :a])
(def ^:dynamic *description-selector* [:nearby_stations_schools ])
(def ^:dynamic *phone-selector* [:listing-results-footer :.icon-phone])
(def ^:dynamic *image-selector* [:a.photo-hover :img])

(defn- to-int [str]
  (try
    (Integer/parseInt str)
    (catch IllegalArgumentException e nil)))

(defn- clean-str [str]
  (-> str
      (str/replace #"\n" "")
      (str/trim)))

(defn- parse-price [price-str]
  (let [matcher (re-matcher #"£([\d,]+) p+cm\s+\(£(\d+) p+w\)" price-str)
        [_ pcm pw] (re-find matcher)
        remove-comma (fn [str] (when str (str/replace str "," "")))]
    {:text price-str
     :weekly {:price (-> pw remove-comma to-int)
              :per-person (str/includes? price-str "pppw")}
     :monthly {:price (-> pcm remove-comma to-int)}}))

(comment
  (parse-price "£1,700 pcm  (£162 pw)")
  (parse-price "£1,300 pcm  (£150 pppw)"))

(defn- relevant-infos [node]
  (let [id (first (html/attr-values (first (html/select [node] *image-selector*)) :data-ajax))
        price (html/text (first (html/select [node] *price-selector*)))
        address (html/text (first (html/select [node] *address-selector*)))
        title (html/text (first (html/select [node] *title-selector*)))
        description (html/text (first (html/select [node] *description-selector*)))
        phone (html/text (first (html/select [node] *phone-selector*)))
        url (first (html/attr-values (first (html/select [node] *price-selector*)) :href))
        image (first (html/attr-values (first (html/select [node] *image-selector*)) :src))
        result [(to-int id) (parse-price (clean-str price)) (clean-str address) (clean-str title) (clean-str description) (clean-str phone) url image]]
    (zipmap [:id :price :address :title :description :phone :url :image] result)))

(defn get-zoopla-listings [url]
  (->> url
       (get-dom)
       extract-results 
       (map relevant-infos)))

(comment 
  (get-zoopla-listings "http://www.zoopla.co.uk/to-rent/property/london/islington/?beds_min=1&include_shared_accommodation=false&price_frequency=per_month&price_max=1750&q=Islington%2C%20London&radius=3&search_source=refine&page_size=25&pn=1&view_type=list"))

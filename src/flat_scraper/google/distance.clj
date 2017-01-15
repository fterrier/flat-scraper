(ns flat-scraper.google.distance
  (:require [cheshire.core :as json]
            [org.httpkit.client :as http]))

(defn get-uri [key origin destination]
  (let [url "https://maps.googleapis.com/maps/api/distancematrix/json"
        params {:mode "transit"
                :origins origin
                :destinations destination
                :key key
                :arrival_time 1484555400}]
    {:url url
     :query-params params}))

(defn extract-duration [{:keys [result]}]
  (let [duration (-> result :rows first :elements first :duration :text)]
    {:duration duration}))

(comment
  (extract-duration {:result {:destination_addresses ["103 Wigmore St, Marylebone, London W1U 1QS, UK"], :origin_addresses ["Bethnal Green Rd, London E2, UK"], :rows [{:elements [{:distance {:text "8.4 km", :value 8385}, :duration {:text "30 mins", :value 1771}, :status "OK"}]}], :status "OK"}}))

(defn retrieve-matrix [key origin destination]
  (let [{:keys [url query-params]} (get-uri key origin destination)]
    @(http/get url {:query-params query-params}
               (fn [{:keys [status headers body error opts]}]
                 (if body 
                   {:result (json/parse-string body true)}
                   {:error error})))))

(comment
  (retrieve-matrix "AIzaSyCCt4snXtT3ncLGvg9e-MNwNtPoJguNY_g" "Bethnal Green Road, London E2" "103 Wigmore Street, London"))

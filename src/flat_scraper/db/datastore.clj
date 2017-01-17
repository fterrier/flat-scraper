(ns flat-scraper.db.datastore
  (:require [taoensso.carmine :as car :refer (wcar)]))

(defn create-db [config]
  config)

(defn ping [db]
  (wcar db (car/ping)))

(defn read [db key]
  (wcar db (car/get key)))

(defn write [db key value]
  (wcar db (car/set key value)))

(comment
  (let [config {:pool {} :spec {:host "127.0.0.1" :port 6379}}
        db (create-db config)]
    (write db "TEST" (rand))
    (read db "TEST")))

(comment
  (ping (create-db  {:pool {} :spec {:host "127.0.0.1" :port 6379}})))

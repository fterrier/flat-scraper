(ns app.config
  (:require [mount.core :refer [defstate]]))

(defstate config :start 
  {:telegram {:key "TBD"
              :host "TBD"}})

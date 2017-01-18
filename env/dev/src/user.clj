(ns user
  (:require [clojure.tools.namespace.repl :as tn]
            ;[app.scheduler :as scheduler]
            [app.server :as server]
            [mount.core :as mount]))

(def config 
  {:telegram {:key "273446564:AAFGqT1WNpc9ZgRZK5gDaQp8CF4Tz0W3fro"
              :host "970b119a.ngrok.io"}})

(defn go []
  (-> (mount/swap {#'app.config/config config})
      (mount/with-args {:port 8000})
      mount/start)
  :ready)

(defn stop []
  (mount/stop))

(defn reset []
  (mount/stop)
  (tn/refresh :after 'user/go))

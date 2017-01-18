(ns app.server
  (:require [app
             [bot :as bot]
             [telegram :as telegram]]
            [compojure
             [core :refer [routes]]
             [route :as route]]
            [mount.core :as mount :refer [defstate]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [telegram.handler :as telegram-handler]))

(defn- not-found [_] 
  {:status 404
   :body   {:message "not found"}})

(defn start-www [{:keys [port]}]
  (println "starting on port" port)
  (-> (routes 
       (telegram-handler/start-and-get-routes telegram/telegram-app bot/bot)
       (route/not-found not-found))
      (wrap-defaults api-defaults)
      (run-jetty {:join? false :port port})))

(defstate web-server
  :start (start-www (mount/args))
  :stop (.stop web-server))

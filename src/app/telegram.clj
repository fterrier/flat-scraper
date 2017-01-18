(ns app.telegram
  (:require [app.config :as config]
            [mount.core :refer [defstate]]
            [telegram.handler :as telegram-handler]))

(defstate telegram-app
  :start   (let [{:keys [telegram]} config/config
                 telegram-key       (:key telegram)
                 telegram-host      (:host telegram)]
             (telegram-handler/create-telegram-app telegram-key telegram-host)))

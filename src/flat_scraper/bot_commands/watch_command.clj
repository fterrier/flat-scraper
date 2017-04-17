(ns flat-scraper.bot-commands.watch-command
  (:require [clojure.tools.logging :as log]))

(defn- get-message []
  {:message "watchie watchie" 
   :options {:parse-mode :markdown}})

(defn handle-watch* [db command send-to-user-fn]
  (log/info "Handling watch command " command)
  (send-to-user-fn {:text (get-message)}))

(defn create-watch-command [db]
  (partial handle-watch* db))

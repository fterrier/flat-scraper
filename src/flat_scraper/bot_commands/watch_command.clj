(ns flat-scraper.bot-commands.watch-command)

(defn- get-message []
  {:message "watchie watchie" 
   :options {:parse-mode :markdown}})

(defn handle-watch* [db {:keys [user]} send-to-user-fn]
  (send-to-user-fn {:text (get-message)}))

(defn create-watch-command [db]
  (partial handle-watch* db))


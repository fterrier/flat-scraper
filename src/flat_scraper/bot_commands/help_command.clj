(ns flat-scraper.bot-commands.help-command)

(defn- get-message []
  {:message "Welcome to FindFlatBot. The following commands are available:.\n
  - /watch <url> : watch an url for new flats. Currently only zoopla is supported.
  - /help : show this message" 
   :options {:parse-mode :markdown}})

(defn handle-help* [{:keys [user]} send-to-user-fn]
  (send-to-user-fn {:text (get-message)}))

(defn create-help-command []
  (partial handle-help*))

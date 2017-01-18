(ns app.bot
  (:require [app.telegram :as telegram]
            [barbotte
             [bot :as bot]
             [matcher :as matcher]]
            [clojure.core.async :refer [>!!]]
            [flat-scraper.bot-commands.help-command :as help-command]
            [mount.core :refer [defstate]]
            [ring.util.response :refer [response]]
            [telegram.handler :as telegram-handler]))

(defn- create-help-command []
  (help-command/create-help-command))

(defn- get-message [{:keys [error success] :as response}]
  (cond
    (= :command-not-found error) 
    {:message "Sorry, did not understand this. Use /help to get help."}
    :else {:message (str response)}))

(defn- send-to-user [get-channel-fn client {:keys [text] :as response} command]
  (>!! (get-channel-fn client)
       (if (nil? text)
         (get-message response)
         text)))

(defn- start-bot [get-channel-fn]
  (let [commands   [{:match-fn (matcher/match-or 
                                (matcher/match-first "/help")
                                (matcher/match-first "/start"))
                     :handle-fn (create-help-command)}]
        bot        (bot/create-bot commands)]
    (fn [data client]
      (bot data (partial send-to-user get-channel-fn client)))))

(defstate bot 
  :start (start-bot 
          (partial telegram-handler/get-channel telegram/telegram-app)))

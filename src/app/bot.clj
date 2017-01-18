(ns app.bot
  (:require [app.telegram :as telegram]
            [barbotte
             [bot :as bot]
             [matcher :as matcher]]
            [clojure.core.async :refer [>!!]]
            [flat-scraper.bot-commands
             [help-command :as help-command]
             [watch-command :as watch-command]]
            [mount.core :refer [defstate]]
            [ring.util.response :refer [response]]
            [telegram.handler :as telegram-handler]))

(defn- create-help-command []
  (help-command/create-help-command))

(defn- create-watch-command [db]
  (watch-command/create-watch-command db))

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

(defn- start-bot [db get-channel-fn]
  (let [commands   [{:match-fn (matcher/match-or 
                                (matcher/match-first "/help")
                                (matcher/match-first "/start"))
                     :handle-fn (create-help-command)}
                    {:match-fn (matcher/match-first "/watch")
                     :handle-fn (create-watch-command db)}]
        bot        (bot/create-bot commands)]
    (fn [data client]
      (bot data (partial send-to-user get-channel-fn client)))))

(defstate bot 
  :start (start-bot nil
          (partial telegram-handler/get-channel telegram/telegram-app)))

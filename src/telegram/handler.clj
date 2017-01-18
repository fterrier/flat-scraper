(ns telegram.handler
  (:require [clojure.core.async :refer [<! >!! chan go go-loop]]
            [clojure.tools.logging :as log]
            [compojure.core :refer [POST routes]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [telegram.client :as client]))

(defn- register-webhook [webhook-domain bot-id]
  (log/info "Registering telegram bot" webhook-domain bot-id)
  @(client/set-webhook bot-id (str "https://" webhook-domain "/telegram")))

(defn- start-handler-loop [message-ch handler]
  (go-loop []
    (when-let [{:keys [body client] :as message} (<! message-ch)]
      (try 
        (handler body client)
        (catch Exception e (log/warn e "Error handling message" message)))
      (recur))))

(defn- start-client-loop [client-ch bot-id chat-id]
  (go-loop []
    (when-let [{:keys [message options]} (<! client-ch)]
      @(client/send-message bot-id chat-id message options)
      (recur))))

(defn- client-channel* [chat-id registry bot-id]
  (when-not (get @registry chat-id)
    (let [client-ch (chan)]
      (swap! registry assoc chat-id client-ch)
      (start-client-loop client-ch bot-id chat-id)))
  (get @registry chat-id))

(defn- event-msg-handler* [message-ch registry bot-id]
  (fn [{:as ev-msg :keys [body]}]
    (log/debug "Got message from telegram client" body)
    (let [{{:keys [text chat from]} :message} body]
      (>!! message-ch {:body body :client (:id chat)}))))

(defn- app-routes [msg-handler]
  (routes
   (-> (POST "/telegram" request (msg-handler request) {:body "ok"})
       (wrap-json-body {:keywords? true :bigdecimals? true})
       wrap-json-response)))

(defn get-channel [app client]
  (let [{:keys [registry bot-id]} app]
    (client-channel* client registry bot-id)))

(defn create-telegram-app [bot-id webhook-domain]
  "The handler is a function that takes the following arguments:
   - body : the original request body
   - client : the client which can be used on get-channel to retrieve the channel"
  (log/info "Starting telegram app with " bot-id webhook-domain)
  (let [registry (atom {})]
    (go (when webhook-domain (register-webhook webhook-domain bot-id)))
    {:registry registry
     :bot-id bot-id}))

(defn start-and-get-routes [app handler]
  ;; TODO not optimal this
  (let [{:keys [registry bot-id]} app
        message-ch (chan 10)
        msg-handler (event-msg-handler* message-ch registry bot-id)]
    (go (start-handler-loop message-ch handler))
    (app-routes msg-handler)))

;; TODO function to stop this

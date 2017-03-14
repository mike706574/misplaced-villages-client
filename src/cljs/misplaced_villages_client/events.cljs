(ns misplaced-villages-client.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [misplaced-villages.game :as game]
            [misplaced-villages-client.comms :as comms]))

(def api-uri "http://goose:8000")

(rf/reg-event-db
 :initialize
 (fn [_ _]
   {:app/game nil
    :app/player ""
    :app/screen :mike-or-abby
    :app/loading? false}))

(rf/reg-event-fx
 :play
 (fn [{db :db} [_ player]]
   {:http-xhrio {:method :get
                 :uri (str api-uri "/game/1")
                 :response-format (ajax/raw-response-format)
                 :headers {"player" player}
                 :on-success [:process-game]
                 :on-failure [:http-error]}
    :db (merge db {:app/player player
                   :app/loading? true})}))

(rf/reg-event-db
 :process-game
 (fn [db [_ response]]
   (let [game (cljs.reader/read-string response)]
     (comms/connect)
     (merge db {:app/game game}))))

(rf/reg-event-db
 :connect
 (fn [db [_ chan]]
   (println "Connected!")
   (set! (.-onmessage chan) (fn [message] (rf/dispatch [:message message])))
   (merge db {:app/screen :game
              :app/loading? false
              :app/socket chan})))

(rf/reg-event-db
 :http-error
 (fn [db [_ response]]
   (println (str "HTTP error: " response))
   (merge db {:app/screen :error
              :app/error-message (:status-text response)})))

(rf/reg-event-db
 :player-change
 (fn [db [_ player]]
   (println (str "Changing player to " (str player)))
   (assoc db :app/player player)))

(rf/reg-event-db
 :message
 (fn [db [_ message]]
   (println (str "Message: " message))
   (merge db {:app/screen :game})))

(rf/reg-event-db
 :error
 (fn [db [_ message]]
   (println (str "WS error: " message))
   (merge db {:app/screen :error
              :app/error-message message})))

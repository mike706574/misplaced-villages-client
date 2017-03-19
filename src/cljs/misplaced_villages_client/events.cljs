(ns misplaced-villages-client.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [misplaced-villages.game :as game]
            [misplaced-villages.player :as player]
            [misplaced-villages.move :as move]
            [taoensso.timbre :as log :refer-macros [debug]]))

(defn handle-socket-event
  [event]
  (let [data (.-data event)
        message (cljs.reader/read-string data)]
    (if-let [status (::game/status message)]
      (rf/dispatch [status message])
      (rf/dispatch [:error (str "Invalid message: " message)]))))

(defn play
  [player]
  (if-let [socket (js/WebSocket. "ws://goose:8001/game-websocket")]
    (do (set! (.-onopen socket) #(rf/dispatch [:socket-open socket player]))
        {:app/socket socket
         :app/player player
         :app/status-message "Waiting for socket to open."
         :app/loading? true})
    {:app/screen :error
     :app/error-message "Failed to create socket."}))

(rf/reg-event-db
 :socket-open
 (fn [db [_ socket player]]
   (set! (.-onmessage socket) handle-socket-event)
   (.send socket (pr-str {::player/id player
                          ::game/id "1"}))
   (merge db {:app/status-message "Socket open."})))

(rf/reg-event-db
 :initialize
 (fn [_ _]
   {:app/screen :player-selection
    :app/loading? false
    :app/status-message "Selecting player."}))

(rf/reg-event-db
 :play
 (fn [db [_ player]]
   (merge db (play player))))

(rf/reg-event-db
 :connected
 (fn [db [_ message]]
   (merge db
          {:app/game (::game/state message)
           :app/loading? false
           :app/screen :game
           :app/card nil
           :app/destination :expedition
           :app/source :draw-pile
           :app/status-message "Connected."})))

(rf/reg-event-db
 :player-connected
 (fn [db [_ {player ::player/id}]]
   (assoc db :app/status-message (str player " connected."))))

(rf/reg-event-db
 :taken
 (fn [db [_ {player ::player/id
             game ::game/state}]]
   (merge db {:app/game game
              :app/move-message nil
              :app/status-message (str player " took a turn.")})))

(rf/reg-event-db
 :too-low
 (fn [db [_ {player ::player/id
             game ::game/state}]]
   (assoc db :app/move-message (str "Tow low!"))))

(rf/reg-event-db
 :invalid-move
 (fn [{player :app/player :as db} [_ {action-player ::player/id}]]
   (when (= player action-player)
       (assoc db :app/move-message (str "Invalid move!")))))

(rf/reg-event-db
 :expedition-underway
 (fn [{player :app/player :as db} [_ {action-player ::player/id}]]
   (when (= player action-player)
       (assoc db :app/move-message (str "Expedition already underway!!")))))

(rf/reg-event-db
 :player-change
 (fn [db [_ player]]
   (assoc db :app/player player)))

(rf/reg-event-db
 :destination-change
 (fn [db [_ destination]]
   (assoc db :app/destination destination)))

(rf/reg-event-db
 :source-change
 (fn [db [_ source]]
   (assoc db :app/source source)))

(rf/reg-event-db
 :card-change
 (fn [db [_ card]]
   (println (str "Changing card to " card))
   (assoc db :app/card card)))

(rf/reg-event-db
 :move
 (fn [db [_]]
   (let [{:keys [:app/socket
                 :app/card
                 :app/destination
                 :app/source
                 :app/player]} db
         move (move/move player
                         card
                         destination source)]
     (println "Sending move:" move)
     (.send socket (pr-str move))
     (merge db {:app/card nil
                :app/destination :expedition
                :app/source :draw-pile})
     db)))

(rf/reg-event-db
 :error
 (fn [db [_ message]]
   (println (str "Error: " message))
   (merge db {:app/screen :error
              :app/error-message message})))

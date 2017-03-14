(ns misplaced-villages-client.comms
  (:require [re-frame.core :as rf]
            [taoensso.timbre :as log :refer-macros [debug]]))

(defn connect
  []
  (log/debug (str "Connecting..."))
  (if-let [chan (js/WebSocket. "ws://goose:8000/game-websocket")]
    (rf/dispatch [:connect chan])
    (rf/dispatch [:error "Connection failed."])))

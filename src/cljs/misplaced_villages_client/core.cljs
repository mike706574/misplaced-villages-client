(ns misplaced-villages-client.core
  (:require [clojure.string :as str]
            [cljs.pprint :refer [pprint]]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]))

;; -- Development --------------------------------------------------------------
(enable-console-print!)

;; -- Event Dispatch -----------------------------------------------------------

(def api-uri "http://goose:8000")

;; -- Event Handlers -----------------------------------------------------------

(defn game-request []
  {:method          :get
   :uri             (str api-uri "/game")
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success      [:process-game]
   :on-failure      [:handle-game-failure]})

(defn initialize [_ _]
  (println "Initializing.")
  {:http-xhrio (game-request)
   :db {:status :loading}})

(rf/reg-event-fx :initialize initialize)

(defn fetch-game
  [{db :db} _]
  {:http-xhrio (game-request)
   :db (assoc db :status :loading)})

(rf/reg-event-fx :fetch-game fetch-game)

(rf/reg-event-db
 :process-game
 (fn [db [_ response]]
;;   (println "Processing movies.")
   (let [game (js->clj response)]
     (assoc db
            :status :ok
            :game game))))

(rf/reg-event-db
 :handle-game-failure
 (fn [db [_ response]]
   (merge db {:status :error
              :error-message (:status-text response)})))

;; -- Query  -------------------------------------------------------------------

(rf/reg-sub
 :game-state
 (fn [db _]
   (select-keys db [:status :error-message])
   (:status db)))

(rf/reg-sub
  :game
  (fn [db _]
    (:game db)))


;; -- View Functions -----------------------------------------------------------

(defn game-display
  []
  (let [{:keys [rounds players]} @(rf/subscribe [:game])
        round-number (count rounds)
        round (last rounds)]
    [:div
     "WHOA"
     [:span (str "Round #" round-number)]
     [:span (str "Players: " (str/join ", " players))]]))

(defn ui
  []
  (let [{:keys [status error-message]} @(rf/subscribe [:game-state])]
    (case status
        :loading [:div "Loading..."]
        :error [:div (str "Error: " error-message)]
        [game-display])))

;; -- Entry Point -------------------------------------------------------------

(defn ^:export run
  []
  (rf/dispatch-sync [:initialize])
  (reagent/render [ui] (js/document.getElementById "app")))

(ns misplaced-villages-client.views
  (:require [clojure.string :as str]
            [cljs.pprint :refer [pprint]]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [misplaced-villages.game :as game]))

(defn button
  [label on-click]
  [:input.btn.btn-default
   {:type "button"
    :value label
    :on-click  on-click}])

(defn login
  []
  (let [player @(rf/subscribe [:player])]
    (println player)
    [:div
     [:input.form-control
      {:id "player"
       :placeholder "Player"
       :type "text"
       :value player
       :on-change #(rf/dispatch [:player-change (-> % .-target .-value)])}]
     [button "Play" #(rf/dispatch [:play player])]]))

(defn mike-or-abby
  []
  (let [player @(rf/subscribe [:player])]
    [:div
     [button "Abby" #(rf/dispatch [:play "Abby"])]
     [button "Mike" #(rf/dispatch [:play "Mike"])]]))

(defn game
  []
  (let [player @(rf/subscribe [:player])
        loading? @(rf/subscribe [:loading?])
        {:keys [::game/opponent ::game/turn ::game/hand] :as state} @(rf/subscribe [:game])
        turn? (= player turn)]
    [:div
     [:h3 "Game"]
     [:p (str "You are " player ".")]

     [:p (str "It's " (if turn? "your" (str opponent "'s")) " turn.")]
     [:ul
      (map-indexed
       (fn [index card] [:li {:key index} (button (str card) identity)])
       hand)]]))

(defn error
  []
  [:div
   [:h1 "Error!"]
   [:p @(rf/subscribe [:error-message])]])

(defn app
  []
  (let [loading?  @(rf/subscribe [:loading?])
        screen @(rf/subscribe [:screen])]
    [:div
     [:p (if loading? "Loading!" "OK!")]
     (case screen
       :login [login]
       :mike-or-abby [mike-or-abby]
       :game [game]
       :error [error])]))

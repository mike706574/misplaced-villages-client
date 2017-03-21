(ns misplaced-villages-client.views
  (:require [clojure.string :as str]
            [cljs.pprint :refer [pprint]]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [misplaced-villages.game :as game]
            [misplaced-villages.card :as card]
            [misplaced-villages.player :as player]))

(defn button
  [label on-click]
  [:input.btn.btn-default
   {:type "button"
    :value label
    :on-click  on-click}])

(defn player-selection
  []
  (let [player @(rf/subscribe [:player])]
    [:div
     [button "Abby" #(rf/dispatch [:play "Abby"])]
     [button "Mike" #(rf/dispatch [:play "Mike"])]]))

(defn active-hand-view
  [hand]
  (let [selected-card @(rf/subscribe [:card])]
    [:ul.no-space
     (map-indexed
      (fn [index card]
        [:li
         {:key index
          :style {"display" "inline"}}
         [:input {:type "radio"
                  :checked (= selected-card card)
                  :on-change #(rf/dispatch [:card-change card])}]
         [:span
          {:class (name (::card/color card))}
          (card/str-card card)]])
      hand)]))

(defn inactive-hand-view
  [hand]
  [:ul.no-space
   (map-indexed
    (fn [index card]
      [:li
       {:key index
        :style {"display" "inline"}}
       [:span
        {:class (name (::card/color card))}
        (card/str-card card)]])
    hand)])

(defn stack-table
  [stacks]
  (letfn [(th [[color _]]
            [:th {:key color :class (name color)} (name color)])
          (td [row [color cards]]
            (if-let [card (get cards row)]
              [:td {:key color
                    :class (name color)} (card/str-card card)]
              [:td {:key color} ""]))
          (tr [row]
            [:tr
             {:key row}
             (map (partial td row) stacks)])]
      (let [max-count (->> (map (comp count val) stacks)
                           (apply max))
            keys (keys stacks)]
        [:table
         [:thead [:tr (map th stacks)]]
         [:tbody (map tr (range 0 max-count))]])))

(defn destination-view
  []
  (let [destination @(rf/subscribe [:destination])]
    [:ul.no-space
     [:li {:key :play
           :style {"display" "inline"}}
      [:input {:type "radio"
               :checked (= destination :expedition)
               :on-change #(rf/dispatch [:destination-change :expedition])}]
      [:span "expedition"]]
     [:li {:key :discard
           :style {"display" "inline"}}
      [:input {:type "radio"
               :checked (= destination :discard-pile)
               :on-change #(rf/dispatch [:destination-change :discard-pile])}]
      [:span.black "discard"]]]))

(defn source-view
  [available-discards]
  (println "AD:" available-discards)
  (let [source @(rf/subscribe [:source])]
    [:ul.no-space
     [:li {:key :draw-pile
           :style {"display" "inline"}}
      [:input {:type "radio"
               :checked (= source :draw-pile)
               :on-change #(rf/dispatch [:source-change :draw-pile])}]
      [:span.black "draw"]]
     (map
      (fn [card]
        (let [color (::card/color card)]
          [:li
           {:key color
            :style {"display" "inline"}}
           [:input {:type "radio"
                    :checked (= source color)
                    :on-change #(rf/dispatch [:source-change color])}]
           [:span
            {:class (name color)}
            (card/str-card card)]]))
      available-discards)]))

(defn game
  []
  (println "Rendering game!")
  (let [player @(rf/subscribe [:player])
        loading? @(rf/subscribe [:loading?])
        {:keys [::game/opponent
                ::game/turn
                ::player/hand
                ::player/expeditions
                ::game/available-discards
                ::game/cards-remaining
                ::game/opponent-expeditions] :as game} @(rf/subscribe [:game])
        turn? (= player turn)]
    [:div
;;     (println (with-out-str (pprint game)))
     (button "Select Player" #(rf/dispatch [:initialize]))
     (button "Refresh" #(rf/dispatch [:play player]))
     [:h3 "Game"]
     [:p (str "You are " player ". It's " (if turn? "your" (str opponent "'s"))
              " turn. There are " cards-remaining " cards remaining.")]
     (if turn?
       [:div
        (active-hand-view hand)
        [destination-view]
        [source-view available-discards]
        (button (str "Make Move") #(rf/dispatch [:move]))
        (when-let [move-message @(rf/subscribe [:move-message])]
          [:p.red-text move-message])]
       (inactive-hand-view hand))
     [:h5 "Your Expeditions"]
     (stack-table expeditions)
     [:h5 (str opponent "'s Expeditions")]
     (stack-table opponent-expeditions)]))

(defn loading
  []
  [:div
   [:span "Loading..."]
   [:span @(rf/subscribe [:status-message])]
   (button "Refresh" #(rf/dispatch [:initialize]))])

(defn error
  []
  [:div
   [:h1 "Error!"]
   [:p @(rf/subscribe [:error-message])]])

(defn app
  []
  (let [loading?  @(rf/subscribe [:loading?])
        screen @(rf/subscribe [:screen])
        status-message @(rf/subscribe [:status-message])]
    [:div
     [:p
      (if status-message status-message "No status message set.")
      (when loading? " Loading...") ]
     (case screen
       :player-selection [player-selection]
       :game [game]
       :error [error]
       (throw (js/Error. (str "Invalid screen: " screen))))]))

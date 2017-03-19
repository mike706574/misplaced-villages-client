(ns misplaced-villages-client.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :screen
 (fn [db _]
   (:app/screen db)))

(rf/reg-sub
 :loading?
 (fn [db _]
   (:app/loading? db)))

(rf/reg-sub
 :status-message
 (fn [db _]
   (:app/status-message db)))

(rf/reg-sub
 :move-message
 (fn [db _]
   (:app/move-message db)))

(rf/reg-sub
 :game
 (fn [db _]
   (:app/game db)))

(rf/reg-sub
 :player
 (fn [db _]
   (:app/player db)))

(rf/reg-sub
 :destination
 (fn [db _]
   (:app/destination db)))

(rf/reg-sub
 :card
 (fn [db _]
   (:app/card db)))

(rf/reg-sub
 :source
 (fn [db _]
   (:app/source db)))

(rf/reg-sub
 :error-message
 (fn [db _]
   (:app/error-message db)))

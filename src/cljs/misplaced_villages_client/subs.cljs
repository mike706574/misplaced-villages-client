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
 :game
 (fn [db _]
   (println "DB:" db)
   (:app/game db)))

(rf/reg-sub
 :player
 (fn [db _]
   (:app/player db)))

(rf/reg-sub
 :error-message
 (fn [db _]
   (:app/error-message db)))

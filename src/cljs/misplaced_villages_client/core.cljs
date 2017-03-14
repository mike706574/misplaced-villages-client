(ns misplaced-villages-client.core
  (:require [clojure.string :as str]
            [cljs.pprint :refer [pprint]]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [misplaced-villages-client.comms]
            [misplaced-villages-client.events]
            [misplaced-villages-client.subs]
            [misplaced-villages-client.views :as views]))

(enable-console-print!)

(defn ^:export run
  []
  (rf/dispatch-sync [:initialize])
  (reagent/render [views/app] (js/document.getElementById "app")))

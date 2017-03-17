(defproject org.clojars.mike706574/misplaced-villages-client "0.0.1-SNAPSHOT"
  :description "Describe me!"
  :url "https://github.com/mike706574/misplaced-villages-client"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "same as Clojure"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/core.async "0.3.441"]
                 [com.cognitect/transit-cljs "0.8.239"]
                 [com.stuartsierra/component "0.3.2"]
                 [com.taoensso/timbre "4.8.0"]
                 [ring/ring-jetty-adapter "1.5.1"]
                 [environ "1.1.0"]
                 [org.clojure/clojurescript "1.9.494"]
                 [reagent "0.6.0"]
                 [re-frame "0.9.2"]
                 [day8.re-frame/http-fx "0.1.3"]
                 [cljs-ajax "0.5.8"]
                 [org.clojars.mike706574/misplaced-villages "0.0.1-SNAPSHOT"]]
  :plugins [[lein-cljsbuild "1.1.5"]
            [cider/cider-nrepl "0.14.0"]
            [org.clojure/tools.nrepl "0.2.12"]
            [lein-figwheel "0.5.9"]]
  :source-paths ["src/clj"]
  :hooks [leiningen.cljsbuild]
  :profiles {:dev {:source-paths ["dev"]
                   :target-path "target/dev"
                   :dependencies [;[clj-http "3.4.1"]
                                  [org.clojure/tools.namespace "0.2.11"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [figwheel-sidecar "0.5.9"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                   :cljsbuild
                   {:builds
                    {:client
                     {:figwheel {:on-jsload "misplaced-villages-client.core/run"
                                 :websocket-host "192.168.1.141"}
                      :compiler {:main "misplaced-villages-client.core"
                                 :asset-path "js"
                                 :optimizations :none
                                 :closure-defines {misplaced-villages-client.core/api-uri "http://192.168.1.141:8000"}
                                 :source-map true
                                 :source-map-timestamp true}}}}}
             :production {:aot :all
                          :main misplaced-villages-client.server
                          :uberjar-name "misplaced-villages-client.jar"
                          :cljsbuild
                          {:builds
                           {:client
                            {:compiler {:output-dir "target"
                                        :optimizations :advanced
                                        :elide-asserts true
                                        :closure-defines {misplaced-villages-client.core/api-uri "https://mike-misplaced-villages-server.herokuapp.com"}
                                        :pretty-print false}}}}}}
  :figwheel {:repl false}
  :clean-targets ^{:protect false} ["resources/public/js"]
  :cljsbuild {:builds {:client {:source-paths ["src/cljs"]
                                :compiler {:output-dir "resources/public/js"
                                           :output-to "resources/public/js/client.js"}}}})

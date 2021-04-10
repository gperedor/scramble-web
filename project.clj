(defproject scramble-web "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [;; Backend
                 [org.clojure/clojure "1.10.0"]

                 [ring "1.8.1"]
                 [metosin/reitit "0.5.12"]
                 [metosin/jsonista "0.3.1"]
                 [clj-scramble "0.1.0-SNAPSHOT"]
                 [ring/ring-mock "0.4.0"]
                 [ring/ring-jetty-adapter "1.8.1"]

                 ;; Frontend
                 [org.clojure/clojurescript "1.10.773"]

                 [reagent "1.0.0"]
                 [hiccup "1.0.5"]
                 [cljs-http "0.1.46"]

                 ;; misc
                 [fipp "0.6.23"]]
  
  :source-paths ["src"]

  :aliases {"check-run" ["do" "clean," "test," "fig:test," "run"]
            "fig"       ["trampoline" "run" "-m" "figwheel.main"]
            "fig:build" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]
            "fig:test"  ["run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-m" "frontend.test-runner"]}

  :profiles {:dev {:dependencies [[com.bhauman/figwheel-main "0.2.12"]
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]]
                   :main ^:skip-aot backend.server
                   :resource-paths ["target"]
                   ;; need to add the compiled assets to the :clean-targets
                   :clean-targets ^{:protect false} ["target"]}})

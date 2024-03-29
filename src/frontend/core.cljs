(ns frontend.core
  "The core frontend, a Reagent application GETs the result of
  the scramble function and displays the result"
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [fipp.edn :as fedn]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(defn remote-scramble?
  "Updates application state from the results of querying the backend"
  [str1 str2 result-text result-style]
  (go (let [response (<! (http/get "/scramble"
                          {:with-credentials? false
                           :query-params {"str1" @str1
                                          "str2" @str2}}))
            {:keys [status body]} response]
        (reset! result-text
                (cond (and (= 200 status) (:scrambled body))
                      "A scramble!"

                      (and (= 200 status) (not (:scrambled body)))
                      "Not a scramble"

                      :else "An error occurred"))
        (reset! result-style

                (cond (and (= 200 status) (:scrambled body))
                      {:color "green"}

                      (and (= 200 status) (not (:scrambled body)))
                      {:color "purple"}

                      :else {:color "red"}))
        (= status 200))))

(defn str-input
  "Renders an atom-backed text input"
  [atm placeholder]
  (fn [atm placeholder]
    [:input {:type "text"
             :placeholder placeholder
             :on-change  #(do (prn "working?")
                              (reset! atm (-> % .-target .-value)))
             :value @atm}]))

(defn index
  "Main structure of the application"
  []
  ;;; TODO: bundle these ratoms in an app-state map
  (let [result-text (r/atom "Try me out!")
        result-style (r/atom {})
        str1 (r/atom nil)
        str2 (r/atom nil)]
    (fn []
     [:div
      {:style {:font-family "sans-serif"}}
      [:h2 "Scramble predicate"]

      [:div {:style @result-style} @result-text]
      [:div
       [str-input str1 "listening"]
       [str-input str2 "silent"]]

      [:button
       {:type "button"
        :on-click (partial remote-scramble? str1 str2 result-text result-style)}
       "Scrambled?"]])))


(defn ^:export run []
  (rdom/render [index] (js/document.getElementById "app")))

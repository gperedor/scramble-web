(ns backend.server
  "Core backend, it's just routing logic and error messages"
  (:require [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring.coercion :as coercion]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.parameters :as parameters]
            [hiccup.page :refer [html5 include-js include-css]]
            [ring.adapter.jetty :as jetty]
            [muuntaja.core :as m]
            [backend.scramble :as s])
  (:gen-class))


(defn internal-server-error
  [exception request]
  {:status 500
   :message "Internal Server Error"
   :uri (:uri request)})

(defn bad-request
  [exception request]
  {:status 400
   :message "Bad Request"
   :uri (:uri request)})

(def exception-handler
  (exception/create-exception-middleware
   (merge
    exception/default-handlers
    { ;; ex-data with :type ::error

     :reitit.coercion/request-coercion bad-request
     ;; override the default handler
     ::exception/default internal-server-error

     ;; print stack-traces for all exceptions
     ::exception/wrap (fn [handler e request]
                        (println "ERROR" (pr-str (:uri request)))
                        (handler e request))})))
(def app
  (ring/ring-handler
   (ring/router

     [["/swagger.json"
        {:get {:no-doc true
               :swagger {:info {:title "scramble-api"
                                :description "The Flexiana coding test"}}
               :handler (swagger/create-swagger-handler)}}]

      ["/api-docs/*"
       {:get {:no-doc true
              :handler (swagger-ui/create-swagger-ui-handler
                        {:config {:validatorUrl nil}})}}]

      ["/scramble"
       {:get {:summary "Check if the first string can be scrambled to contain the second"
              :parameters {:query {:str1 string?, :str2 string?}}
              :responses {200 {:body {:scrambled boolean?}}}
              :handler (fn [{{{:keys [str1 str2]} :query} :parameters}]
                         {:status 200
                          :body {:scrambled (s/scramble? str1 str2)}})}}]]

     {:exception pretty/exception
      :conflicts (constantly nil)
      :data {:coercion reitit.coercion.spec/coercion
              :muuntaja m/instance
              :middleware [;; swagger feature
                           swagger/swagger-feature
                           ;; query-params & form-params
                           parameters/parameters-middleware
                           ;; content-negotiation
                           muuntaja/format-negotiate-middleware
                           ;; encoding response body
                           muuntaja/format-response-middleware
                           ;; exception handling
                           exception-handler
                           ;; decoding request body
                           muuntaja/format-request-middleware
                           ;; coercing response bodys
                           coercion/coerce-response-middleware
                           ;; coercing request parameters
                           coercion/coerce-request-middleware
                           ;; multipart
                           multipart/multipart-middleware]}})
   (ring/routes
    (ring/create-resource-handler {:path "/"})
    (ring/create-default-handler))))

(defn start []
  (let [port (or (System/getenv "SERVER_PORT") 3000)]
    (jetty/run-jetty #'app {:port port, :join? false})
    (println (str "server running in port " port))))

(defn -main []
  (start))

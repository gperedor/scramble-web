(ns backend.server
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
            [ring.adapter.jetty :as jetty]
            [muuntaja.core :as m]
            [clj-scramble.core :as s])
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

       ["/scramble"
        {:get {:summary "Check if the first string can be scrambled to contain the second"
               :parameters {:query {:str1 string?, :str2 string?}}
               :responses {200 {:body {:scrambled boolean?}}}
               :handler (fn [{{{:keys [str1 str2]} :query} :parameters}]
                          {:status 200
                           :body {:scrambled (s/scramble? str1 str2)}})}}]]
      {:exception pretty/exception
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
      (swagger-ui/create-swagger-ui-handler
        {:path "/"
         :config {:validatorUrl nil
                  :operationsSorter "alpha"}})
      (ring/create-default-handler))))

(defn start []
  (jetty/run-jetty #'app {:port 3000, :join? false})
  (println "server running in port 3000"))

(defn -main []
  (start))

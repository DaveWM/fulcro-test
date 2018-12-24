(ns fulcro-test.client
  (:require [fulcro.client :as fc]
            [fulcro-test.ui.root :as root]
            [fulcro.client.network :as net]
            [fulcro.client.data-fetch :as df]
            [fulcro-test.ui.counter :as counter]))

(defonce app (atom nil))

(defn mount []
  (reset! app (fc/mount @app root/Root "app")))

(defn start []
  (mount))

(def secured-request-middleware
  ;; The CSRF token is embedded in the server_components/html.clj
  (->
    (net/wrap-csrf-token (or js/fulcro_network_csrf_token "TOKEN-NOT-IN-HTML!"))
    (net/wrap-fulcro-request)))

(defn ^:export init []
  (reset! app (fc/new-fulcro-client
                ;; This ensures your client can talk to  CSRF-protected server.
                ;; See middleware.clj to see how the token is embedded into the HTML
                :networking {:remote (net/fulcro-http-remote
                                       {:url "/api"
                                        :request-middleware secured-request-middleware})}
                :started-callback (fn [app]
                                    (df/load app :all-counters counter/Counter {:target [:counters]}))
                :initial-state {:root/message "Hello!"
                                :counters []
                                :counters/by-id {}}))
  (start))

(comment
  (prim/db->tree (prim/get-query Root)
                 @(fulcro.client.primitives/app-state
                    (-> app deref :reconciler))
                 @(fulcro.client.primitives/app-state
                    (-> app deref :reconciler)))
  )

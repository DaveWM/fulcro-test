(ns fulcro-test.server-main
  (:require
    [mount.core :as mount]
    fulcro-test.server-components.http-server
    [fulcro.server])
  (:gen-class))

;; This is a separate file for the uberjar only. We control the server in dev mode from src/dev/user.clj
(defn -main [& args]
  (mount/start-with-args {:config "config/prod.edn"}))

(defonce state (atom {:counters {1 {:db/id 1
                                    :counter/value 1337}}}))

(fulcro.server/defquery-root :all-counters
  (value [env params]
    (->> (:counters @state)
         vals
         vec)))

(fulcro.server/defmutation fulcro-test.ui.root/increment [{:keys [id]}]
  (action [env]
    (swap! state update-in [:counters id :counter/value] inc)
    nil))

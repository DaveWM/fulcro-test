(ns fulcro-test.server-main
  (:require
    [mount.core :as mount]
    fulcro-test.server-components.http-server
    [fulcro.server]
    [fulcro.client.primitives :as prim])
  (:gen-class))

;; This is a separate file for the uberjar only. We control the server in dev mode from src/dev/user.clj
(defn -main [& args]
  (mount/start-with-args {:config "config/prod.edn"}))

(defonce state (atom {:counters {1 {:db/id         1
                                    :counter/value 1}}}))

(fulcro.server/defquery-root :all-counters
  (value [env params]
         (->> (:counters @state)
              vals
              vec)))

(fulcro.server/defmutation fulcro-test.ui.root/increment [{:keys [id]}]
  (action [env]
    (swap! state update-in [:counters id :counter/value] inc)
    nil))

(defn ->monoid [f identity]
  (fn monoid
    ([] identity)
    ([& args] (apply f args))))

(fulcro.server/defmutation fulcro-test.ui.root/add-counter [{:keys [tempid]}]
  (action [env]
    (let [id (->> @state
                  :counters
                  keys
                  (apply (->monoid max 0))
                  inc)]
      (swap! state assoc-in [:counters id] {:db/id         id
                                            :counter/value 1})
      {::prim/tempids {tempid id}})))

(fulcro.server/defmutation fulcro-test.ui.root/remove-counter [{:keys [id]}]
  (action [env]
    (swap! state update :counters dissoc id)
    nil))

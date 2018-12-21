(ns fulcro-test.ui.root
  (:require
    [fulcro.client.dom :as dom :refer [div]]
    [fulcro.client.primitives :as prim :refer [defsc]]
    [fulcro.client.mutations :refer [defmutation]]
    [fulcro-test.ui.components :as comp]
    [fulcro-test.ui.counter :as counter]))

(defmutation increment [{:keys [id]}]
  (action [{:keys [state] :as env}]
    (swap! state update-in [:counters/by-id id :counter/value] inc))
  (remote [_] true))

(defsc Root [this {:keys [root/message counters ui/loading-data] :as props}]
  {:query [:root/message {:counters (prim/get-query counter/Counter)} :ui/loading-data]}
  (if loading-data
    (dom/p "Loading...")
    (div :.ui.segments
         (div :.ui.top.attached.segment
              (div :.content
                   "Welcome to Fulcro!"))
         (div :.ui.attached.segment
              (div :.content
                   (comp/ui-placeholder {:w 50 :h 50})
                   (div message)
                   (map counter/ui-counter counters))))))

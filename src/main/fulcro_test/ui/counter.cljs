(ns fulcro-test.ui.counter
  (:require [fulcro.client.primitives :refer [defsc]]
            [fulcro.client.dom :as dom]
            [fulcro.client.primitives :as prim]))


(defsc Counter
  [this {:keys [db/id counter/value]}]
  {:query [:db/id :counter/value]
   :ident [:counters/by-id :db/id]}
  (dom/div
    (dom/h3 "Counter " (if (prim/tempid? id)
                         "..."
                         id))
    (dom/p (str "value is: " value))
    (dom/button #js {:onClick #(prim/transact! this
                                               `[(fulcro-test.ui.root/increment {:id ~id})])}
                "Increment")
    (dom/button #js {:onClick #(prim/transact! this
                                               `[(fulcro-test.ui.root/remove-counter {:id ~id}) :counters])}
                "Delete")))


(def ui-counter (prim/factory Counter {:key-fn #(:db/id %)}))

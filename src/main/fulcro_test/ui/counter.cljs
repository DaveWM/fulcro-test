(ns fulcro-test.ui.counter
  (:require [fulcro.client.primitives :refer [defsc]]
            [fulcro.client.dom :as dom]
            [fulcro.client.primitives :as prim]
            ["@material-ui/core/Card" :default Card]
            ["@material-ui/core/CardContent" :default CardContent]
            ["@material-ui/core/CardActions" :default CardActions]
            ["@material-ui/core/Button" :default Button]
            [fulcro-test.ui.utils :as u]))


(defsc Counter
  [this {:keys [db/id counter/value]}]
  {:query [:db/id :counter/value]
   :ident [:counters/by-id :db/id]}
  (u/js-comp
    Card {}
    (u/js-comp
      CardContent {}
      (dom/h3 "Counter " (if (prim/tempid? id)
                           "..."
                           id))
      (dom/p (str "value is: " value)))
    (u/js-comp
      CardActions {}
      (u/js-comp Button #js {:onClick #(prim/transact! this
                                                       `[(fulcro-test.ui.root/increment {:id ~id})])
                             :color   "primary"
                             :variant "contained"}
                 "Increment")
      (u/js-comp Button #js {:onClick #(prim/transact! this
                                                       `[(fulcro-test.ui.root/remove-counter {:id ~id}) :counters])
                             :color   "secondary"
                             :variant "contained"}
                 "Delete"))))


(def ui-counter (prim/factory Counter {:key-fn #(:db/id %)}))

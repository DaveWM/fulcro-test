(ns fulcro-test.ui.root
  (:require
    [fulcro.client.dom :as dom :refer [div]]
    [fulcro.client.primitives :as prim :refer [defsc]]
    [fulcro.client.mutations :as fm :refer [defmutation]]
    [fulcro-test.ui.counter :as counter]
    ["@material-ui/core/Button" :default Button]
    ["@material-ui/core/Grid" :default Grid]
    ["@material-ui/core/CssBaseline" :default CssBaseline]
    ["@material-ui/core/AppBar" :default AppBar]
    ["@material-ui/core/Toolbar" :default Toolbar]
    ["@material-ui/core/CircularProgress" :default CircularProgress]
    [fulcro-test.ui.utils :as u]))

(defmutation increment [{:keys [id]}]
  (action [{:keys [state] :as env}]
    (swap! state update-in [:counters/by-id id :counter/value] inc))
  (remote [_] true))

(defmutation add-counter [{:keys [tempid]}]
  (action [{:keys [state]}]
    (swap! state (fn [s]
                   (let [ident [:counters/by-id tempid]]
                     (-> s
                         (assoc-in ident {:db/id         tempid
                                          :counter/value 1})
                         (fm/integrate-ident* ident
                                              :append
                                              [:counters]))))))
  (remote [_] true))

(defmutation remove-counter [{:keys [id]}]
  (action [{:keys [state]}]
    (swap! state (fn [s]
                   (-> s
                       (update :counters #(->> %
                                               (remove (fn [[_ counter-id]]
                                                         (= id counter-id)))
                                               (into [])))))))
  (remote [_] true))


(defsc Root [this {:keys [root/message counters ui/loading-data] :as props}]
  {:query [:root/message {:counters (prim/get-query counter/Counter)} :ui/loading-data]}
  (dom/div
    {}
    (u/js-comp CssBaseline {})
    (if loading-data
      (dom/div {:bp "grid 4 vertical-center margin"}
               (u/js-comp CircularProgress {:bp "offset-2 text-center flex"
                                            :style {:width "auto"
                                                    :height "auto"}}))
      (dom/div
        {}
        (u/js-comp
          AppBar {:position "relative"}
          (u/js-comp Toolbar {} (dom/h1 "Amazing Counter Application")))
        (div {:bp "container margin"}
             (dom/div {:bp "grid 4 margin-bottom"}
                      (map counter/ui-counter counters))
             (u/js-comp
               Button #js {:onClick #(prim/transact! this `[(add-counter {:tempid ~(prim/tempid)})])
                           :color   "primary"
                           :variant "contained"}
               ["Add Counter"]))))))
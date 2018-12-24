(ns fulcro-test.ui.utils)

(defn js-comp [class props & children]
  (js/React.createElement class (clj->js props) (doall children)))

(ns fulcro-test.workspaces
  (:require
    [nubank.workspaces.core :as ws]
    [fulcro-test.demo-ws]))

(defonce init (ws/mount))

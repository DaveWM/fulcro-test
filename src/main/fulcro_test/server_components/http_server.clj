(ns fulcro-test.server-components.http-server
  (:require
    [fulcro-test.server-components.config :refer [config]]
    [fulcro-test.server-components.middleware :refer [middleware]]
    [mount.core :refer [defstate]]
    [org.httpkit.server :as http-kit]))

(defstate http-server
  :start (http-kit/run-server middleware (:http-kit config))
  :stop (http-server))

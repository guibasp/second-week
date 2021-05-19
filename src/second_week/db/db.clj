(ns second-week.db.db
  (:require [datomic.client.api :as d])
  (:use [clojure pprint]))

(def cfg {:server-type :peer-server
          :access-key "myaccesskey"
          :secret "mysecret"
          :endpoint "localhost:8998"
          :validate-hostnames false})

(def client (d/client cfg))

(defn create-database
  []
  (d/connect client {:db-name "credit-card"}))
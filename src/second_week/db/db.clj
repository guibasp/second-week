(ns second-week.db.db
  (:require [datomic.api :as d])
  (:use [clojure pprint]))


(def db-uri "datomic:dev://localhost:4334/credit-card")

(defn create-database
  []
  (let [_ (d/create-database db-uri)
        conn (d/connect db-uri)]
    conn))

(defn delete
  []
  (d/delete-database db-uri))
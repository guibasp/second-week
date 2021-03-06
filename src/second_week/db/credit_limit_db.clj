(ns second-week.db.credit-limit-db
  (:require [datomic.api :as d]
            [second-week.db.db :as s.db])
  (:use [clojure pprint]))

(def credit-limit-schema [{:db/ident       :credit/customer-id
                           :db/valueType :db.type/uuid
                           :db/cardinality :db.cardinality/one
                           :db/unique :db.unique/identity
                      }
                     {:db/ident :credit/credit-limit
                      :db/valueType :db.type/bigint
                      :db/cardinality :db.cardinality/one
                      :db/doc "The customer's credit limit"
                      }])

(defn create-credit-limit-schema
  [conn]
  (d/transact conn credit-limit-schema))

(defn init-db
  []
  (let [conn (s.db/create-database)
        schema-tx (create-credit-limit-schema conn)]
    (pprint schema-tx)))


(defn find-all
  [conn]
  (let [db (d/db conn)]
    (d/q '[:find ?entity ?customer-id ?credit-limit
           :keys :credit/entity :credit/customer-id :credit/credit-limit
          :where [?entity :credit/customer-id ?customer-id]
          [?entity :credit/credit-limit ?credit-limit]] db)))

(defn find-credit-by-customer
  [customer-id conn]
  ;(println customer-id)
  (let [db (d/db conn)]
    (d/q '[
           :find (pull ?entity [*])                         ;recupera todos os atributos da entity
           ;:find (pull ?entity [:db/id :credit/customer-id :credit/credit-limit])
                 :in $ ?id-filter
                 :where [?entity :credit/customer-id ?customer-id]
                 [?entity :credit/credit-limit ?credit-limit]
                 [?entity :credit/customer-id ?id-filter]] db customer-id)))

(defn save!
  [customer-id limit conn]
  (let [credit-to-storage {:credit/customer-id  customer-id
                           :credit/credit-limit (bigint limit)}
        credit (find-credit-by-customer customer-id conn)]
    (println "Trying to create a customer's limit " customer-id limit)
    (println "Credit ?" credit)
    (if (not-empty credit)
      credit
      ;(throw (Exception. "Its not possible create a new credit, cause credit already exist!"))
      ;(d/transact conn {:tx-data [credit-to-storage]}))))
      (d/transact conn [credit-to-storage]))))
(init-db)
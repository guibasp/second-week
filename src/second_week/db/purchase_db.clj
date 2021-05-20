(ns second-week.db.purchase-db
  (:require [datomic.api :as d]
            [second-week.db.credit-limit-db :as s.cldb]
            [second-week.db.customers-db :as s.cdb]
            [second-week.db.db :as s.db])
  (:use [clojure pprint]))


;{
 ;  :customer-id  15
 ;  :date-event   (f.logic/date-of 2021 01 01)
 ;  :amount-value 900
 ;  :merchant     "Coffe & You"
 ;  :category     "Food"
 ;  }
(def purchase-schema [{:db/ident  :purchase/customer-id
                       :db/valueType   :db.type/uuid
                       :db/cardinality :db.cardinality/one
                       }
                      {:db/ident :purchase/date-event
                      :db/valueType :db.type/instant
                      :db/cardinality :db.cardinality/one
                      :db/doc "The customer's credit limit"
                      }
                      {:db/ident :purchase/amount-value
                       :db/valueType :db.type/bigint
                       :db/cardinality :db.cardinality/one
                       :db/doc "The amount value of purchase"
                       }
                      {:db/ident :purchase/merchant
                       :db/valueType :db.type/string
                       :db/cardinality :db.cardinality/one
                       :db/doc "The merchants name, but in future this value could be a merchants 'id'"
                       }
                      {:db/ident :purchase/category
                       :db/valueType :db.type/string
                       :db/cardinality :db.cardinality/one
                       :db/doc "The category name, but in future this value could be a category 'id'"
                       }])

(defn create-purchase-schema
  [conn]
  (d/transact conn purchase-schema))

(defn init-db
  []
  (let [conn (s.db/create-database)
        schema-tx (create-purchase-schema conn)]
    (pprint schema-tx)))

(defn do-transaction
  [customer-id purchase conn]
  (let [optional (s.cldb/find-credit-by-customer customer-id conn)
        customer-limit (ffirst optional)
        limit (:credit/credit-limit customer-limit)
        limit-id (:db/id customer-limit)
        amount-value (:purchase/amount-value purchase 0)]
    (if (nil? customer-limit)
      (throw (Exception. "The customer limit is null")))
    (if (< amount-value 1)
      (throw (Exception. "the amount value is laster than 0.01 cents")))
    (if (> amount-value limit)
      (println "Customer has not limit")
      (do
        (let [tx (d/transact conn [[:db/add limit-id :credit/credit-limit (- limit amount-value)]
                                   purchase])]
          (pprint tx))
        purchase))))

(defn list-all-purchases
  [conn]
  (let [db (d/db conn)]
    (d/q '[:find (pull ?entity [*])
           :where [?entity :purchase/customer-id]] db)))

(defn list-all-purchases-by-customer
  [customer-id conn]
  (let [db (d/db conn)]
    (d/q '[:find (pull ?entity [*])
           :in $ ?filter-customer
           :where [?entity :purchase/customer-id]
                  [?entity :purchase/customer-id ?filter-customer]] db customer-id)))
(init-db)
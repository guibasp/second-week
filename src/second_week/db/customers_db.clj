(ns second-week.db.customers-db
  (:require [datomic.api :as d]
            [second-week.db.db :as s.db])
  (:use [clojure pprint]))
;:cpf "000000000191",
;:name        "Pedro Paulo Santos"
;:email       "pedro.paulo@gmail.com"
;}

(def customers-schema [{:db/ident       :customer/cpf
                      :db/valueType   :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc         "The customer's identifier in brazil"
                      }
                     {:db/ident :customer/name
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "The customer's name"
                      }
                     {:db/ident :customer/email
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "The customer's e-mail"
                      }
                       {:db/ident :customer/id
                        :db/valueType :db.type/uuid
                        :db/cardinality :db.cardinality/one
                        :db/unique :db.unique/identity}])

(defn create-customers-schema
  [conn]
  ;(d/transact conn {:tx-data customers-schema}))
  (d/transact conn customers-schema))
(defn init-db
  []
  (let [conn (s.db/create-database)
        schema-tx (create-customers-schema conn)]
    (pprint schema-tx)))

(defn find-all
  [conn]
  (let [db (d/db conn)]
    (d/q '[:find (pull ?entity [*])
          :where [?entity :customer/cpf ?cpf]
          [?entity :customer/name ?name]
          [?entity :customer/email ?email]] db)))

(defn find-customer-by-cpf
  [cpf conn]
  (println cpf)
  (when (not (nil? cpf))
    (let [db (d/db conn)]
      (d/q '[:find (pull ?entity [:db/id :customer/id :customer/cpf :customer/name :customer/email])
             :in $ ?cpf-filter
             :where [?entity :customer/id ?id]
             [?entity :customer/cpf ?cpf]
             [?entity :customer/name ?name]
             [?entity :customer/email ?email]
             [?entity :customer/cpf ?cpf-filter]] db cpf))))

(defn save!
  [customer conn]
  (let [cpf (:customer/cpf customer)
        customer-filtered (find-customer-by-cpf cpf conn)]
    (println "customer already exist!" customer-filtered)
    (if (empty? customer-filtered)
      ;(d/transact conn {:tx-data [customer]}))))
      (d/transact conn [customer]))))
(defn change-customers-email
  [customer-id email conn]
  ;(d/transact conn {:tx-data [[:db/add customer-id :customer/email email]]}))
  (d/transact conn [[:db/add customer-id :customer/email email]]))


(defn find-customer-by-uuid
  [uuid conn]
  (d/pull (d/db conn) '[*] [:customer/id uuid]))

(init-db)
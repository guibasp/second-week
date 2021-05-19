(ns second-week.core
  (:require [second-week.logic.logic :as s.logic]
            [second-week.db.db :as s.db]
            [second-week.db.customers-db :as s.customer-db]
            [second-week.db.credit-limit-db :as s.credit-db])
  (:use [clojure pprint]))

(defn start
  []
  (println "Init")
  (let [customer-to-insert {:customer/cpf "000000000191"
                            :customer/name "Pedro Santos"
                            :customer/email "pedro.paulo@gmail.com"}
        conn (s.db/create-database)
        customer-tx (s.customer-db/save! customer-to-insert conn)
        customer-id (first (vals (:tempids customer-tx {})))]
    (println "Customer id " customer-id)
    (if (nil? customer-id)
      (throw (.Exception "Customer id is nil"))
      (do
        (println "creating a credit card")
        ;create a credit with a value of 1000 reais
        (s.credit-db/save! customer-id 100000 conn)
        ))))

(defn input
  [label]
  (print label)
  (flush)
  (read-line))

(defn create-a-new-customer
  []
  (let [customer {:customer/cpf (input "Enter the cpf please :")
                  :customer/name (input "Enter with a name please :")
                  :customer/email (input "Enter with a email please :")}
        conn (s.db/create-database)
        customer-tx (s.customer-db/save! customer conn)
        customer-id (first (vals (:tempids customer-tx {})))]
    (println "Customer id " customer-id)
    (if (nil? customer-id)
      (throw (.Exception "Customer id is nil"))
      (do
        (println "creating a credit card")
        ;create a credit with a value of 1000 reais
        (s.credit-db/save! customer-id 100000 conn)
        (assoc customer :db/id customer-id)))))

(defn list-all-customers
  []
  (let [conn (s.db/create-database)
        customers (s.customer-db/find-all conn)]
    (println customers)
    customers))

(defn find-customer-by-cpf
  []
  (let [conn (s.db/create-database)
        cpf (input "Enter with a cpf number")
        customer (s.customer-db/find-customer-by-cpf cpf conn)]
    (println customer)
    customer))

(def functions {:1 create-a-new-customer
                :2 list-all-customers
                :3 find-customer-by-cpf})

(defn start-two
  []
  (do
      (flush)
      (println "Select")
      (println "1 - Register customer")
      (println "2 - list all customer")
      (println "3 - find customer by cpf")
      (println "4 - find customer by cpf")
      (flush)
      (let [option (read-line)]
        (when (not (= option "0"))
          (((keyword option) functions #()))
          (recur)))))

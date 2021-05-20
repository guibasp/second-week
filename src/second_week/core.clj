(ns second-week.core
  (:require [second-week.logic.logic :as s.logic]
            [second-week.db.db :as s.db]
            [second-week.db.customers-db :as s.customer-db]
            [second-week.db.credit-limit-db :as s.credit-db]
            [second-week.db.purchase-db :as s.purchase-db]
            [second-week.model.model :as s.model])
  (:use [clojure pprint]))

(defn input
  [label]
  (print label)
  (flush)
  (read-line))

(defn create-a-new-customer!
  []
  (let [
        cpf (input "Enter the cpf please :")
        name (input "Enter with a name please :")
        email (input "Enter with a email please :")
        customer (s.model/create-customer cpf name email)
        conn (s.db/create-database)
        customer-tx @(s.customer-db/save! customer conn)
        customer-id (first (vals (:tempids customer-tx)))]
    (if (nil? customer-id)
      (throw (.Exception "Customer id is nil"))
      (do
        (println "creating a credit card")
        ;create a credit with a value of 1000 reais
        (s.credit-db/save! (:customer/id customer) 100000 conn)
        (println "Created customer with id " customer-id)
        (assoc customer :customer/id customer-id)))))

(defn list-all-customers
  []
  (let [conn (s.db/create-database)
        customers (s.customer-db/find-all conn)]
    (->> customers
         (map #(str (:customer/cpf (first %)) " - " (:customer/name (first %))))
         (reduce #(str %1 "\n" %2))
         println)
    customers))

(defn find-customer-by-cpf
  []
  (let [conn (s.db/create-database)
        cpf (input "Enter with a cpf number")
        customer (s.customer-db/find-customer-by-cpf cpf conn)]
    (->> customer
         first
         (map #(str (:customer/cpf %) " - " (:customer/name %)))
         println)
    customer))

(defn register-a-new-transaction!
  []
  (let [conn (s.db/create-database)
        cpf (input "What is the cpf number of these transaction? ")
        value (bigint (input "Transaction's value : "))
        category (input "Enter with the category name : ")
        merchant (input "Enter with the merchant name : ")
        date-of (s.logic/date-now)
        optional (s.customer-db/find-customer-by-cpf cpf conn)
        customer (-> optional
                     ffirst)
        customer-id (:customer/id customer)]
      (if (nil? customer-id)
        (throw (Exception. (str "Customer with the cpf " cpf " does not exists"))))
      (let [purchase (s.model/create-purchase
                       customer-id date-of value merchant category)
            purchase-tx (s.purchase-db/do-transaction customer-id purchase conn)]
        (println "Transaction is done!")
        purchase-tx)))

(defn customers-limit
  []
  (let [conn (s.db/create-database)
        cpf (input "Enter with a cpf number")
        optional (s.customer-db/find-customer-by-cpf cpf conn)
        customer (-> optional
                     ffirst)
        customer-id (:customer/id customer)
        limit (-> (s.credit-db/find-credit-by-customer customer-id conn)
                  ffirst
                  :credit/credit-limit)]
    (println "The customer's limit is " limit)))

(defn list-all-purchases
  []
  (let [conn (s.db/create-database)
        purchases (s.purchase-db/list-all-purchases conn)]
    (->> purchases
         (map #(str
                 (:purchase/customer-id (first %)) "|" (:purchase/date-event (first %))
                 "|" (:purchase/amount-value (first %)) "|" (:purchase/category (first %))
                 "|" (:purchase/merchant (first %))))
         (reduce #(str %1 "\n" %2))
         println)
    purchases))

(def functions {:1 create-a-new-customer!
                :2 list-all-customers
                :3 find-customer-by-cpf
                :4 register-a-new-transaction!
                :5 customers-limit
                :6 list-all-purchases})

(defn start
  []
  (do
      (flush)
      (println "Select")
      (println "1 - Register customer")
      (println "2 - list all customer")
      (println "3 - find customer by cpf")
      (println "4 - Register a new transaction")
      (println "5 - Show the customers limit")
      (println "6 - Show all purchase")
      (println "0 - Exit")
      (flush)
      (let [option (read-line)]
        (when (not (= option "0"))
          (((keyword option) functions #(println "Enter with the valid option")))
          (recur)))))

(ns second-week.model.model
  (:require [second-week.logic.logic :as s.logic]))

(defn create-customer
  ([cpf name email]
   (create-customer (s.logic/uuid) cpf name email))
  ([uuid cpf name email]
   {:customer/id uuid
    :customer/cpf   cpf
    :customer/name  name
    :customer/email email}))

;{
;  :customer-id  15
;  :date-event   (f.logic/date-of 2021 01 01)
;  :amount-value 900
;  :merchant     "Coffe & You"
;  :category     "Food"
;  }
(defn create-purchase
  [customer-uuid date-event amount-value merchant category]
  {:purchase/customer-id customer-uuid
   :purchase/date-event date-event
   :purchase/amount-value amount-value
   :purchase/merchant merchant
   :purchase/category category})
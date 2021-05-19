(ns second-week.logic.logic)
(defn generate-products-id
  []
  (str (java.util.UUID/randomUUID)))

(defn date-of
  [year month day]
  (java.util.Date/UTC year month day 0 0 0))

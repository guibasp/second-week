(ns second-week.logic.logic)
(defn uuid
  []
  (java.util.UUID/randomUUID))

(defn date-of
  [year month day]
  (java.util.Date/UTC year month day 0 0 0))

(defn uuid-from-string [data]
  (java.util.UUID/fromString
    (clojure.string/replace data
                            #"(\w{8})(\w{4})(\w{4})(\w{4})(\w{12})"
                            "$1-$2-$3-$4-$5")))


(defn date-now
  []
  (java.util.Date.))
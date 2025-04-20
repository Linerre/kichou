(ns kichou.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as jsql]
            [honey.sql :as sql]
            [honey.sql.helpers :refer [select from where join]]))

;; Define the database connection spec
;; This uses a relative path to the database file
(def db-spec
  {:dbtype "sqlite"
   :dbname "db/expense.db"})

;; Create a connection pool for better performance
(def datasource (jdbc/get-datasource db-spec))

;; Helper function to execute queries
(defn query [sql-map]
  (jdbc/execute! datasource (sql/format sql-map)))

;; == Queries =======================================

(defn get-all-providers []
  (query {:select [:*], :from [:Providers]}))

(defn get-provider-by-abn [abn]
  (first
   (query
    (-> (select :*)
        (from :Providers)
        (where [:= :abn abn])))))

(defn get-all-expenses []
  (query
   (-> (select :*)
       (from :Expenses))))

(defn get-expenses-by-week [week-number]
  (query
   (-> (select :*)
       (from :Expenses)
       (where [:= :week_number week-number]))))

(defn get-expenses-by-provider [provider-abn]
  (query
   (-> (select :*)
       (from :Expenses)
       (where [:= :provider_abn provider-abn]))))

(defn get-expenses-with-provider-details []
  (query
   (-> (select [:e.expense_id :id]
                      [:e.date :date]
                      [:e.week_number :week]
                      [:e.product_name :product]
                      [:e.price :price]
                      [:e.quantity :quantity]
                      [:p.full_brand_name :provider_name]
                      [:p.location :location])
       (from [:Expenses :e])
       (join [:Providers :p] [:= :e.provider_abn :p.abn]))))

;; == Updates =======================================

(defn add-provider [abn full-brand-name location website]
  (jsql/insert! datasource :Providers
               {:abn abn
                :full_brand_name full-brand-name
                :location location
                :website website}))

(defn add-expense [date week-number product-name price quantity provider-abn]
  (jsql/insert! datasource :Expenses
               {:date date
                :week_number week-number
                :product_name product-name
                :price price
                :quantity quantity
                :provider_abn provider-abn}))

(defn update-provider [abn updates]
  (jsql/update! datasource :Providers updates {:abn abn}))

(defn update-expense [expense-id updates]
  (jsql/update! datasource :Expenses updates {:expense_id expense-id}))

(defn delete-provider [abn]
  (jsql/delete! datasource :Providers {:abn abn}))

(defn delete-expense [expense-id]
  (jsql/delete! datasource :Expenses {:expense_id expense-id}))

(ns kichou.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as jsql]
            [honey.sql :as sql]
            [honey.sql.helpers :refer [select from where join]]))

;; == Databse =======================================
(def db-spec
  {:dbtype "sqlite"
   :dbname "db/expense.db"})

(def datasource (jdbc/get-datasource db-spec))

(defn query [sql-map]
  (jdbc/execute! datasource (sql/format sql-map)))

;; == Queries =======================================

(defn get-all-providers []
  (query {:select [:*], :from [:Providers]}))

(defn get-provider-by-abn [abn]
  (-> (select :*)
      (from :Providers)
      (where [:= :abn abn])
      (query)
      (first)))

(defn get-all-expenses []
  (-> (select :*)
      (from :Expenses)
      (query)))

(defn get-expenses-by-week [week-number]
  (-> (select :*)
      (from :Expenses)
      (where [:= :week_number week-number])
      (query)))

(defn get-expenses-by-provider [provider-abn]
  (-> (select :*)
      (from :Expenses)
      (where [:= :provider_abn provider-abn])
      (query)))

(defn get-expenses-with-provider-details []
  (-> (select [:e.expense_id :id]
              [:e.date :date]
              [:e.week_number :week]
              [:e.product_name :product]
              [:e.price :price]
              [:e.quantity :quantity]
              [:p.full_brand_name :provider_name]
              [:p.location :location])
      (from [:Expenses :e])
      (join [:Providers :p] [:= :e.provider_abn :p.abn])
      (query)))

;; == Updates =======================================

(defn add-provider [{:keys [abn full-brand-name location website]}]
  (jsql/insert! datasource :Providers
                {:abn             abn
                 :full_brand_name full-brand-name
                 :location        location
                 :website         website}))

(defn add-expense
  [{:keys [date week-number product-name price quantity provider-abn]}]
  (jsql/insert! datasource :Expenses
                {:date         date
                 :week_number  week-number
                 :product_name product-name
                 :price        price
                 :quantity     quantity
                 :provider_abn provider-abn}))

(defn update-provider [abn updates]
  (jsql/update! datasource :Providers updates {:abn abn}))

(defn update-expense [expense-id updates]
  (jsql/update! datasource :Expenses updates {:expense_id expense-id}))

(defn delete-provider [abn]
  (jsql/delete! datasource :Providers {:abn abn}))

(defn delete-expense [expense-id]
  (jsql/delete! datasource :Expenses {:expense_id expense-id}))

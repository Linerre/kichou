(ns kichou.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as jsql]
            [honey.sql :as sql]
            [honey.sql.helpers :refer [select from where join]]))

;; == Databse =======================================
(def db-spec
  {:dbtype "sqlite"
   :dbname "db/expenses.db"})

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
      (where [:= :week week-number])
      (query)))

(defn get-expenses-by-provider-abn [provider-abn]
  (-> (select :*)
      (from :Expenses)
      (where [:= :abn provider-abn])
      (query)))

(defn get-expenses-by-provider [provider]
  (-> (select :*)
      (from :Expenses)
      (where [:= :provider provider])
      (query)))

(defn get-expenses-with-provider-details []
  (-> (select [:e.expense_id :id]
              [:e.date :date]
              [:e.number :week]
              [:e.product :product]
              [:e.price :price]
              [:e.quantity :quantity]
              [:p.provider_name :provider_name]
              [:p.location :location])
      (from [:Expenses :e])
      (join [:Providers :p] [:= :e.abn :p.abn])
      (query)))

;; == Updates =======================================

(defn add-provider [{:keys [abn full-brand-name location website]}]
  (jsql/insert! datasource :Providers
                {:abn           abn
                 :provider_name full-brand-name
                 :location      location
                 :website       website}))

(defn add-expense
  [{:keys [date week-number product-name price quantity abn]}]
  (jsql/insert! datasource :Expenses
                {:date     date
                 :week     week-number
                 :product  product-name
                 :price    price
                 :quantity quantity
                 :abn      abn}))

(defn update-provider [abn updates]
  (jsql/update! datasource :Providers updates {:abn abn}))

(defn update-expense [expense-id updates]
  (jsql/update! datasource :Expenses updates {:expense_id expense-id}))

(defn delete-provider [abn]
  (jsql/delete! datasource :Providers {:abn abn}))

(defn delete-expense [expense-id]
  (jsql/delete! datasource :Expenses {:expense_id expense-id}))

(comment (get-all-providers))

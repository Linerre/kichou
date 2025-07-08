(ns kichou.page
  "The home page for backend"
  (:require
   [shadow.css :refer (css)]
   [hiccup2.core :as h]
   [kichou.db :as db]))

(defn render-html [content]
  (-> [:html
       [:head
        [:title "Kichou Expense Tracker"]]
       [:body
        [:nav
         [:a {:href "/"} "Home"] " | "
         [:a {:href "/providers"} "Providers"] " | "
         [:a {:href "/expenses"} "Expenses"]]
        content]]
      (h/html)
      (str)))

(defn home []
  (render-html
    [:div
     [:h1 "Kichou Expense Tracker"]
     [:ul
      [:li [:a {:href "/providers"} "View All Providers"]]
      [:li [:a {:href "/expenses"} "View All Expenses"]]
      [:li [:a {:href "/expenses-with-details"} "View Expenses With Provider Details"]]]]))

;; TODO: allow editing of a provider
(defn providers []
  (let [providers (db/get-all-providers)]
    (render-html
     [:div
      [:h1 "All Providers"]
      [:table
       [:thead
        [:tr
         [:th "ABN"]
         [:th "Name"]
         [:th "Location"]
         [:th "Website"]
         [:th "Actions"]]]
       [:tbody
        (for [provider providers]
          [:tr
           [:td (:Providers/abn provider)]
           [:td (:Providers/provider_name provider)]
           [:td (:Providers/location provider)]
           [:td (or (:Providers/website provider) "N/A")]
           [:td
            [:a {:href (str "/provider/" (:abn provider))} "View"]]])
        ]]
      [:h2 "Add New Provider"]
      [:form {:action "/add-provider" :method "post"}
       [:div [:label "ABN: "] [:input {:type "text" :name "abn" :required true}]]
       [:div [:label "Name: "] [:input {:type "text" :name "full-brand-name" :required true}]]
       [:div [:label "Location: "] [:input {:type "text" :name "location"}]]
       [:div [:label "Website: "] [:input {:type "text" :name "website"}]]
       [:div [:input {:type "submit" :value "Add Provider"}]]]])))


(defn not-found []
  [:div
   [:h1 "Page Not Found"]])

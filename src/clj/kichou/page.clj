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
        ;; (for [provider providers]
        ;;   [:tr
        ;;    [:td (:providers/abn provider)]
        ;;    [:td (:providers/provider_name provider)]
        ;;    [:td (:providers/location provider)]
        ;;    [:td (:providers/website provider)]
        ;;    [:td
        ;;     [:a {:href (str "/provider/" (:providers/abn provider))} "View"]]])
        ]]
      [:h2 "Add New Provider"]
      [:form {:action "/add-provider" :method "post"}
       [:div [:label "ABN: "] [:input {:type "text" :name "abn" :required true}]]
       [:div [:label "Name: "] [:input {:type "text" :name "full-brand-name" :required true}]]
       [:div [:label "Location: "] [:input {:type "text" :name "location"}]]
       [:div [:label "Website: "] [:input {:type "text" :name "website"}]]
       [:div [:input {:type "submit" :value "Add Provider"}]]]])))

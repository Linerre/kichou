(ns kichou.pages.home
  "The home page for backend"
  (:require [shadow.css :refer (css)]
            [hiccup2.core :as h]))

(defn home []
  (-> [:html
       [:body
        [:h1 {:class (css {:color "green"})} "Kichou Home"]]]
      (h/html)
      (str)))

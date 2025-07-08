(ns kichou.route
  (:require
   [reitit.ring :as rr]
   [kichou.handler :as h]))

(def routes
  [["/" {:get h/main-home}]
   ["/providers" {:get (fn [req] (h/providers-info req))}]])

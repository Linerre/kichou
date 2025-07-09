(ns kichou.route
  (:require
   [ring.middleware.params :refer [wrap-params]]
   [reitit.ring :as rr]
   [kichou.handler :as h]))

(def router
  (rr/router
    [["/" {:get h/main-home}]
     ["/providers" {:get (fn [req] (h/providers-info req))}]]
    {:middleware [wrap-params]}))

(ns kichou.handler
  (:require
   [kichou.page :as page]))


(defn main-home [_]
  {:status  200,
   :headers {"Content-Type" "text/html"}
   :body    (page/home)})

(defn providers-info [req]
  {:status  200,
   :headers {"Content-Type" "text/html"}
   :body    (page/providers)})

(defn not-found []
  {:status  404
   :headers {"Content-Type" "text/html"}
   :body    (page/not-found)})

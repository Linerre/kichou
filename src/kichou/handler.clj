(ns kichou.handler
  (:require
   [ring.util.response :as util]
   [kichou.page :as page]))


(defn main-home [_]
  {:status  200,
   :headers {"Content-Type" "text/html"}
   :body    (page/home)})

(defn providers-info [_req]
  {:status  200,
   :headers {"Content-Type" "text/html"}
   :body    (page/providers)})

(defn not-found []
  (util/not-found))

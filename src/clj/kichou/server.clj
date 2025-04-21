(ns kichou.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [hiccup2.core :as h]
            [shadow.css :refer (css)])
  (:gen-class))

;; To use a atom as server, see: https://gist.github.com/plexus/19ef2874d9f0c56e458e78c2e1103f16
#_(defonce server (atom nil))


;; home page for server and dev
(defn home []
  (-> [:html
       [:body
        [:h1 {:class (css {:color "blue" :text-align "center"})} "Kichou Backend"]]]
      (h/html)
      (str)))

(defn handler [_]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (home)})

;;; Remove the dev-handler once dev is done

(defn -main [& args]
  (jetty/run-jetty handler {:port  3210,
                            :join? false}))

(comment (-main))

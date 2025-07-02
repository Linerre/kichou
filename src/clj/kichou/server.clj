(ns kichou.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [hiccup2.core :as h]
            [shadow.css :refer [css]])
  (:gen-class))

;; To use a atom as server, see: https://gist.github.com/plexus/19ef2874d9f0c56e458e78c2e1103f16
(defonce server (atom nil))

;; home page for server and dev
(defn home []
  (-> [:html
       [:body
        [:h1 {:class (css {:color "blue" :text-align "center"})} "Hi Kichou"]]]
      (h/html)
      (str)))


(defn handler [_]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (home)})

(defn stop-server! []
  (when @server
    (.stop @server)
    (reset! server nil)
    (println "Server shut down")))

(defn start-server! [port]
  (when @server
    (stop-server!))
  (reset! server (jetty/run-jetty handler {:port port, :join? false}))
  (println "Server started at port: " port))

(defn -main [& args]
    (start-server! 3000))

(comment (-main))

(comment (stop-server!))

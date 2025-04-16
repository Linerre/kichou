(ns kichou.core
  "The server"
  (:require [ring.adapter.jetty as jetty]))

(defonce server (atom nil))

(defn handler [_]
  {:status 200
   :headers {"Context-Type" "text/html"}
   :body "Hello"})

(defn start-jetty! []
  (jetty/run-jetty handler {:port 3210,
                            :join? false}))

(defn stop-jetty! []
  (.stop @server)
  (reset! server nil))

(defn -main [& args]
  (start-jetty!))

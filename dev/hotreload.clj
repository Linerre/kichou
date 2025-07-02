;; dev/hotreload.clj
(ns hotreload
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [kichou.server :refer [handler]])
  (:gen-class))

(defonce server (atom nil))

(def dev-handler
  (wrap-reload #'handler))

(defn stop-server! []
  (when @server
    (.stop @server)
    (reset! server nil)
    (println "Server shut down")))

(defn start-server! [port]
  (when @server
    (stop-server!))
  (reset! server (jetty/run-jetty dev-handler {:port port, :join? false}))
  (println "Server started at port: " port))

(defn -main [& args]
  (start-server! 9000))

(comment (-main))

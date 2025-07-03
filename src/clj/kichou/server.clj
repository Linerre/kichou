(ns kichou.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [reitit.ring :as rr]
            [kichou.route :as route]
            [kichou.handler :refer [not-found]]))

;; To use a atom as server, see: https://gist.github.com/plexus/19ef2874d9f0c56e458e78c2e1103f16
(defonce server (atom nil))

;; home page for server and dev

(defn stop-server! []
  (when @server
    (.stop @server)
    (reset! server nil)
    (println "Server shut down")))


(def app
  (rr/ring-handler
    (rr/router
      route/routes)

    (rr/create-default-handler
      {:not-found not-found})))


(defn start-server! [port]
  (when @server
    (stop-server!))
  (reset! server (jetty/run-jetty (wrap-reload #'app)
                                  {:port port, :join? false}))
  (println "Server started at port: " port))

(defn -main [& args]
    (start-server! 3000))

(comment (-main))

(comment (stop-server!))

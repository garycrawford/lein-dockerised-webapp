(defproject {{ns-name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-jetty-adapter "1.3.2"]
                 [ring/ring-json "0.3.1"]        
                 [ring/ring-defaults "0.1.4"] 
                 [scenic "0.2.3"]
                 [com.stuartsierra/component "0.2.3"]]

  :repl-options {:init-ns user
                 :welcome (println "Type (dev) to start")}

  :profiles {:dev {:source-paths ["dev"]
                   :plugins [[lein-ancient "0.6.3"]
                             [lein-create-template "0.1.1"]]
                   :dependencies [[org.clojure/tools.namespace "0.2.10"]]}
             :uberjar {:aot :all             
                       :main {{ns-name}}.zygote}})

(defproject flat-scraper "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [enlive "1.1.6"]
                 [http-kit "2.2.0"]
                 [com.cemerick/url "0.1.1"]
                 [cheshire "5.7.0"]
                 [com.taoensso/carmine "2.15.1"]
                 [clj-time "0.13.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/core.async "0.2.395"]
                 [compojure "1.5.2"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-jetty-adapter "1.5.1"]
                 [ring/ring-defaults "0.2.2"]
                 [barbotte "0.0.1-SNAPSHOT"]
                 [mount "0.1.11"]]

  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]]
                   :source-paths ["env/dev/src"]}
             :uberjar {:aot :all}})

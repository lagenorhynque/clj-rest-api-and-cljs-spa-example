(defproject todo-api "0.1.0-SNAPSHOT"
  :description "Todo API"
  :url "https://github.com/lagenorhynque/clj-rest-api-and-cljs-spa-example"
  :min-lein-version "2.0.0"
  :dependencies [[duct/core "0.8.0"]
                 [duct/module.ataraxy "0.3.0"]
                 [duct/module.logging "0.5.0"]
                 [duct/module.sql "0.6.0"]
                 [duct/module.web "0.7.0"]
                 [org.clojure/clojure "1.10.1"]
                 [org.postgresql/postgresql "42.2.14"]]
  :plugins [[duct/lein-duct "0.12.1"]]
  :main ^:skip-aot todo-api.main
  :resource-paths ["resources" "target/resources"]
  :prep-tasks     ["javac" "compile" ["run" ":duct/compiler"]]
  :middleware     [lein-duct.plugin/middleware]
  :profiles
  {:dev  [:project/dev :profiles/dev]
   :repl {:prep-tasks   ^:replace ["javac" "compile"]
          :repl-options {:init-ns user}}
   :uberjar {:aot :all}
   :profiles/dev {}
   :project/dev  {:source-paths   ["dev/src"]
                  :resource-paths ["dev/resources"]
                  :dependencies   [[eftest "0.5.9"]
                                   [fipp "0.6.23"]
                                   [hawk "0.2.11"]
                                   [integrant/repl "0.3.1"]
                                   [kerodon "0.9.1"]]}})

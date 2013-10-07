(defproject conveyor-clojurescript "0.2.0"
  :description "Clojurescript plugin for conveyor"
  :url "https://github.com/mylesmegyesi/conveyor"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-1859"
                  :exclusions [org.apache.ant/ant]]
                 [conveyor "0.2.0"]]

  :profiles {:dev {:dependencies [[speclj "2.6.1"]]
                   :main speclj.main
                   :aot [speclj.main]
                   :plugins [[speclj "2.6.1"]]
                   :test-paths ["spec"]}}

  :scm {:name "git"
        :url "https://github.com/mylesmegyesi/conveyor"
        :dir "conveyor-clojurescript"}

  )

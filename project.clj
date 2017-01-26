(defproject rogue-prototype "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[com.google.guava/guava "19.0"], [com.google.inject/guice "3.0"], [org.slick2d/slick2d-core "1.0.2"]]
  :main unfinished-story.core
  :profiles {:dev {:dependencies [[junit/junit "4.11"]]}}
  :java-source-paths ["src", "test"]
  :target-path "target/%s"
  :plugins [[lein-junit "1.1.8"]]
  :junit ["test"]
  :javac-options ["-target" "1.8" "-source" "1.8"])

(ns conveyor.static-asset-finder-spec
  (:require [speclj.core :refer :all]
            [conveyor.config :refer :all]
            [conveyor.core :refer [find-asset]]))

(describe "conveyor.static-asset-finder"

  (with config (thread-pipeline-config
                 (set-search-strategy :static)
                 (set-output-dir "test_fixtures/output")
                 (set-manifest "test_fixtures/output/manifest1.edn")))

  (it "finds an asset in the output directory"
    (let [found-asset (find-asset @config "test1.js")]
      (should= "test1.js" (:logical-path found-asset))))

  (it "finds an asset in the output directory with a prefix"
      (let [config (set-manifest (add-prefix @config "/assets")
                                 "test_fixtures/output/manifest2.edn")
          found-asset (find-asset config "test1.js")]
      (should= "/assets/test1.js" (:logical-path found-asset))))

  (it "throws an exception if the requested file is not in the manifest"
    (should-throw
      Exception
      "unknown.js is not in the manifest \"test_fixtures/output/manifest1.edn\". It has not been precompiled."
      (find-asset @config "unknown.js")))

  )

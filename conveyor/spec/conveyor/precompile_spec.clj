(ns conveyor.precompile-spec
  (:require [speclj.core :refer :all]
            [clojure.java.io :refer [file input-stream copy]]
            [clojure.edn :refer [read-string] :rename {read-string read-edn}]
            [conveyor.config :refer :all]
            [conveyor.core :refer [with-pipeline-config pipeline-config]]
            [conveyor.precompile :refer :all]
            [conveyor.file-utils :refer [read-file read-stream]])
  (:import [org.apache.commons.io FileUtils]
           [java.io ByteArrayOutputStream FileInputStream]
           [java.util.zip GZIPInputStream]))

(describe "conveyor.precompile"

  (around [it]
    (let [config (thread-pipeline-config
                   (set-output-dir "test_output")
                   (add-directory-to-load-path "test_fixtures/public/images")
                   (add-directory-to-load-path "test_fixtures/public/javascripts")
                   (add-directory-to-load-path "test_fixtures/public/stylesheets"))]
      (with-pipeline-config config
        (try
          (it)
          (finally
            (FileUtils/deleteDirectory (file (:output-dir config)))
            (FileUtils/deleteDirectory (file "test_manifest_dir")))))))

  (it "writes the asset to the output directory"
    (precompile ["test1.js"])
    (should= "var test = 1;\n" (slurp "test_output/test1.js")))

  (it "throws an exception if the asset is not found"
    (should-throw
      Exception
      "Asset not found: unknown.js"
      (precompile ["unknown.js"])))

  (it "includes the prefix in the file name"
    (with-pipeline-config (add-prefix (pipeline-config) "/assets")
      (precompile ["test1.js"])
      (should= "var test = 1;\n" (slurp "test_output/assets/test1.js"))))

  (it "compiles two files"
    (precompile ["test1.js" "test2.css"])
    (should= "var test = 1;\n" (slurp "test_output/test1.js"))
    (should= ".test2 { color: black; }\n" (slurp "test_output/test2.css")))

  (it "compiles a png file"
    (precompile ["joodo.png"])
    (let [png-content (read-file "test_output/joodo.png")]
      (should= 6533 (count png-content))))

  (it "writes the digest file"
    (precompile ["test1.js" "test2.css"])
    (should= "var test = 1;\n" (slurp "test_output/test1-200368af90cc4c6f4f1ddf36f97a279e.js"))
    (should= ".test2 { color: black; }\n" (slurp "test_output/test2-9d7e7252425acc78ff419cf3d37a7820.css")))

  (with manifest-output
        {"test2.css" {:logical-path "test2.css"
                      :digest-path "test2-9d7e7252425acc78ff419cf3d37a7820.css"
                      :digest "9d7e7252425acc78ff419cf3d37a7820"}
         "test1.js" {:logical-path "test1.js"
                     :digest-path "test1-200368af90cc4c6f4f1ddf36f97a279e.js"
                     :digest "200368af90cc4c6f4f1ddf36f97a279e"}})

  (it "writes the manifest file mapping the logical path to the written file"
    (precompile ["test1.js" "test2.css"])
    (let [manifest (read-edn (slurp "test_output/manifest.edn"))]
      (should=
        @manifest-output
        manifest)))

  (it "writes the manifest file without the prefix"
    (with-pipeline-config (add-prefix (pipeline-config) "/assets")
      (precompile ["test1.js" "test2.css"])
      (let [manifest (read-edn (slurp "test_output/assets/manifest.edn"))]
        (should=
          @manifest-output
          manifest))))

  (it "writes the manifest that is specified in the config"
    (with-pipeline-config (set-manifest (pipeline-config) "test_output/test-manifest.edn")
      (precompile ["test1.js" "test2.css"])
      (let [manifest (read-edn (slurp "test_output/test-manifest.edn"))]
        (should=
          @manifest-output
          manifest))))

  (it "creates the manifest file in a directory that does not exist"
    (with-pipeline-config (set-manifest (pipeline-config) "test_manifest_dir/test-manifest.edn")
      (precompile  ["test1.js" "test2.css"])
      (let [manifest (read-edn (slurp "test_manifest_dir/test-manifest.edn"))]
        (should=
          @manifest-output
          manifest))))

  (defn gunzip [file-name]
    (read-stream (GZIPInputStream. (FileInputStream. (file file-name)))))

  (it "gzips the output"
    (precompile ["test1.js"])
    (should= "var test = 1;\n" (gunzip "test_output/test1.js.gz")))

  (it "gzips a png file"
    (precompile ["joodo.png"])
    (should= 6533 (count (gunzip "test_output/joodo.png.gz")))
    (should= 6470 (count (read-file "test_output/joodo.png.gz"))))

  )

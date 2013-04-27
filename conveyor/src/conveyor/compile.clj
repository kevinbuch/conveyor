(ns conveyor.compile
  (:require [clojure.string :refer [join]]))

(defn- throw-multiple-output-exensions-with-no-requested-output-extension [requested-path found-path output-extensions]
  (throw (Exception. (format "Search for \"%s\" found \"%s\". However, you did not request an output extension and the matched compiler has multiple output extensions: %s"
                             requested-path
                             found-path
                             (join ", " output-extensions)))))

(defn- throw-multiple-compilers [input-extension output-extension]
  (throw (Exception. (format "Found multiple compilers to handle input extension \"%s\" and output extension \"%s\""
                             input-extension output-extension))))

(defn- compilers-for-extension [compilers input-extension output-extension]
  (filter
    (fn [compiler]
      (and (some #(= % input-extension) (:input-extensions compiler))
           (if (empty? output-extension)
             true
             (some #(= % output-extension) (:output-extensions compiler)))))
    compilers))

(defn- do-compile [config found-extension asset compiler-fn output-extension]
  (-> asset
    (assoc :body (compiler-fn
                   config
                   (:body asset)
                   (:absolute-path asset)
                   found-extension
                   output-extension))
    (assoc :extension output-extension)))

(defn compile-asset [config path extension asset]
  (let [found-extension (:extension asset)
        compilers (compilers-for-extension (:compilers config) found-extension extension)
        num-compilers (count compilers)
        compiler (first compilers)
        output-extensions (:output-extensions compiler)]
    (cond
      (> num-compilers 1)
      (throw-multiple-compilers found-extension extension)
      (= num-compilers 1)
      (if (empty? extension)
        (if (= 1 (count output-extensions))
          (do-compile config found-extension asset (:compiler compiler) (first output-extensions))
          (throw-multiple-output-exensions-with-no-requested-output-extension
            path (:absolute-path asset) output-extensions))
        (do-compile config found-extension asset (:compiler compiler) extension))
      :else
      asset)))


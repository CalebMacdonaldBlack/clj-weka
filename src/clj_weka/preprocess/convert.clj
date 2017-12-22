(ns clj-weka.preprocess.convert
  (:require [clj-weka.util :as util]
            [clojure.string :as string]
            [clojure.spec.alpha :as spec]
            [clojure.data.csv :as csv]
            [clojure.walk :as walk]
            [orchestra.spec.test :as st]
            [clojure.spec.alpha :as s])
  (:import
    (weka.core Instances)
    (weka.core.converters CSVLoader ArffSaver ArffLoader CSVSaver)
    (java.io InputStream OutputStream ByteArrayOutputStream)))

(defn- convert
  "converts string dataset"
  [loader saver text]
  (let [_ (.setSource loader ^InputStream (util/string->stream text))
        instances (.getDataSet loader)
        _ (.setInstances saver instances)
        output-stream (ByteArrayOutputStream.)
        _ (.setDestination saver output-stream)
        _ (.writeBatch saver)]
    (String. (.toByteArray output-stream) "UTF-8")))

(defn arff->csv
  "Converts arff to csv"
  [s]
  (convert (ArffLoader.) (CSVSaver.) s))

(s/fdef arff->csv
        :args (s/cat :s string?)
        :ret string?)

(defn csv->arff
  "Converts csv to arff"
  [s]
  (convert (CSVLoader.) (ArffSaver.) s))

(s/fdef csv->arff
        :args (s/cat :s string?)
        :ret string?)

(defn- convert-type
  "Converts string to another datatype if matches"
  [value]
  (try
    (let [parsed (read-string value)]
      (condp instance? parsed
        Number parsed
        Boolean parsed
        value))
    (catch Exception _
      value)))

(s/fdef convert-type
        :args (s/cat :value string?)
        :ret #(or (string? %)
                  (number? %)
                  (boolean? %)))

(defn csv->edn
  "Converts csv to edn"
  [s]
  (let [[header & rows] (csv/read-csv s)]
    (map #(-> (zipmap header %)
              (walk/keywordize-keys))
         (map #(map convert-type %) rows))))

(s/fdef csv->edn
        :args (s/cat :s string?)
        :ret seq?)

(defn edn->csv
  "Converts edn to csv"
  [coll]
  (let [headers (map name (keys (first coll)))
        rows (map vals coll)
        csv-rows (map (partial string/join ",") rows)
        csv-header (string/join "," headers)]
    (str (string/join "\n" (cons csv-header csv-rows)) "\n")))

(s/fdef edn->csv
        :args (s/cat :coll seq?)
        :ret string?)

(defn edn->arff
  "Converts edn to arff"
  [coll]
  (-> coll edn->csv csv->arff))

(s/fdef edn->arff
        :args (s/cat :coll seq?)
        :ret string?)

(defn arff->edn
  "Converts arff to edn"
  [arff]
  (-> arff arff->csv csv->edn))

(s/fdef arff->edn
        :args (s/cat :arff string?)
        :ret seq?)

(st/instrument)

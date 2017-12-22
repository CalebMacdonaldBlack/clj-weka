(ns clj-weka.preprocess.convert
  (:require [clj-weka.util :as util]
            [clojure.string :as string]
            [clojure.data.csv :as csv]
            [clojure.walk :as walk])
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

(defn csv->arff
  "Converts csv to arff"
  [s]
  (convert (CSVLoader.) (ArffSaver.) s))

(defn convert-type
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


(defn csv->edn
  "Converts csv to edn"
  [s]
  (let [[header & rows] (csv/read-csv s)]
    (map #(-> (zipmap header %)
              (walk/keywordize-keys))
         (map #(map convert-type %) rows))))

(defn edn->csv
  "Converts edn to csv"
  [coll]
  (let [headers (map name (keys (first coll)))
        rows (map vals coll)
        csv-rows (map (partial string/join ",") rows)
        csv-header (string/join "," headers)]
    (str (string/join "\n" (cons csv-header csv-rows)) "\n")))

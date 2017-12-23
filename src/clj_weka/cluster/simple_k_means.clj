(ns clj-weka.cluster.simple-k-means
  (:require [clj-weka.util :as util]
            [clj-weka.preprocess.convert :as convert]
            [clojure.spec.alpha :as s]
            [orchestra.spec.test :as st])
  (:import (weka.clusterers SimpleKMeans)
           (weka.core.converters CSVLoader)
           (java.io InputStream)))

(defn cluster
  "Assigns each row in the dataset to a cluster"
  [dataset
   {seed     :seed
    clusters :clusters
    :or      {seed 10}}]
  (let [kmeans (SimpleKMeans.)
        _ (.setPreserveInstancesOrder kmeans true)
        _ (.setNumClusters kmeans clusters)
        loader (CSVLoader.)
        _ (.setSource loader ^InputStream (util/string->stream (convert/edn->csv dataset)))
        instances (.getDataSet loader)
        _ (.buildClusterer kmeans instances)
        assignments (.getAssignments kmeans)]
    (map #(map second %)
         (vals (group-by first (map vector assignments dataset))))))

(s/def ::seed int?)
(s/def ::clusters int?)

(s/fdef cluster
        :args (s/cat :dataset (s/and (s/coll-of (s/map-of keyword? any?))
                                     ffirst)
                     :options (s/keys :req-un [::clusters]
                                      :opt-un [::seed])))

(st/instrument)

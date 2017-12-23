(ns clj-weka.cluster.simple-k-means
  (:require [clj-weka.util :as util]
            [clj-weka.preprocess.convert :as convert]
            [clojure.spec.alpha :as s]
            [orchestra.spec.test :as st])
  (:import (weka.clusterers SimpleKMeans)
           (weka.core.converters CSVLoader)
           (java.io InputStream)
           (weka.core EuclideanDistance ManhattanDistance SelectedTag)))

(defn- get-distance-function
  [distance-function]
  (case distance-function
    :euclidean-distance (EuclideanDistance.)
    :manhattan-distance (ManhattanDistance.)))

(defn- get-initialization-method
  [method]
  (case method
    :random (SelectedTag. (SimpleKMeans/RANDOM) (SimpleKMeans/TAGS_SELECTION))
    :k-means++ (SelectedTag. (SimpleKMeans/KMEANS_PLUS_PLUS) (SimpleKMeans/TAGS_SELECTION))
    :canopy (SelectedTag. (SimpleKMeans/CANOPY) (SimpleKMeans/TAGS_SELECTION))
    :furthest-first (SelectedTag. (SimpleKMeans/FARTHEST_FIRST) (SimpleKMeans/TAGS_SELECTION))))

(defn cluster
  "Returns the a seq of the cluster numbers assigned."
  [dataset
   {seed                                :seed
    clusters                            :clusters
    max-canopies-hold-in-mem            :max-canopies-hold-in-mem
    min-canopy-density                  :min-canopy-density
    canopy-periodic-pruning-rate        :canopy-periodic-pruning-rate
    canopy-t1                           :canopy-t1
    canopy-t2                           :canopy-t2
    distance-fn                         :distance-fn
    replace-missing-vals?               :replace-missing-vals?
    fast-distance-calc?                 :fast-distance-calc?
    initialization-method               :initialization-method
    max-iterations                      :max-iterations
    execution-slots                     :execution-slots
    reduce-distance-calcs-via-canopies? :reduce-distance-calcs-via-canopies
    :or                                 {seed                                10
                                         max-canopies-hold-in-mem            100
                                         min-canopy-density                  2.0
                                         canopy-periodic-pruning-rate        10000
                                         canopy-t1                           -1.25
                                         canopy-t2                           -1.0
                                         distance-fn                         :euclidean-distance
                                         replace-missing-vals?               true
                                         fast-distance-calc?                 false
                                         initialization-method               :random
                                         max-iterations                      500
                                         execution-slots                     1
                                         reduce-distance-calcs-via-canopies? false}}]
  (let [kmeans (SimpleKMeans.)
        _ (.setPreserveInstancesOrder kmeans true)
        _ (.setNumClusters kmeans clusters)
        _ (.setCanopyMaxNumCanopiesToHoldInMemory kmeans max-canopies-hold-in-mem)
        _ (.setCanopyMinimumCanopyDensity kmeans min-canopy-density)
        _ (.setCanopyPeriodicPruningRate kmeans canopy-periodic-pruning-rate)
        _ (.setCanopyT1 kmeans canopy-t1)
        _ (.setCanopyT2 kmeans canopy-t2)
        _ (.setDistanceFunction kmeans (get-distance-function distance-fn))
        _ (.setDontReplaceMissingValues kmeans (not replace-missing-vals?))
        _ (.setFastDistanceCalc kmeans fast-distance-calc?)
        _ (.setInitializationMethod kmeans (get-initialization-method initialization-method))
        _ (.setMaxIterations kmeans max-iterations)
        _ (.setNumExecutionSlots kmeans execution-slots)
        _ (.setReduceNumberOfDistanceCalcsViaCanopies kmeans reduce-distance-calcs-via-canopies?)
        loader (CSVLoader.)
        _ (.setSource loader ^InputStream (util/string->stream (convert/edn->csv dataset)))
        instances (.getDataSet loader)
        _ (.buildClusterer kmeans instances)]
    (seq (.getAssignments kmeans))))

(s/def ::seed int?)
(s/def ::clusters int?)
(s/def ::max-canopies-hold-in-mem int?)
(s/def ::min-canopy-density number?)
(s/def ::canopy-periodic-pruning-rate int?)
(s/def ::canopy-t1 number?)
(s/def ::canopy-t2 number?)
(s/def ::distance-fn #{:euclidean-distance :manhattan-distance})
(s/def ::replace-missing-vals? boolean?)
(s/def ::fast-distance-calc? boolean?)
(s/def ::initialization-method #{:random :k-means++ :canopy :furthest-first})
(s/def ::max-iterations int?)
(s/def ::execution-slots int?)
(s/def ::reduce-distance-calcs-via-canopies boolean?)

(s/fdef cluster
        :args (s/cat :dataset (s/and (s/coll-of (s/map-of keyword? any?))
                                     ffirst)
                     :options (s/keys :req-un [::clusters]
                                      :opt-un [::seed
                                               ::max-canopies-hold-in-mem
                                               ::min-canopy-density
                                               ::canopy-periodic-pruning-rate
                                               ::canopy-t1
                                               ::canopy-t2
                                               ::distance-fn
                                               ::replace-missing-vals?
                                               ::fast-distance-calc?
                                               ::initialization-method
                                               ::max-iterations
                                               ::execution-slots
                                               ::reduce-distance-calcs-via-canopies])))

(st/instrument)

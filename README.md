# clj-weka

Clojure wrapper for the java machine learning library weka

[![Clojars Project](https://img.shields.io/clojars/v/clj-weka.svg)](https://clojars.org/clj-weka)

## Usage

Convert between csv, arff and edn

```clojure
(require '[clj-weka.preprocess.convert :as convert])

(-> "data.arff" slurp arff->csv)

(-> "data.csv" slurp csv->arff)

(-> "data.csv" slurp csv->edn)

(edn->csv [{:a 1 :b "hello"}])

(edn->arff [{:a 1 :b "hello"}])

(-> "data.arff" slurp arff->edn)
```

Simple K Means clustering

```clojure
(require '[clj-weka.cluster.simple-k-means :as simple-k-means])

(simple-k-means/cluster '({:a 1} {:a 1} {:a 5}) {:clusters 2})
; => (0 0 1)

(simple-k-means/cluster 
  '({:a 1} {:a 1} {:a 5})
  {:clusters                           2
   :seed                               10
   :max-canopies-hold-in-mem           100})
   :min-canopy-density                 2.0
   :canopy-periodic-pruning-rate       10000
   :canopy-t1                          -1.25
   :canopy-t2                          -1.0
   :distance-fn                        :euclidean-distance
   :replace-missing-vals?              true
   :fast-distance-calc?                false
   :initialization-method              :random
   :max-iterations                     500
   :execution-slots                    1
   :reduce-distance-calcs-via-canopies false
; => (0 0 1)
```

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

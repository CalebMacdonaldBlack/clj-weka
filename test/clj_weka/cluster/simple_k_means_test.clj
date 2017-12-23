(ns clj-weka.cluster.simple-k-means-test
  (:require [clojure.test :refer :all]
            [clj-weka.cluster.simple-k-means :as simple-k-means]))

(deftest cluster-test
  (testing "Clusters correctly"
    (is (= '(({:a 1} {:a 1}) ({:a 5}))
           (simple-k-means/cluster '({:a 1} {:a 1} {:a 5}) {:clusters 2})))))

(run-tests 'clj-weka.cluster.simple-k-means-test)

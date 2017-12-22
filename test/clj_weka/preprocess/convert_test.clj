(ns clj-weka.preprocess.convert-test
  (:require [clojure.test :refer :all]
            [clj-weka.preprocess.convert :as convert]))

(deftest csv->arff-test
  (testing "Converts csv to arff"
    (is (= "@relation stream\n\n@attribute A numeric\n@attribute B {hello}\n\n@data\n1,hello\n"
           (convert/csv->arff "A,B\n1,hello\n")))))

(deftest arff->csv-test
  (testing "Converts arff to csv"
    (is (= "A,B\n1,hello\n"
           (convert/arff->csv "@relation stream\n\n@attribute A numeric\n@attribute B {hello}\n\n@data\n1,hello\n")))))

(deftest csv->edn-test
  (testing "Converts csv to edn"
    (is (= '({:a 1
              :b "hello"})
           (convert/csv->edn "a,b\n1,hello\n")))))

(run-tests 'clj-weka.preprocess.convert-test)


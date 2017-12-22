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
    (is (= '({:a 1 :b "hello"})
           (convert/csv->edn "a,b\n1,hello\n")))))

(deftest edn->csv-test
  (testing "Converts edn to csv"
    (is (= "a,b\n1,hello\n"
           (convert/edn->csv '({:a 1 :b "hello"}))))))

(deftest edn->arff-test
  (testing "Converts edn to arff"
    (is (= "@relation stream\n\n@attribute a numeric\n@attribute b {hello}\n\n@data\n1,hello\n"
           (convert/edn->arff '({:a 1 :b "hello"}))))))

(deftest arff->edn-test
  (testing "Converts arff to edn"
    (is (= '({:A 1, :B "hello"})
           (convert/arff->edn "@relation stream\n\n@attribute A numeric\n@attribute B {hello}\n\n@data\n1,hello\n")))))

(run-tests 'clj-weka.preprocess.convert-test)


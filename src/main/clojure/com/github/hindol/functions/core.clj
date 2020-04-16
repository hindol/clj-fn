(ns com.github.hindol.functions.core
  (:require
   [clojure.walk :as walk])
  (:import
   (com.microsoft.azure.functions ExecutionContext
                                  HttpRequestMessage
                                  HttpResponseMessage
                                  HttpStatus)))

(defn body
  [request]
  (let [maybe-body (.getBody request)]
    (when (.isPresent maybe-body)
      (.get maybe-body))))

(defn query-map
  [request]
  (->> (.getQueryParameters request)
       (into {})
       walk/keywordize-keys))

(defn response
  [request status body]
  (-> (.createResponseBuilder request (HttpStatus/valueOf status))
      (.body body)
      .build))

(defn run
  ^HttpResponseMessage [^HttpRequestMessage request ^ExecutionContext _context]
  (if-let [name (or (body request)
                    (:name (query-map request)))]
    (response request 200 (str "Hello, " name ", from Clojure!"))
    (response request 400 "Please pass a name on the query string or in the request body")))
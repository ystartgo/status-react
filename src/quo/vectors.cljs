(ns quo.vectors
  (:refer-clojure :exclude [set delay divide])
  (:require ["react-native-redash" :as redash]))

(def create       (-> ^js redash .-vec .-create))
(def create-value (-> ^js redash .-vec .-createValue))
(def minus        (-> ^js redash .-vec .-minus))
(def add          (-> ^js redash .-vec .-add))
(def sub          (-> ^js redash .-vec .-sub))
(def dot          (-> ^js redash .-vec .-dot))
(def multiply     (-> ^js redash .-vec .-multiply))
(def divide       (-> ^js redash .-vec .-divide))
(def pow          (-> ^js redash .-vec .-pow))
(def sqrt         (-> ^js redash .-vec .-sqrt))
(def set          (-> ^js redash .-vec .-set))
(def clamp        (-> ^js redash .-vec .-clamp))
(def apply*       (-> ^js redash .-vec .-apply))
(def min*         (-> ^js redash .-vec .-min))
(def max*         (-> ^js redash .-vec .-max))
(def cos          (-> ^js redash .-vec .-cos))
(def sin          (-> ^js redash .-vec .-sin))
(def length       (-> ^js redash .-vec .-length))
(def normalize    (-> ^js redash .-vec .-normalize))
(def cross        (-> ^js redash .-vec .-cross))

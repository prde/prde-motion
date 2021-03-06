(ns ru.profitware.prde-motion.core
  (:refer-clojure :exclude [empty? count vector?])
  (:require [ru.profitware.prde-motion.controller :as l-controller]
            [ru.profitware.prde-motion.frame :as l-frame]
            [ru.profitware.prde-motion.pointable :as l-pointable]
            [ru.profitware.prde-motion.hand :as l-hand]
            [ru.profitware.prde-motion.gestures :as l-gesture]
            [ru.profitware.prde-motion.screen :as l-screen]
            [ru.profitware.prde-motion.protocols :as l-protocols]
            [ru.profitware.prde-motion.extended-protos :as l-eprotos])
  (:import (com.leapmotion.leap Controller
                                Listener
                                Frame
                                Hand Finger Gesture Tool Pointable
                                Vector)))


;; Predicates
;;;;;;;;;;;;;;;;
(defn controller? [potential-controller]
  (instance? Controller potential-controller))

(defn listener? [potential-listener]
  (instance? Listener potential-listener))

(defn frame? [potential-frame]
  (instance? Frame potential-frame))

(defn hand? [potential-hand]
  (instance? Hand potential-hand))

(defn gesture? [gesture]
  (instance? Gesture gesture))

(defn pointable? [potential-pointable]
  (instance? Pointable potential-pointable))

(defn finger? [potential-finger]
  (or (instance? Finger potential-finger)
      (and (pointable? potential-finger) (l-pointable/finger? potential-finger))))

(defn tool? [potential-tool]
  (or (instance? Tool potential-tool)
      (and (pointable? potential-tool) (l-pointable/tool? potential-tool))))

(defn vector? [potential-vector]
  (instance? Vector potential-vector))

(def valid? l-protocols/valid?)


;; Frame
;;;;;;;;;;
(defn frame [t & [history-count]]
  (cond
    (controller? t) (l-controller/frame t history-count)
    (pointable? t)  (l-pointable/frame t)
    (hand? t)       (l-hand/frame t)))

(def frames l-controller/frames)

;; Hands
;;;;;;;;;;;
(def hands? l-frame/hands?)
(def hands l-frame/hands)
(def raw-hand l-frame/raw-hand)
(defn ^Hand hand
  ([^Pointable pointable]
   (l-pointable/hand pointable))
  ([^Frame frame hand-id]
   (l-frame/hand frame hand-id)))

(def single-hand? l-frame/single-hand?)
(def leftmost-hand l-frame/leftmost-hand)
(def rightmost-hand l-frame/rightmost-hand)
(def highest-hand l-frame/highest-hand)
(def lowest-hand l-frame/lowest-hand)

(def same-hand? l-hand/equal?)
(def same-screen? l-screen/equal?)
(def same-pointable? l-pointable/equal?)

;; NOTE: The following use protocols
;; To get raw performance (and type hints) please use the specific functions
;; directly in the namespaces.
;; The protocol functions below are for ease of use and quick prototyping only.

;; Fingers
;;;;;;;;;;;;
(def fingers? l-protocols/fingers?)
(def fingers l-protocols/fingers)
(def raw-finger l-protocols/raw-finger)
(def finger l-protocols/finger)
(def leftmost-finger l-protocols/leftmost-finger)
(def rightmost-finger l-protocols/rightmost-finger)
(def highest-finger l-protocols/highest-finger)
(def lowest-finger l-protocols/lowest-finger)

;; Tools
;;;;;;;;;
(def tools? l-protocols/tools?)
(def tools l-protocols/tools)
(def raw-tool l-protocols/raw-tool)
(def tool l-protocols/tool)
(def leftmost-tool l-protocols/leftmost-tool)
(def rightmost-tool l-protocols/rightmost-tool)
(def highest-tool l-protocols/highest-tool)
(def lowest-tool l-protocols/lowest-tool)

;; Pointables
;;;;;;;;;;;;;;
(def pointables? l-protocols/pointables?)
(def pointables l-protocols/pointables)
(def raw-pointable l-protocols/raw-pointable)
(def pointable l-protocols/pointable)
(def leftmost-pointable l-protocols/leftmost-pointable)
(def rightmost-pointable l-protocols/rightmost-pointable)
(def highest-pointable l-protocols/highest-pointable)
(def lowest-pointable l-protocols/lowest-pointable)

;; Gestures
;;;;;;;;;;;
(def gestures? l-frame/gestures?)
(def gestures l-frame/gestures)
(def raw-gesture l-frame/raw-gesture)

;; Leap *Lists
;;;;;;;;;;;;;;;
(def empty? l-protocols/empty?)
(def count l-protocols/count)

;; Movement Helpers
;;;;;;;;;;;;;;;;;;;;
(defn movement?
  ([t])
  ([t direction-kw]))

;; Listeners and Controllers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn map->listener
  "Given a map with the keys- :init, :connect, :disconnect, :exit, :frame
  Produce a Listener object.
  The functions of the Listener object (those passed in as the values of the map)
  accept a single map as their argument, with the keys- :controller, :frame, :screen.
  This differs from the standard Leap API, which only passes in the controller."
  [handlers]
  (proxy [Listener] []
    (onInit [controller]
      ((:init handlers (:default handlers identity)) (assoc (l-controller/controller-map controller this)
                                                            :state :init)))
    (onConnect [controller]
      ((:connect handlers (:default handlers identity)) (assoc (l-controller/controller-map controller this)
                                                               :state :connect)))
    (onDisconnect [controller]
      ((:disconnect handlers (:default handlers identity)) (assoc (l-controller/controller-map controller this)
                                                                  :state :disconnect)))
    (onExit [controller]
      ((:exit handlers (:default handlers identity)) (assoc (l-controller/controller-map controller this)
                                                            :state :exit)))
    (onFrame [controller]
      ((:frame handlers (:default handlers identity)) (l-controller/controller-map controller this)))))

(defn listener [& on-actions]
  (let [handlers (apply hash-map on-actions)]
    (map->listener handlers)))

(defn add-listener! [^Controller controller ^Listener listener]
  (.addListener controller listener))

(defn remove-listener! [^Controller controller ^Listener listener]
  (.removeListener controller listener))

(defn attach-listeners!
  "Attach many listeners (leap.Listeners) to a controller.
  You pass in a list/seq/vector of listeners,
  or a varags list of listeners"
  [controller & listener-objs]
  (when-let [listener-objs (if (seq? (first listener-objs))
                             (first listener-objs) listener-objs)]
    (interleave listener-objs
                (doall (map #(add-listener! controller %) listener-objs)))))

(defn controller
  "Create a controller and optionally hook up listeners to it.
  Listeners can just be maps or leap.Listener objects.
  If they are hash-maps, they will be converted to leap.Listener objects
  via then `map->listener` function
  This returns a Vector: [controller-obj list-of-listeners]
  This design choice is to stop the JVM from GC'ing listeners out from underneath you."
  [& listeners]
  {:pre [(every? #(or (map? %) (listener? %)) listeners)]}
  (let [listener-objs (map #(if (map? %) (map->listener %) %) listeners)
        controller (Controller.)]
    (when (seq listener-objs)
      (attach-listeners! controller listener-objs))
    [controller listener-objs]))

(comment
 
  )


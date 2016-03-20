(ns ru.profitware.prde-motion.gestures
  (:refer-clojure :exclude [empty? count])
  (:require [ru.profitware.prde-motion.pointable :as l-pointable]
            [ru.profitware.prde-motion.vector :as l-vector])
  (:import (com.leapmotion.leap Controller
                                Frame
                                Hand HandList
                                Gesture Gesture$Type Gesture$State
                                GestureList GestureList$GestureListIterator
                                Pointable PointableList)))

;; Gesture Types
(def gesture-circle Gesture$Type/TYPE_CIRCLE)
(def gesture-key-tap Gesture$Type/TYPE_KEY_TAP)
(def gesture-screen-tap Gesture$Type/TYPE_SCREEN_TAP)
(def gesture-swipe Gesture$Type/TYPE_SWIPE)


;; Gesture States
(def gesture-start Gesture$State/STATE_START)
(def gesture-update Gesture$State/STATE_UPDATE)
(def gesture-stop Gesture$State/STATE_STOP)

;; Enable Gestures
(defn enable-gesture [^Controller controller gesture-type]
  (.enableGesture controller gesture-type))

(defn gesture-enabled? [^Controller controller gesture-type]
  (.isGestureEnabled controller gesture-type))


;; Gesture List
;;;;;;;;;;;;;
;;    We won't support IFn lookup, but it should work with `nth`
(defn count [^GestureList gesture-list]
  (.count gesture-list))

(defn empty? [^GestureList gesture-list]
  (.isEmpty gesture-list))



;; Gesture
;;;;;;;;;;;;;
(defn ^Frame frame [^Gesture gesture]
  (.frame gesture))

(defn valid? [^Gesture gesture]
  (.isValid gesture))

(defn duration [^Gesture gesture]
  (.duration gesture))

(defn duration-seconds [^Gesture gesture]
  (.durationSeconds gesture))

(defn ^Gesture$State state [^Gesture gesture]
  (.state gesture))

(defn ^Gesture$Type g-type [^Gesture gesture]
  (.type gesture))

(comment defn palm [^Gesture gesture]
  {:normal (.palmNormal gesture) ; The vector outward/orthog
   :position (.palmPosition gesture) ; The center of the palm
   :velocity (.palmVelocity gesture)
   :direction (.direction gesture)})

(defn equal? [^Gesture gesture ^Gesture other]
  (.equals gesture other))

(defn equal? [^Gesture$Type gesture-type ^Gesture$Type other]
  (.equals gesture-type other))

(defn equal? [^Gesture$State gesture-state ^Gesture$State other]
  (.equals gesture-state other))


(defn ^HandList hands [^Gesture gesture]
  (.hands gesture))

(defn hands? [^Gesture gesture]
  (not (.isEmpty (.hands gesture))))


(defn ^PointableList pointables [^Gesture gesture]
  (.pointables gesture))

(defn pointables? [^Gesture gesture]
  (not (.isEmpty (.pointables gesture))))

(defn ^Pointable raw-pointable [^Gesture gesture pointable-id]
  (.pointable gesture pointable-id))

(defn pointable [^Gesture gesture pointable-id]
  {:pre [(integer? pointable-id)]}
  (let [pointable (.pointable gesture pointable-id)]
    (when (.isValid pointable)
      pointable)))

(defn ^Pointable leftmost-pointable [^Gesture gesture]
  (when (pointables? gesture)
    (apply min-key #(-> % l-pointable/tip-position l-vector/x) (pointables gesture))))

(defn ^Pointable rightmost-pointable [^Gesture gesture]
  (when (pointables? gesture)
    (apply max-key #(-> % l-pointable/tip-position l-vector/x) (pointables gesture))))

(defn ^Pointable highest-pointable [^Gesture gesture]
  (when (pointables? gesture)
    (apply max-key #(-> % l-pointable/tip-position l-vector/y) (pointables gesture))))

(defn ^Pointable lowest-pointable [^Gesture gesture]
  (when (pointables? gesture)
    (apply min-key #(-> % l-pointable/tip-position l-vector/y) (pointables gesture))))

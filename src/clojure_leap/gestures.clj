(ns clojure-leap.gestures
  (:require [clojure-leap.core :as leap]))

;; This is a preliminary library for basic gestures that
;; developers would commonly want to detect.
;;
;; THIS IS ALPHA AND COULD BE REMOVED IN THE FUTURE

(def ^:dynamic *window* 10)
(def ^:dynamic *threshold* (* 1.5 *window*))

;; TODO: no or VERY little velocity on the Z axis
(defn finger-flash?
  "Can we detect a user flashing all fingers on a single hand?
  We need to be at a frame with 0 fingers,
  where there 4 or more fingers in a previous frames.
  This is essentially the motion of flashing your fingers out,
  and then making a fist"
  [frame-vec]
  (let [latest-frame (first frame-vec)
        past-frames (nnext frame-vec) ;; We skip the second frame to filter our noise
        ;one-max-hand? (= 1 (apply max (map #(leap/count (leap/hands %)) past-frames)))
        one-max-hand? (every? leap/single-hand? past-frames)
        flashed-fingers? (>= (apply max (map #(leap/count (leap/fingers %)) past-frames))
                           4)
        currently-no-fingers? (not (leap/fingers? latest-frame))]
    (and one-max-hand? flashed-fingers? currently-no-fingers?)))

(defn punch?
  "Can we detect a user punching? - a fist moving perpendicular to the palm direction"
  [frame-vec])

(defn bop?
  "Can we detect a user bopping their hand - like the motion for 'rock' in rock-paper-scissors"
  [frame-vec])

(defn pressing-the-leap?
  "Can we detect the user making a motion like they're pressing the Leap Motion"
  [frame-vec])

(defn finger-click?
  "Can we detect the user clicking with their primary finger?"
  [frame-vec])

;; The "Slide" is the the hand totally flat (like a karate chop).
;; "slide-down" is the same as "hand-press-down"

(defn slide-left?
  "Can we detect the user is moving a chop-hand mostly left?"
  [frame-vec])

(defn slide-right?
  "Can we detect the user is moving a chop-hand mostly right?"
  [frame-vec])

(defn left-slide-left?
  "Can we detect the user is making a chop-hand with the leftmost-hand, and moving it mostly left?"
  [frame-vec])

(defn left-slide-right?
  "Can we detect the user is making a chop-hand with the leftmost-hand, and moving it mostly right?"
  [frame-vec])

(defn right-slide-left?
  "Can we detect the user is making a chop-hand with the rightmost-hand, and moving it mostly left?"
  [frame-vec])

(defn right-slide-right?
  "Can we detect the user is making a chop-hand with the rightmost-hand, and moving it mostly right?"
  [frame-vec])

(defn chop?
  "Can we detect the user is making a karate chop motion?"
  [frame-vec])

(defn left-chop?
  "Can we detect the user is making a karate chop motion with the leftmost-hand?"
  [frame-vec])

(defn right-chop?
  "Can we detect the user is making a karate chop motion with the rightmost-hand?"
  [frame-vec])

(defn slide-down?
  "Can we detect the user with a horizontal/palm-down chop-hand, moving mostly downwards?"
  [frame-vec])

(defn slide-up?
  "Can we detect the user with a horizontal/palm-up chop-hand, moving mostly upwards?"
  [frame-vec])

(defn slide-together?
  "Can we detect the user with vertical chop-hands, moving closer together along the x-axis; crushing the can?"
  [frame-vec])

(defn slide-apart?
  "Can we detect the user with vertical chop-hands, moving apart along the x-axis; open the curtains; splitting the sea?"
  [frame-vec])

(defn pull-apart?
  "Can we detect the user with two fists, moving apart along the x-axis?"
  [frame-vec])

(defn tear?
  "Can we detect the user with two fists, moving apart along the z-axis"
  [frame-vec])


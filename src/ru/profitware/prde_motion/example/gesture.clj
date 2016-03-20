(ns ru.profitware.prde-motion.example.gesture
  (:require [ru.profitware.prde-motion.core :as leap]
            [ru.profitware.prde-motion.gestures :as g]))

(defn connect-controller [controller]
  (doto controller
    (g/enable-gesture g/gesture-circle)
    (g/enable-gesture g/gesture-key-tap)
    (g/enable-gesture g/gesture-screen-tap)
    (g/enable-gesture g/gesture-swipe)))

(defn handle-gesture [gesture]
  (if (g/equal? g/gesture-stop (g/state gesture))
    (println
     (condp g/equal? (g/g-type gesture)
       g/gesture-circle "Circle"
       g/gesture-key-tap "Key Tap"
       g/gesture-screen-tap "Screen Tap"
       g/gesture-swipe "Swipe"))))

(defn process-frame [frame]
  (doall 
   (map handle-gesture
        (iterator-seq (.iterator (leap/gestures frame))))))

(defn -main [& args]
  (let [listener (leap/listener :frame #(process-frame (:frame %))
                                :connect #(connect-controller (:controller %))
                                :default #(println "Toggling" (:state %) "for listener:" (:listener %)))
        [controller _] (leap/controller listener)]
    (println "Press Enter to quit")
    (read-line)
    (leap/remove-listener! controller listener)))


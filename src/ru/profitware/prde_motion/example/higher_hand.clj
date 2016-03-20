(ns ru.profitware.prde-motion.example.higher-hand
  (:require [ru.profitware.prde-motion.core :as leap]))

(defn process-frame [frame]
  (println
    (cond
      (not (leap/hands? frame)) "No hands are present"
      (leap/single-hand? frame) "  -- There is only one hand --  "
      (leap/same-hand? (leap/highest-hand frame) (leap/rightmost-hand frame)) "     The RIGHT HAND is higher -->>"
      :else "<<-- The LEFT HAND is higher ")))

(defn -main [& args]
  (let [listener (leap/listener :frame #(process-frame (:frame %))
                                :default #(println "Toggling" (:state %) "for listener:" (:listener %)))
        [controller _] (leap/controller listener)]
    (println "Press Enter to quit")
    (read-line)
    (leap/remove-listener! controller listener)))


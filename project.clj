(defproject prde_motion "0.1.0-SNAPSHOT"
  :description "A Clojure API for the Leap Motion"
  :url "https://github.com/prde/prde-motion"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]]
  :resource-paths ["leap_lib/LeapJava.jar" "resources"]
  
  
  :jvm-opts  [~(str "-Djava.library.path=leap_lib/x64:" (System/getenv "LD_LIBRARY_PATH"))]
  :main ru.profitware.prde-motion.example.higher-hand)

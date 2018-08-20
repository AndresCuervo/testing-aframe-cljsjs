(ns testing-aframe-cljsjs.core
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [cljsjs.aframe :as aframe]))

(defn load-script []
  ;; JS that checks if the script's been attached to the head already
  ;;
  ;; Checking was too cumbersone in CLJS
  (js* "function loadScript(url, callback){
       if (document.querySelectorAll('[src=\"' + url + '\"]').length > 0) {
       return
       }
           var script = document.createElement('script')
           script.type = 'text/javascript';
           if (script.readyState){  //IE
           script.onreadystatechange = function(){
           if (script.readyState == 'loaded' ||
           script.readyState == 'complete'){
           script.onreadystatechange = null;
           callback();
           }
           };
           } else {  //Others
           script.onload = function(){
           callback();
           };
           }
           script.src = url;
           console.log('loaded ' + url )
           document.getElementsByTagName('head')[0].appendChild(script);
           }"))

;; -------------------------
;; Views

(defn home-page []
  [:div
   [:h1 "An A-Frame scene"]
   [:a {:href "/about"} "About page (AR)"]
   [:code {:style {:whiteSpace "pre" :fontSize "2em"}}
    "
[:div.embeddedAframeSceneParent
    [:a-scene {:embedded true}
     [:a-sky {:color \"#2eafac\"}]
     [:a-box {:position \"0 0 -4\"
              :color \"blue\"}
      [:a-box {:color \"red\"
               :position \"2 0 0\"}]]]]
  "
    ]
   [:div.embeddedAframeSceneParent {:style {:maxWidth "55%"}}
    [:a-scene {:embedded true}
     [:a-sky {:color "#2eafac"}]
     [:a-box {:position "0 0 -4"
              :color "blue"}
      [:a-box {:color "red"
               :position "2 0 0"}]]]]])

(defn about-page []
  [:div
   ((load-script) "https://rawgit.com/google-ar/three.ar.js/master/dist/three.ar.js" #())
   ((load-script) "https://rawgit.com/chenzlabs/aframe-ar/master/dist/aframe-ar.js" #())
   [:a.aframeOverlay {:href ".."} "Back"]
   [:a-scene {:ar ""}
    #_[:a-sky {:color "#DAD"}]
    (map
      (fn [n]
        [:a-sphere {
                    :key (str "sphere-number-" n)
                    :radius 0.01
                    :color "red"
                    :position (str (* 0.05 n) " 0 -0.5")}])
      (range 0 10))]])

;; -------------------------
;; Routes

(defonce page (atom #'home-page))

(defn current-page []
  [:div [@page]])

(secretary/defroute "/" []
  (reset! page #'home-page))

(secretary/defroute "/about" []
  (reset! page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))

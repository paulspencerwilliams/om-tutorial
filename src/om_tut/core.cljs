(ns om-tut.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(def app-state (atom {:text "Hello world from Paul!"}))

(om/root
 (fn [app owner]
   (om/component (dom/h2 nil (:text app))))
 app-state
 {:target (. js/document (getElementById "app0"))})

(ns om-tut.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]
            [clojure.data :as data]
            [clojure.string :as string]))

(enable-console-print!)

(def app-state
  (atom
    {:tasks
     [{:first "Ben" :last "Bitdiddle" :email "benb@mit.edu"}
      {:first "Alyssa" :middle-initial "P" :last "Hacker" :email "aphacker@mit.edu"}
      {:first "Eva" :middle "Lu" :last "Ator" :email "eval@mit.edu"}
      {:first "Louis" :last "Reasoner" :email "prolog@mit.edu"}
      {:first "Cy" :middle-initial "D" :last "Effect" :email "bugs@mit.edu"}
      {:first "Lem" :middle-initial "E" :last "Tweakit" :email "morebugs@mit.edu"}]}))

(defn parse-task [task-str]
  (let [[first middle last :as parts] (string/split task-str #"\s+")
        [first last middle] (if (nil? last) [first middle] [first last middle])
        middle (when middle (string/replace middle "." ""))
        c (if middle (count middle) 0)]
    (when (>= (count parts) 2)
      (cond-> {:first first :last last}
        (== c 1) (assoc :middle-initial middle)
        (>= c 2) (assoc :middle middle)))))

(defn add-task [app owner]
  (let [new-task (-> (om/get-node owner "new-task")
                        .-value
                        parse-task)]
    (when new-task
      (om/transact! app :tasks #(conj % new-task)))))

(defn middle-name [{:keys [middle middle-initial]}]
  (cond
    middle (str " " middle)
    middle-initial (str " " middle-initial ".")))

(defn display-name [{:keys [first last] :as task}]
  (str last ", " first (middle-name task)))

(defn task-view [task owner]
  (reify
    om/IRenderState
    (render-state [this {}]
      (dom/li nil
        (dom/span nil (display-name task))))))

(defn tasks-view [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {})

    om/IRenderState
    (render-state [this state]
      (dom/div nil
        (dom/h2 nil "task list")
        (apply dom/ul nil
          (om/build-all task-view (:tasks app)
            {:init-state state}))
        (dom/div nil
          (dom/input #js {:type "text" :ref "new-task"})
          (dom/button #js {:onClick #(add-task app owner)} "Add task"))))))

(om/root tasks-view app-state
  {:target (. js/document (getElementById "tasks"))})

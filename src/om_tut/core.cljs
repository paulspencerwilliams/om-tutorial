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
     [{:title "Organise BCS night" :due "2014-11-04"}
      {:title "Learn pedestal" :due "2014-12-31"}
      {:title "Learn Om" :due "2014-11-03"}]}))

(defn parse-task [task-str]
  (let [[title due :as parts] (string/split task-str #"\s+")]
    {:title title :due due}))

(defn add-task [app owner]
  (let [new-task (-> (om/get-node owner "new-task")
                        .-value
                        parse-task)]
    (when new-task
      (om/transact! app :tasks #(conj % new-task)))))

(defn display-name [{:keys [title due] :as task}]
  (str title " due by " due))

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

 (ns env.android.main
  (:require [status-im.android.core :as core]
            [re-frisk-remote.core :as re-frisk]))

 (re-frisk/enable {:host "192.168.0.16:4567"})
 (core/init)



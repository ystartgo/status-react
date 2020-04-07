 (ns env.android.main
  (:require [status-im.android.core :as core]
            [re-frisk-remote.core :as rr]))


 (rr/enable-re-frisk-remote! {:host (env.utils/re-frisk-url "192.168.0.16")})

 (core/init)



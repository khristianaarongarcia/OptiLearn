package com.lokixcz.optilearn

import android.app.Application
import com.lokixcz.optilearn.managers.SoundManager

class OptiLearnApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SoundManager.init(this)
    }
}

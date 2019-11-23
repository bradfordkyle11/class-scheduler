package com.kmbapps.classscheduler

import android.app.Application
import com.kmbapps.classscheduler.inject.AppComponent
import com.kmbapps.classscheduler.inject.DaggerAppComponent

class ClassSchedulerApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.create()
    }

}
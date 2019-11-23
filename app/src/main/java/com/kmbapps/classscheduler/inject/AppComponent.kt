package com.kmbapps.classscheduler.inject

import com.kmbapps.classscheduler.MainActivity
import dagger.Component

@Component(modules = [DataModule::class])
interface AppComponent {
    fun inject (activity: MainActivity)
}
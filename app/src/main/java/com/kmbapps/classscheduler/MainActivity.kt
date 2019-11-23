package com.kmbapps.classscheduler

import android.os.Bundle
import com.kmbapps.core.data.CourseRepository
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    internal lateinit var repo: CourseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun inject() {
        classSchedulerApplication.appComponent.inject(this)
    }
}

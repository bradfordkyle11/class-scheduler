package com.kmbapps.classscheduler.inject

import com.kmbapps.core.data.CourseDataSource
import com.kmbapps.core.data.SimpleCourseDataSource
import dagger.Binds
import dagger.Module

@Module
abstract class DataModule {

    @Binds
    abstract fun bindCourseDataSource(simpleCourseDataSource: SimpleCourseDataSource): CourseDataSource
}
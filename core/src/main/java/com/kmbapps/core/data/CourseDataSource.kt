package com.kmbapps.core.data

import com.kmbapps.core.domain.Course
import com.kmbapps.core.domain.CourseSection

interface CourseDataSource {

    suspend fun getAll(): List<Course>

    suspend fun addCourse(course: Course)

    suspend fun removeCourse(course: Course)

    suspend fun addCourseSection(section: CourseSection)

    suspend fun removeCourseSection(section: CourseSection)
}
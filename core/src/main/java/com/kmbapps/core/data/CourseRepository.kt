package com.kmbapps.core.data

import com.kmbapps.core.domain.Course
import com.kmbapps.core.domain.CourseSection
import javax.inject.Inject

class CourseRepository @Inject constructor(private val courseDataSource: CourseDataSource) {

    suspend fun getAll() = courseDataSource.getAll()

    suspend fun addCourse(course: Course) = courseDataSource.addCourse(course)

    suspend fun removeCourse(course: Course) = courseDataSource.removeCourse(course)

    suspend fun addCourseSection(section: CourseSection) = courseDataSource.addCourseSection(section)

    suspend fun removeCourseSection(section: CourseSection) = courseDataSource.removeCourseSection(section)
}
package com.kmbapps.core.data

import com.kmbapps.core.domain.Course
import com.kmbapps.core.domain.CourseSection
import javax.inject.Inject

class SimpleCourseDataSource @Inject constructor() : CourseDataSource {

    private val courses: MutableList<Course> = mutableListOf()
    private val sections: HashMap<Course, MutableList<CourseSection>> = hashMapOf()

    override suspend fun getAll(): List<Course> = courses

    override suspend fun addCourse(course: Course) {
        courses.add(course)
    }

    override suspend fun removeCourse(course: Course) {
        courses.remove(course)
        sections.remove(course)
    }

    override suspend fun addCourseSection(section: CourseSection) {
        val currSections = sections[section.course]

        if (currSections == null) {
            sections[section.course] = mutableListOf(section)
        } else {
            currSections.add(section)
        }
    }

    override suspend fun removeCourseSection(section: CourseSection) {
        sections[section.course]?.remove(section)
    }
}
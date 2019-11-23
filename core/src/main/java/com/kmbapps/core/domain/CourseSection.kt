package com.kmbapps.core.domain

data class CourseSection(
    val professor: String = "",
    val sectionNumber: String = "",
    val notes: String = "",
    val course: Course
)
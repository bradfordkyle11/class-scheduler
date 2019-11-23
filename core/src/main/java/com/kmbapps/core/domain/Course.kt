package com.kmbapps.core.domain

data class Course(
    val department: String = "",
    val number: String = "",
    val name: String = "",
    val creditHours: Int = 3,
    val sections: List<CourseSection> = emptyList()
)
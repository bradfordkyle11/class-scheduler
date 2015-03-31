package com.kmbapps.classscheduler;

import java.io.Serializable;

/**
 * Created by Kyle on 3/31/2015.
 */
public class Assignment implements Serializable {
    private static final long serialVersionUID = 1;

    private MyTime dueDate;
    private String type;
    private String name;
    private String details;
    private int grade;

    public Assignment(MyTime dueDate, String type, String name, String details, int grade){
        this.dueDate = dueDate;
        this.type = type;
        this.name = name;
        this.details = details;
        this.grade = grade;
    }

    public MyTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(MyTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}

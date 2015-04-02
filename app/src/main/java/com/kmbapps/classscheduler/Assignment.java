package com.kmbapps.classscheduler;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Kyle on 3/31/2015.
 */
public class Assignment implements Serializable, Comparable{
    private static final long serialVersionUID = 2;
    private static final int COMPARE_DUE_DATE = 0;
    private static final int COMPARE_TYPE = 1;

    private Calendar dueDate;
    private String type;
    private String name;
    private String details;
    private int grade;

    private int sortingMode = COMPARE_DUE_DATE;

    public Assignment(Calendar dueDate, String type, String name, String details, int grade){
        this.dueDate = dueDate;
        this.type = type;
        this.name = name;
        this.details = details;
        this.grade = grade;
    }

    public void setSortingMode(int sortingMode) {
        this.sortingMode = sortingMode;
    }

    @Override
    public boolean equals(Object object){
        if(!(object instanceof Assignment)){
            return false;
        }
        Assignment a = (Assignment) object;

        if(dueDate!=null) {
            return (type.equals(a.getType())) && (name.equals(a.getName())) && (details.equals(a.getDetails())) && (dueDate.equals(a.getDueDate()));
        }
        else{
            return (type.equals(a.getType())) && (name.equals(a.getName())) && (details.equals(a.getDetails())) && (dueDate==a.getDueDate());
        }
    }

    @Override
    public int compareTo(Object object){
        boolean yes = object instanceof Assignment;
        Assignment a = (Assignment) object;
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        switch(sortingMode){
            case COMPARE_DUE_DATE:
                if(this.equals(a)){
                    return EQUAL;
                }

                if(dueDate==null&&a.getDueDate()!=null){
                    return AFTER;
                }
                else if(dueDate!=null&&a.getDueDate()==null){
                    return BEFORE;
                }
                if(dueDate.compareTo(a.getDueDate())==0){
                    return (type + " " + name).compareTo(a.getType() + " " + a.getName());
                }
                else{
                    return dueDate.compareTo(a.getDueDate());
                }

            case COMPARE_TYPE:
                if(this.equals(a)){
                    return EQUAL;
                }

                if(type.equals(a.getType())){
                    return dueDate.compareTo(a.getDueDate());
                }
                else{
                    return type.compareTo(a.getType());
                }

            default:
                return 0;
        }


    }

    public Calendar getDueDate() {
        return dueDate;
    }

    public int getSortingMode() {
        return sortingMode;
    }

    public void setDueDate(Calendar dueDate) {
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

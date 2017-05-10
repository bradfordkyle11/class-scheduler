package com.kmbapps.classscheduler;

import android.content.Context;
import android.text.Spanned;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Created by Kyle on 9/23/2014.
 */

//TODO: implement parcelable for quicker passing between activities

public class Class implements Serializable {

    public static final int MAX = 0;
    public static final int HIGH = 1;
    public static final int MEDIUM = 2;
    public static final int LOW = 3;
    public static final int NUM_PRIORITIES = 4;
    public static final int DEFAULT_PRIORITY = MEDIUM;
    public static final int DEFAULT_CREDIT_HOURS = 3;
    private final UUID ID;
    private String department;
    private String number;
    private String name;
    private int creditHours;
    private int priority;
    private List<Section> sections;
    private int color;
    private boolean colorSet = false;
    private static final long serialVersionUID = 1000000;

    public UUID getId() {
        return ID;
    }

    private static ArrayList<Class> myClasses;

    static final Comparator<Class> PRIORITY = new Comparator<Class>(){
        public int compare(Class c1, Class c2){
            return Integer.compare(c1.getPriority(), c2.getPriority());
        }
    };

    @Override
    public boolean equals(Object object){
        if(object.getClass()!=com.kmbapps.classscheduler.Class.class){
            return false;
        }
        Class c = (Class) object;

        return (department.equals(c.getDepartment()))&&(number.equals(c.getNumber()))&&(name.equals(c.getName()))
                &&(creditHours==c.getCreditHours()&&priority==c.getPriority());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                append(department).
                append(number).
                append(name).
                append(creditHours).
                toHashCode();
    }

    Class() {
        ID = UUID.randomUUID();
        department = "";
        number = "";
        name = "";
        creditHours = 0;
        priority = 0;
        sections = new ArrayList<Section>();
    }

    Class(String department, String number, String name, int creditHours, int priority) {
        //create list of classes if it hasn't been already
        if (Class.myClasses == null) {
            Class.myClasses = new ArrayList<Class>();
        }

        //create new class and add it to the list of classes
        ID = UUID.randomUUID();
        this.name = name;
        this.department = department;
        this.number = number;
        this.creditHours = creditHours;
        this.priority = priority;
        Class.myClasses.add(this);

        sections = new ArrayList<Section>();
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
        for (Section s : sections){
            s.setContainingClass(this);
        }
    }

    @Override
    /**
     * returns string showing name, class number, and credit hours
     */
    public String toString() {
        return (department + " " + number + "\n" + name + " " + creditHours);
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIdentifierString(){
        return department + number + name;
    }

    public Spanned getHtmlScheduleOverview (MyTime time){
        return HtmlCompat.fromHtml("<b>" + MyTime.to12HourFormat(time.getStartHour(), time.getStartMinute()) + "</b><br>" +
                department + " " + number + " " + name);
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public static Class toClass(String classInfo) {
        StringTokenizer tokens = new StringTokenizer(classInfo, ",");
        String department = tokens.nextToken();
        String number = tokens.nextToken();
        String name = tokens.nextToken();
        int priority = Integer.parseInt(tokens.nextToken());
        int creditHours = Integer.parseInt(tokens.nextToken());
        Class myClass = new Class(name, department, number, creditHours, priority);
        return myClass;
    }

    public void saveClass(Context context) {
        ClassLoader.saveClass(context, this, ClassLoader.DESIRED_CLASSES);
    }



    public boolean addSection(Section s) {
        if(!sections.contains(s)){
            sections.add(s);
            return true;
        }
        return false;

    }

    public Section getSection(int position) {
        return sections.get(position);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        colorSet = true;
        this.color = color;
    }

    public boolean isColorSet(){
        return colorSet;
    }
}

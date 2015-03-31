package com.kmbapps.classscheduler;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Created by Kyle on 9/23/2014.
 */

//TODO: implement parcelable for quicker passing between activities

public class Class implements Serializable {
    private final UUID ID;
    private String department;
    private String number;
    private String name;
    private int creditHours;
    private List<Section> sections;
    private static final long serialVersionUID = 999999;

    public UUID getId() {
        return ID;
    }

    private static ArrayList<Class> myClasses;

    Class() {
        ID = UUID.randomUUID();
        department = "";
        number = "";
        name = "";
        creditHours = 0;
    }

    Class(String department, String number, String name, int creditHours) {
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
        Class.myClasses.add(this);

        sections = new ArrayList<Section>();
    }

    public List<Section> getSections() {
        return sections;
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
        int creditHours = Integer.parseInt(tokens.nextToken());
        Class myClass = new Class(name, department, number, creditHours);
        return myClass;
    }

    public void saveClass(Context context) {
        ClassLoader.saveClass(context, this);
    }

    @Override
    public boolean equals(Object object){
        if(object.getClass()!=com.kmbapps.classscheduler.Class.class){
            return false;
        }
        Class c = (Class) object;

        if(!c.getId().equals(ID)){
            return false;
        }
        return true;
    }

    public void addSection(Section s) {
        if(!sections.contains(s)){
            sections.add(s);
        }

    }

    public Section getSection(int position) {
        return sections.get(position);
    }


}

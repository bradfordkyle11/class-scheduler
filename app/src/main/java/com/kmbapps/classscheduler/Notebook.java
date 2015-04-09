package com.kmbapps.classscheduler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Kyle on 3/31/2015.
 */
public class Notebook implements Serializable {
    public static final int ASSIGNMENTS = 0;
    public static final int NOTES = 1;

    private static final long serialVersionUID = 4;


    private Hashtable<Section, List<Assignment>> assignments;
    private Hashtable<Section, List<Assignment>> grades;

    public Notebook(List<Section> sections){
        assignments = new Hashtable<Section, List<Assignment>>();
        grades = new Hashtable<Section, List<Assignment>>();
        for (Section section : sections){
            assignments.put(section, new ArrayList<Assignment>());
            grades.put(section, new ArrayList<Assignment>());
        }
    }

    public Hashtable<Section, List<Assignment>> getGrades() {
        return grades;
    }

    public List<Assignment> getAssignments(Section mClass){
        return assignments.get(mClass);
    }

    public List<Assignment> getGrades(Section mClass){ return grades.get(mClass);}

    public void addAssignment(Section mClass, Assignment assignment){
        ArrayList<Assignment> mClassAssignments = (ArrayList) getAssignments(mClass);
        mClassAssignments.add(assignment);

    }

    public Hashtable<Section, List<Assignment>> getAssignments() {
        return assignments;
    }

    public void updateAssignments(Section mClass, List<Assignment> assignments){
        this.assignments.put(mClass, assignments);
    }

    public void changeSortingMethod(Section section, int listType, int sortingKey){

        switch(listType){
            case ASSIGNMENTS:
                List<Assignment> assignmentsForSection = assignments.get(section);
                for(Assignment assignment : assignmentsForSection){
                    assignment.setSortingMode(sortingKey);
                }
                break;
            case NOTES:
                break;
        }
    }

    //erases all info related to the given section
    public void erase(Section section){
        assignments.remove(section);
        grades.remove(section);
    }

}

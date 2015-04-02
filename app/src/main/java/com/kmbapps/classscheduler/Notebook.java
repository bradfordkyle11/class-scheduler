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

    private static final long serialVersionUID = 1;


    private Hashtable<Section, List<Assignment>> assignments;

    public Notebook(List<Section> sections){
        assignments = new Hashtable<Section, List<Assignment>>();
        for (Section section : sections){
            assignments.put(section, new ArrayList<Assignment>());
        }
    }

    public List<Assignment> getAssignments(Section mClass){
        return assignments.get(mClass);
    }

    public void addAssignment(Section mClass, Assignment assignment){
        ArrayList<Assignment> mClassAssignments = (ArrayList) getAssignments(mClass);
        mClassAssignments.add(assignment);

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

}

package com.kmbapps.classscheduler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Kyle on 3/31/2015.
 */
public class Notebook implements Serializable {
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

    public void addAssignemnt(Section mClass, Assignment assignment){
        ArrayList<Assignment> mClassAssignments = (ArrayList) getAssignments(mClass);
        mClassAssignments.add(assignment);

    }

}

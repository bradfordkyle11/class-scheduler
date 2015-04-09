package com.kmbapps.classscheduler;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Kyle on 9/23/2014.
 */
public class ClassLoader {
    private static ArrayList<Class> myClasses;
    private static boolean classesLoaded = false;

    private static List<Schedule> schedules;
    private static boolean schedulesChanged = true;

    private static Schedule currentSchedule;
    private static boolean scheduleLoaded = false;

    private static String savedClassesFile = "savedclasses.txt";
    private static String currentScheduleFile = "CurrentSchedule.txt";
    private static String savedNotebooksFile = "Notebooks.txt";

    private static Hashtable<Schedule, Notebook> mNotebooks;
    private static boolean notebooksLoaded = false;



    public static ArrayList<Class> loadClasses(Context context) {
        if(!classesLoaded) {
            try {
                FileInputStream fis = context.openFileInput(savedClassesFile);
                ObjectInputStream is = new ObjectInputStream(fis);
                System.out.println(is.toString());

                myClasses = (ArrayList<Class>) is.readObject();
                classesLoaded = true;
                is.close();
            } catch (FileNotFoundException e) {
                System.out.println("FileNotFoundException: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.out.println("SystemNotFoundException: " + e.getMessage());
            }
        }


        return myClasses;
    }

    public static boolean saveClass(Context context, Class myClass) {
        if (myClasses == null) {
            myClasses = new ArrayList<Class>();
        }

        if(!myClasses.contains(myClass)){
            myClasses.add(myClass);
        }
        else{
            myClasses.set(myClasses.indexOf(myClass), myClass);
        }

        try {
            FileOutputStream fos = context.openFileOutput(savedClassesFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(myClasses);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            return false;
        }

        schedulesChanged = true;
        return true;
    }

    //replaces an existing class with an updated one
    public static boolean saveClass(Context context, Class updatedClass, Class originalClass) {
        if (myClasses == null) {
            myClasses = new ArrayList<Class>();
        }

        if(!myClasses.contains(updatedClass)){
            myClasses.set(myClasses.indexOf(originalClass), updatedClass);
        }
        else{
            return false;
        }

        try {
            FileOutputStream fos = context.openFileOutput(savedClassesFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(myClasses);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            return false;
        }

        schedulesChanged = true;
        return true;
    }

    public static void removeClass(Context context, Class removeThis) {
        myClasses.remove(removeThis);

        try {
            FileOutputStream fos = context.openFileOutput(savedClassesFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(myClasses);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }

        schedulesChanged = true;
    }

    public static void removeClasses(Context context, ArrayList<Class> classesToRemove){
        for(Class classToRemove : classesToRemove) {
            myClasses.remove(classToRemove);
        }

        try {
            FileOutputStream fos = context.openFileOutput(savedClassesFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(myClasses);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }

        schedulesChanged = true;
    }

    public static void removeSection(Context context, Section removeThis, Class containingClass){
        Class c = myClasses.get(myClasses.indexOf(removeThis.getContainingClass()));
        c.getSections().remove(removeThis);

        try {
            FileOutputStream fos = context.openFileOutput(savedClassesFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(myClasses);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }

        schedulesChanged = true;
    }

    public static void removeSections(Context context, ArrayList<Section> sectionsToRemove){

        for(Section sectionToRemove : sectionsToRemove) {
            Class c = myClasses.get(myClasses.indexOf(sectionToRemove.getContainingClass()));
            c.getSections().remove(sectionToRemove);
        }

        try {
            FileOutputStream fos = context.openFileOutput(savedClassesFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(myClasses);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }

        schedulesChanged = true;
    }

    public static void updateSchedules(){
        if(schedulesChanged){
            schedules = Schedule.createSchedules(myClasses);
            schedulesChanged = false;
        }
    }

    public static void setCurrentSchedule(Context context, Schedule schedule){
        currentSchedule = schedule;

        try {
            FileOutputStream fos = context.openFileOutput(currentScheduleFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(currentSchedule);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public static Schedule loadCurrentSchedule(Context context){
        if(!scheduleLoaded){
            try {
                FileInputStream fis = context.openFileInput(currentScheduleFile);
                ObjectInputStream is = new ObjectInputStream(fis);
                System.out.println(is.toString());

                currentSchedule = (Schedule) is.readObject();
                scheduleLoaded = true;
                is.close();
            } catch (FileNotFoundException e) {
                System.out.println("FileNotFoundException: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.out.println("SystemNotFoundException: " + e.getMessage());
            }
        }
        if (currentSchedule==null){
            return new Schedule();
        }
        return currentSchedule;
    }

    public static List<Schedule> loadSchedules(){
        return schedules;
    }

    public static Hashtable<Schedule, Notebook> loadNotebooks(Context context){
        if(!notebooksLoaded) {
            try {
                FileInputStream fis = context.openFileInput(savedNotebooksFile);
                ObjectInputStream is = new ObjectInputStream(fis);
                System.out.println(is.toString());

                mNotebooks = (Hashtable<Schedule, Notebook>) is.readObject();
                notebooksLoaded = true;
                is.close();
            } catch (FileNotFoundException e) {
                System.out.println("FileNotFoundException: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.out.println("SystemNotFoundException: " + e.getMessage());
            }
        }

        if(mNotebooks==null){
            return new Hashtable<Schedule, Notebook>();
        }

        return mNotebooks;
    }

    public static void saveNotebooks(Context context, Hashtable<Schedule, Notebook> notebooks){
        mNotebooks = notebooks;
        try {
            FileOutputStream fos = context.openFileOutput(savedNotebooksFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(notebooks);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public static void updateNotebooks(Context context){
        try {
            FileOutputStream fos = context.openFileOutput(savedNotebooksFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(mNotebooks);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public static void updateNotebooks(Context context, List<Assignment> assignments, Section section, Schedule schedule){
        Notebook notebook = mNotebooks.get(schedule);
        notebook.updateAssignments(section, assignments);
        mNotebooks.put(schedule, notebook);

        try {
            FileOutputStream fos = context.openFileOutput(savedNotebooksFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(mNotebooks);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public static void updateNotebook(Context context, Schedule schedule, Notebook notebook){
        mNotebooks.put(schedule, notebook);
        try {
            FileOutputStream fos = context.openFileOutput(savedNotebooksFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(mNotebooks);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public static void removeNotebook(Context context, Schedule schedule){
        mNotebooks.remove(schedule);
        try {
            FileOutputStream fos = context.openFileOutput(savedNotebooksFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(mNotebooks);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public static void saveAssignment(Context context, Assignment updatedAssignment, Assignment originalAssignment, Section containingSection){
        Notebook notebook = mNotebooks.get(currentSchedule);
        ArrayList<Assignment> assignments = (ArrayList) notebook.getAssignments(containingSection);
        assignments.set(assignments.indexOf(originalAssignment), updatedAssignment);
        try {
            FileOutputStream fos = context.openFileOutput(savedNotebooksFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(mNotebooks);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public static void saveGradedAssignment(Context context, Assignment updatedAssignment, Assignment originalAssignment, Section containingSection){
        Notebook notebook = mNotebooks.get(currentSchedule);
        ArrayList<Assignment> gradedAssignments = (ArrayList) notebook.getGrades(containingSection);
        gradedAssignments.set(gradedAssignments.indexOf(originalAssignment), updatedAssignment);
        try {
            FileOutputStream fos = context.openFileOutput(savedNotebooksFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(mNotebooks);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public static void deleteAssignment(Context context, Assignment assignmentToDelete, Section containingSection){
        Notebook notebook = mNotebooks.get(currentSchedule);
        ArrayList<Assignment> assignments = (ArrayList) notebook.getAssignments(containingSection);
        assignments.remove(assignmentToDelete);
        try {
            FileOutputStream fos = context.openFileOutput(savedNotebooksFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(mNotebooks);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }


}

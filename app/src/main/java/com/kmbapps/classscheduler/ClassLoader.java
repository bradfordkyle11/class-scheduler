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

    public static void saveClass(Context context, Class myClass) {
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
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }

        schedulesChanged = true;
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

    public static void removeSection(Context context, Section removeThis, Class containingClass){
        Class c = myClasses.get(myClasses.indexOf(containingClass));
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
}

package com.kmbapps.classscheduler;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by Kyle on 9/23/2014.
 */
public class ClassLoader {
    private static List<Class> myClasses = (List<Class>) Collections.synchronizedList(new ArrayList<Class>());
    private static boolean classesLoaded = false;
    private static boolean classesChanged = false;

    private static List<Schedule> schedules = (List<Schedule>) Collections.synchronizedList(new ArrayList<Schedule>());
    private static boolean schedulesChange = false;
    private static List<Schedule> selectSchedules = (List<Schedule>) Collections.synchronizedList(new ArrayList<Schedule>());
    private static boolean selectSchedulesChanged = false;
    private static boolean schedulesChanged = false;
    private static boolean schedulesLoaded = false;
    private static boolean schedulesOptionsChanged = false;
    public static final int ALL_SCHEDULES = 0;
    public static final int SELECT_SCHEDULES = 1;

    private static Schedule currentSchedule;
    private static boolean currentScheduleChanged = false;
    private static boolean scheduleLoaded = false;

    private static String savedClassesFile = "savedclasses.txt";
    private static String currentScheduleFile = "CurrentSchedule.txt";
    private static String savedNotebooksFile = "Notebooks.txt";
    private static String schedulesFile = "schedules.txt";
    private static String selectSchedulesFile = "SelectSchedules.txt";

    private static Hashtable<Schedule, Notebook> mNotebooks;
    private static boolean notebooksChanged = false;
    private static boolean notebooksLoaded = false;

    private static Deque<Integer> availableColors;
    private static Deque<Integer> currScheduleAvailableColors;

    private static int minCreditHours = Integer.MIN_VALUE;
    private static int maxCreditHours = Integer.MAX_VALUE;
    private static int minNumClasses = Integer.MIN_VALUE;
    private static int maxNumClasses = Integer.MAX_VALUE;
    private static Class newClass;
    private static Class oldClass;
    private static Section newSection;
    private static Section oldSection;


    public static final int CURR_SCHEDULE = 0;
    public static final int DESIRED_CLASSES = 1;


    public static void save(Context context){
        saveCurrentSchedule(context);
        saveClasses(context);
        //saveSchedules(context);
    }

    public static List<Class> loadClasses(Context context) {
        if(!classesLoaded) {
            try {
                FileInputStream fis = context.openFileInput(savedClassesFile);
                ObjectInputStream is = new ObjectInputStream(fis);
                System.out.println(is.toString());
                myClasses = (List<Class>) is.readObject();

                is.close();
                if (fis != null){
                    fis.close();
                }
            } catch (FileNotFoundException e) {
                System.out.println("FileNotFoundException: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.out.println("SystemNotFoundException: " + e.getMessage());
            }
        }
        if (myClasses == null){
            myClasses = new ArrayList<>();
            classesChanged = true;
        }
        classesLoaded = true;
        loadColors(context, DESIRED_CLASSES);
        return myClasses;
    }

    public static boolean saveClass(Context context, Class myClass, int where) {
        if (myClasses == null) {
            myClasses = new ArrayList<Class>();
        }

        for (Class c : myClasses){
            if (c.getIdentifierString().equals(myClass.getIdentifierString())){
                return false;
            }
        }
        myClass.setColor(getNextAvailableColor(context, DESIRED_CLASSES));
        myClasses.add(myClass);
        Collections.sort(myClasses, Class.PRIORITY);
        classesChanged = true;
//        if(!myClasses.contains(myClass)){
//
//        }
//        else{
//            return false;
//        }

        /*try {
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
        }*/

        return true;
    }

    //replaces an existing class with an updated one
    public static boolean saveClass(Context context, Class updatedClass, Class originalClass, int where) {
        if (myClasses == null) {
            myClasses = new ArrayList<Class>();
        }

        if(!myClasses.contains(updatedClass)){
            updatedClass.setColor(originalClass.getColor());
            myClasses.set(myClasses.indexOf(originalClass), updatedClass);
            Collections.sort(myClasses, Class.PRIORITY);
            classesChanged = true;
        }
        else{
            return false;
        }

        /*try {
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
        }*/


        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String minCreditHours = pref.getString("min_credit_hours",  context.getResources().getString(R.string.pref_min_credit_hours));
        String maxCreditHours = pref.getString("max_credit_hours",  context.getResources().getString(R.string.pref_max_credit_hours));
        String minNumClasses = pref.getString("min_num_classes",  context.getResources().getString(R.string.pref_min_num_classes));
        String maxNumClasses = pref.getString("max_num_classes",  context.getResources().getString(R.string.pref_max_num_classes));
        //update schedule values
        ClassLoader.minCreditHours = Integer.parseInt(minCreditHours);
        ClassLoader.maxCreditHours = Integer.parseInt(maxCreditHours);
        ClassLoader.minNumClasses = Integer.parseInt(minNumClasses);
        ClassLoader.maxNumClasses = Integer.parseInt(maxNumClasses);
        ClassLoader.newClass = updatedClass;
        ClassLoader.oldClass = originalClass;
        ClassLoader.newSection = null;
        ClassLoader.oldSection = null;

//        schedules = Schedule.updateSchedules(Integer.MIN_VALUE, Integer.MAX_VALUE,
//                Integer.MIN_VALUE, Integer.MAX_VALUE, updatedClass, originalClass, loadSchedules(context, ALL_SCHEDULES));
//        selectSchedules = Schedule.updateSchedules(Integer.parseInt(minCreditHours), Integer.parseInt(maxCreditHours),
//                Integer.parseInt(minNumClasses), Integer.parseInt((maxNumClasses)), updatedClass, originalClass, loadSchedules(context, ALL_SCHEDULES));
//        //saveSchedules(context);
        schedulesChange = true;
        selectSchedulesChanged = true;
        schedulesChanged = false;
        return true;
    }

    public static void saveClasses(Context context){
        if (classesChanged) {
            if (!classesLoaded) {
                loadClasses(context);
            }
            try {
                FileOutputStream fos = context.openFileOutput(savedClassesFile, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(myClasses);
                classesChanged = true;
                os.close();
            } catch (FileNotFoundException e) {
                System.out.println("FileNotFoundException: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }

    public static boolean saveSection(Context context, Section updatedSection, Section originalSection, Class containingClass, int where){
        int index;
        switch (where){
            case CURR_SCHEDULE:
                useColor(context, where,containingClass.getColor());
                if (currentSchedule == null){
                    currentSchedule = new Schedule();
                }
                index = currentSchedule.getSections().indexOf(originalSection);
                if (index < 0){
                    currentSchedule.getSections().add(updatedSection);
                }
                else {
                    currentSchedule.getSections().set(index, updatedSection);
                }
                currentScheduleChanged = true;
                setCurrentSchedule(context, currentSchedule);
                return true;
            case DESIRED_CLASSES:

                for (Class c : myClasses){
                    if (! c.equals(containingClass)){
                        for (Section s : c.getSections()) {
                            ArrayList<List<MyTime>> times = new ArrayList<>();
                            times.add(s.getTimes());
                            times.add(updatedSection.getTimes());
                            if (!MyTime.noConflicts(times)){
                                if (originalSection == null) {
                                    s.setNumConflicts(s.getNumConflicts() + 1);
                                }
                                updatedSection.setNumConflicts(updatedSection.getNumConflicts() + 1);
                            }
                        }

                    }
                }
                index = myClasses.indexOf(containingClass);
                if (originalSection == null) {
                    boolean ableToAdd = containingClass.addSection(updatedSection);
                    if (!ableToAdd){
                        return false;
                    }
                } else {
                    if (containingClass.getSections().contains(updatedSection)){
                        return false;
                    }
                    int replaceIndex = containingClass.getSections().indexOf(originalSection);
                    containingClass.getSections().set(replaceIndex, updatedSection);
                }
                myClasses.set(index, containingClass);
                classesChanged = true;
                /*try {
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
                }*/

                //update schedule
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String minCreditHours = pref.getString("min_credit_hours",  context.getResources().getString(R.string.pref_min_credit_hours));
                String maxCreditHours = pref.getString("max_credit_hours",  context.getResources().getString(R.string.pref_max_credit_hours));
                String minNumClasses = pref.getString("min_num_classes",  context.getResources().getString(R.string.pref_min_num_classes));
                String maxNumClasses = pref.getString("max_num_classes",  context.getResources().getString(R.string.pref_max_num_classes));
                //update schedule values
                ClassLoader.minCreditHours = Integer.parseInt(minCreditHours);
                ClassLoader.maxCreditHours = Integer.parseInt(maxCreditHours);
                ClassLoader.minNumClasses = Integer.parseInt(minNumClasses);
                ClassLoader.maxNumClasses = Integer.parseInt(maxNumClasses);
                ClassLoader.newClass = null;
                ClassLoader.oldClass = null;
                ClassLoader.newSection = updatedSection;
                ClassLoader.oldSection = originalSection;
//                schedules = Schedule.updateSchedules(Integer.MIN_VALUE, Integer.MAX_VALUE,
//                        Integer.MIN_VALUE, Integer.MAX_VALUE, updatedSection, originalSection, loadSchedules(context, ALL_SCHEDULES));
//                selectSchedules = Schedule.updateSchedules(Integer.parseInt(minCreditHours), Integer.parseInt(maxCreditHours),
//                        Integer.parseInt(minNumClasses), Integer.parseInt((maxNumClasses)), loadSchedules(context, ALL_SCHEDULES));
                schedulesChange = true;
                selectSchedulesChanged = true;
                schedulesChanged = false;
                //saveSchedules(context);
                return true;
            default:
                return false;
        }
    }

    public static void removeClass(Context context, Class removeThis) {
        releaseColor(removeThis.getColor(), DESIRED_CLASSES);
        for (Section section : removeThis.getSections()) {
            for (Class c : myClasses) {
                if (!c.equals(removeThis)) {
                    for (Section s : c.getSections()) {
                        ArrayList<List<MyTime>> times = new ArrayList<>();
                        times.add(s.getTimes());
                        times.add(section.getTimes());
                        if (!MyTime.noConflicts(times)) {
                            s.setNumConflicts(s.getNumConflicts() - 1); //decrease conflicts, since section is being removed
                        }
                    }

                }
            }
        }
        myClasses.remove(removeThis);
        Collections.sort(myClasses, Class.PRIORITY);
        classesChanged = true;

        /*try {
            FileOutputStream fos = context.openFileOutput(savedClassesFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(myClasses);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }*/

        //update schedule
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String minCreditHours = pref.getString("min_credit_hours",  context.getResources().getString(R.string.pref_min_credit_hours));
        String maxCreditHours = pref.getString("max_credit_hours",  context.getResources().getString(R.string.pref_max_credit_hours));
        String minNumClasses = pref.getString("min_num_classes",  context.getResources().getString(R.string.pref_min_num_classes));
        String maxNumClasses = pref.getString("max_num_classes",  context.getResources().getString(R.string.pref_max_num_classes));

        //update schedule values
        ClassLoader.minCreditHours = Integer.parseInt(minCreditHours);
        ClassLoader.maxCreditHours = Integer.parseInt(maxCreditHours);
        ClassLoader.minNumClasses = Integer.parseInt(minNumClasses);
        ClassLoader.maxNumClasses = Integer.parseInt(maxNumClasses);
        ClassLoader.newClass = null;
        ClassLoader.oldClass = removeThis;
        ClassLoader.newSection = null;
        ClassLoader.oldSection = null;
//        schedules = Schedule.updateSchedules(Integer.MIN_VALUE, Integer.MAX_VALUE,
//                Integer.MIN_VALUE, Integer.MAX_VALUE, null, removeThis, loadSchedules(context, ALL_SCHEDULES));
//        selectSchedules = Schedule.updateSchedules(Integer.parseInt(minCreditHours), Integer.parseInt(maxCreditHours),
//                Integer.parseInt(minNumClasses), Integer.parseInt((maxNumClasses)), loadSchedules(context, ALL_SCHEDULES));

        schedulesChange = true;
        selectSchedulesChanged = true;
        //saveSchedules(context);
        schedulesChanged = false;
    }

    public static void removeClasses(Context context, ArrayList<Class> classesToRemove){
        for (Class removeThis : classesToRemove) {
            for (Section section : removeThis.getSections()) {
                for (Class c : myClasses) {
                    if (!c.equals(removeThis)) {
                        for (Section s : c.getSections()) {
                            ArrayList<List<MyTime>> times = new ArrayList<>();
                            times.add(s.getTimes());
                            times.add(section.getTimes());
                            if (!MyTime.noConflicts(times)) {
                                s.setNumConflicts(s.getNumConflicts() - 1); //decrease conflicts, since section is being removed
                            }
                        }

                    }
                }
            }
        }
        for(Class classToRemove : classesToRemove) {
            releaseColor(classToRemove.getColor(), DESIRED_CLASSES);
            myClasses.remove(classToRemove);
        }
        Collections.sort(myClasses, Class.PRIORITY);
        classesChanged = true;

        /*try {
            FileOutputStream fos = context.openFileOutput(savedClassesFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(myClasses);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }*/

        //TODO: update schedules with the new method
        schedulesChanged = true;
    }

    public static void removeSection(Context context, Section removeThis, int where){
        switch (where){
            case CURR_SCHEDULE:
                currentSchedule.getSections().remove(removeThis);
                setCurrentSchedule(context, currentSchedule);
                break;
            case DESIRED_CLASSES:
                for (Class c : myClasses){
                    if (! c.equals(removeThis.getContainingClass())){
                        for (Section s : c.getSections()) {
                            ArrayList<List<MyTime>> times = new ArrayList<>();
                            times.add(s.getTimes());
                            times.add(removeThis.getTimes());
                            if (!MyTime.noConflicts(times)){
                                s.setNumConflicts(s.getNumConflicts() - 1); //decrease conflicts, since section is being removed
                            }
                        }

                    }
                }
                Class c = myClasses.get(myClasses.indexOf(removeThis.getContainingClass()));
                c.getSections().remove(removeThis);
                classesChanged = true;

                /*try {
                    FileOutputStream fos = context.openFileOutput(savedClassesFile, Context.MODE_PRIVATE);
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    os.writeObject(myClasses);
                    os.close();
                } catch (FileNotFoundException e) {
                    System.out.println("FileNotFoundException: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("IOException: " + e.getMessage());
                }*/
                //update schedule
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String minCreditHours = pref.getString("min_credit_hours",  context.getResources().getString(R.string.pref_min_credit_hours));
                String maxCreditHours = pref.getString("max_credit_hours",  context.getResources().getString(R.string.pref_max_credit_hours));
                String minNumClasses = pref.getString("min_num_classes",  context.getResources().getString(R.string.pref_min_num_classes));
                String maxNumClasses = pref.getString("max_num_classes",  context.getResources().getString(R.string.pref_max_num_classes));
                //update schedule values
                ClassLoader.minCreditHours = Integer.parseInt(minCreditHours);
                ClassLoader.maxCreditHours = Integer.parseInt(maxCreditHours);
                ClassLoader.minNumClasses = Integer.parseInt(minNumClasses);
                ClassLoader.maxNumClasses = Integer.parseInt(maxNumClasses);
                ClassLoader.newClass = null;
                ClassLoader.oldClass = null;
                ClassLoader.newSection = null;
                ClassLoader.oldSection = removeThis;

//                schedules = Schedule.updateSchedules(Integer.MIN_VALUE, Integer.MAX_VALUE,
//                        Integer.MIN_VALUE, Integer.MAX_VALUE, null, removeThis, loadSchedules(context, ALL_SCHEDULES));
//                selectSchedules = Schedule.updateSchedules(Integer.parseInt(minCreditHours), Integer.parseInt(maxCreditHours),
//                        Integer.parseInt(minNumClasses), Integer.parseInt((maxNumClasses)), loadSchedules(context, ALL_SCHEDULES));
                schedulesChange = true;
                selectSchedulesChanged = true;
                //saveSchedules(context);
                schedulesChanged = false;
                break;
        }

    }

    public static void removeSections(Context context, ArrayList<Section> sectionsToRemove, int where){

        for(Section sectionToRemove : sectionsToRemove) {
            Class c = myClasses.get(myClasses.indexOf(sectionToRemove.getContainingClass()));
            c.getSections().remove(sectionToRemove);
        }
        classesChanged = true;

        /*try {
            FileOutputStream fos = context.openFileOutput(savedClassesFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(myClasses);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }*/

        //TODO: update schedules with the new method

        schedulesChanged = true;
    }

    static void setSchedulesChanged(boolean schedulesChanged){
        ClassLoader.schedulesChanged = schedulesChanged;
    }

    static void setSchedulesOptionsChanged(boolean schedulesOptionsChanged){
        ClassLoader.schedulesOptionsChanged = schedulesOptionsChanged;
    }

    public static void setSchedules(List<Schedule> schedules){
        ClassLoader.schedules = schedules;
    }

    public static void setSelectSchedules(List<Schedule> selectSchedules){
        ClassLoader.selectSchedules = selectSchedules;
    }

    public static void updateSchedules(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String minCreditHours = pref.getString("min_credit_hours",  context.getResources().getString(R.string.pref_min_credit_hours));
        String maxCreditHours = pref.getString("max_credit_hours",  context.getResources().getString(R.string.pref_max_credit_hours));
        String minNumClasses = pref.getString("min_num_classes",  context.getResources().getString(R.string.pref_min_num_classes));
        String maxNumClasses = pref.getString("max_num_classes",  context.getResources().getString(R.string.pref_max_num_classes));
        if(schedulesChanged){
            schedules = Schedule.createSchedules(myClasses, Integer.parseInt(minCreditHours), Integer.parseInt(maxCreditHours),
                    Integer.parseInt(minNumClasses), Integer.parseInt((maxNumClasses)), 3);
            //saveSchedules(context);
            schedulesLoaded = true;
            schedulesChanged = false;
        }
        else if (schedulesOptionsChanged){
            selectSchedules = Schedule.updateSchedules(context, Integer.parseInt(minCreditHours), Integer.parseInt(maxCreditHours),
                    Integer.parseInt(minNumClasses), Integer.parseInt((maxNumClasses)), loadSchedules(context, ClassLoader.ALL_SCHEDULES));
            selectSchedulesChanged = true;
           // saveSchedules(context);
        }
    }

    public static void setCurrentSchedule(Context context, Schedule schedule){
        currentSchedule = schedule;
        loadColors(context, CURR_SCHEDULE);
        currentScheduleChanged = true;

        /*try {
            FileOutputStream fos = context.openFileOutput(currentScheduleFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(currentSchedule);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }*/
    }

    public static void saveCurrentSchedule(Context context){
        if (currentScheduleChanged) {
            if (!scheduleLoaded) {
                loadCurrentSchedule(context);
            }
            try {
                FileOutputStream fos = context.openFileOutput(currentScheduleFile, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(currentSchedule);
                currentScheduleChanged = false;
                os.close();
            } catch (FileNotFoundException e) {
                System.out.println("FileNotFoundException: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }

    public static Schedule loadCurrentSchedule(Context context){
        if(!scheduleLoaded){
            try {
                FileInputStream fis = context.openFileInput(currentScheduleFile);
                ObjectInputStream is = new ObjectInputStream(fis);
                System.out.println(is.toString());
                currentSchedule = (Schedule) is.readObject();
                is.close();
                if (fis != null){
                    fis.close();
                }
            } catch (FileNotFoundException e) {
                System.out.println("FileNotFoundException: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.out.println("SystemNotFoundException: " + e.getMessage());
            }
        }
        scheduleLoaded = true;
        if (currentSchedule==null){
            currentSchedule = new Schedule();
        }
        return currentSchedule;
    }

    public static void saveSchedules(Context context){
        if (!schedulesLoaded){
            loadSchedules(context, ALL_SCHEDULES);
        }

        try {
            FileOutputStream fos = context.openFileOutput(schedulesFile, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            if (schedulesChange) {
                os.writeObject(schedules);
            }
            os.close();
            if (fos != null){
                fos.close();
            }
            fos = context.openFileOutput(selectSchedulesFile, Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            if (selectSchedulesChanged) {
                os.writeObject(selectSchedules);
            }
            os.close();
            if (fos != null){
                fos.close();
            }
            schedulesChange = false;
            selectSchedulesChanged = false;
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public static List<Schedule> loadSchedules(Context context, int which){
//        if(!schedulesLoaded){
//            schedulesLoaded = true;
//            try {
//                FileInputStream fis = context.openFileInput(schedulesFile);
//                ObjectInputStream is = new ObjectInputStream(fis);
//                System.out.println(is.toString());
//
//                schedules = (List<Schedule>) is.readObject();
//                is.close();
//                if (fis!=null){
//                    fis.close();
//                }
//                fis = context.openFileInput(selectSchedulesFile);
//                is = new ObjectInputStream(fis);
//                System.out.println(is.toString());
//
//                selectSchedules = (List<Schedule>) is.readObject();
//                is.close();
//                if (fis!=null){
//                    fis.close();
//                }
//            } catch (FileNotFoundException e) {
//                System.out.println("FileNotFoundException: " + e.getMessage());
//            } catch (IOException e) {
//                System.out.println("IOException: " + e.getMessage());
//            } catch (ClassNotFoundException e) {
//                System.out.println("SystemNotFoundException: " + e.getMessage());
//            }
//        }

        switch (which){
            case ALL_SCHEDULES:
                if (schedules==null){
                    return new ArrayList<Schedule>();
                }
                return schedules;
            case SELECT_SCHEDULES:
                if (selectSchedules==null){
                    return new ArrayList<>();
                }
                return selectSchedules;
            default:
                return null;
        }

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
                if (fis != null){
                    fis.close();
                }
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
            notebooksChanged = false;
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

    /**
     * Load all the colors from resources, then use the current classes to determine which colors
     * are still available
     * @param context
     */
    private static void loadColors(Context context, int which){
        int[] colors = context.getResources().getIntArray(R.array.classCalendarColors);
        switch (which) {
            case DESIRED_CLASSES:
                ArrayList<Integer> usedColors;
                int offset;
                int multiplier = myClasses.size() / colors.length;
                offset = colors.length * multiplier;
                usedColors = new ArrayList<>(myClasses.size() - offset);
                for (int i = 0; i < myClasses.size() - offset; i++) {
                    usedColors.add(myClasses.get(i + offset).getColor());
                }
                availableColors = new ConcurrentLinkedDeque<>();
                for (int i = 0; i < colors.length; i++) {
                    if (!usedColors.contains(colors[i])) {
                        availableColors.add(colors[i]);
                    }
                }
                break;
            case CURR_SCHEDULE:
                if (!scheduleLoaded){
                    loadCurrentSchedule(context);
                }
                ArrayList<Integer> usedColorsCurrSchedule;
                multiplier = currentSchedule.getSections().size() / colors.length;
                offset = colors.length * multiplier;
                usedColorsCurrSchedule = new ArrayList<>(currentSchedule.getSections().size() - offset);
                for (int i = 0; i < currentSchedule.getSections().size() - offset; i++) {
                    usedColorsCurrSchedule.add(currentSchedule.getSections().get(i + offset).getContainingClass().getColor());
                }
                currScheduleAvailableColors = new ConcurrentLinkedDeque<>();
                for (int i = 0; i < colors.length; i++) {
                    if (!usedColorsCurrSchedule.contains(colors[i])) {
                        currScheduleAvailableColors.add(colors[i]);
                    }
                }
                break;
        }
    }

    /**
     * Retrieve the next color that is available
     * @param context
     * @return the next color that is not yet used
     */
    public static int getNextAvailableColor(Context context, int which){

        switch (which){
            case DESIRED_CLASSES:
                if (availableColors == null){
                    loadColors(context, which);
                }
                else if (availableColors.isEmpty()){
                    loadColors(context, which);
                }
                return availableColors.pop();
            case CURR_SCHEDULE:
                if (currScheduleAvailableColors == null){
                    loadColors(context, which);
                }
                else if (currScheduleAvailableColors.isEmpty()){
                    loadColors(context, which);
                }
                return currScheduleAvailableColors.pop();
        }
        return -1;
    }

    /**
     * Notify the color deque that this color is no longer in use
     * @param color the color that is no longer in use
     * @param which which color list to release from
     */

    public static void releaseColor(int color, int which){
        switch (which){
            case DESIRED_CLASSES:
                if (!availableColors.contains(color)){
                    availableColors.push(color);
                }
                break;
            case CURR_SCHEDULE:
                if (!currScheduleAvailableColors.contains(color)){
                    currScheduleAvailableColors.push(color);
                }
                break;
        }

    }

    private static void useColor(Context context, int which, int color){
        switch (which){
            case DESIRED_CLASSES:
                if (availableColors == null){
                    loadColors(context, which);
                }
                if (availableColors.contains(color)){
                    availableColors.remove(color);
                }
                break;
            case CURR_SCHEDULE:
                if (currScheduleAvailableColors == null){
                    loadColors(context, which);
                }
                if (currScheduleAvailableColors.contains(color)){
                    currScheduleAvailableColors.remove(color);
                }
                break;
        }
    }

    public static List<Class> getMyClasses() {
        return myClasses;
    }

    public static int getMinCreditHours() {
        return minCreditHours;
    }

    public static int getMaxCreditHours() {
        return maxCreditHours;
    }

    public static int getMinNumClasses() {
        return minNumClasses;
    }

    public static int getMaxNumClasses() {
        return maxNumClasses;
    }

    public static Class getNewClass() {
        Class result = newClass;
        newClass = null;
        return result;
    }

    public static Class getOldClass() {

        Class result = oldClass;
        oldClass = null;
        return result;
    }

    public static Section getNewSection() {
        Section result = newSection;
        newSection = null;
        return result;
    }

    public static Section getOldSection() {
        Section result = oldSection;
        oldSection = null;
        return result;
    }
}

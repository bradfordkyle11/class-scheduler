package com.kmbapps.classscheduler;

import android.content.Context;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kyle on 3/2/2015.
 */

//TODO: implement Parcelable for faster passing between activities
public class Schedule implements Serializable {

    public static final int NEW_SECTION = 0;
    public static final int PRIORITY_CHANGE = 1;

    private static final int MAX_NUM_SCHEDULES = 250;
    private static final int MAX_SECTIONS_TO_CONSIDER = 24;

    private List<Section> sections;
    private int priorityScore;
    private int deadTime;
    private static final long serialVersionUID = 1001;
    protected static List<List<Section>> staticSectionLists = Collections.synchronizedList( new ArrayList<List<Section>>());
    private final UUID ID;
    private static int minNumClasses;
    private static int maxNumClasses;
    private static int minCreditHours;
    private static int maxCreditHours;


    public Schedule() {
        this.sections = new ArrayList<Section>();
        priorityScore = 0;
        ID = UUID.randomUUID();
    }

    public Schedule(List<Section> sections) {
        this.sections = new ArrayList<Section>(sections);
        ArrayList<MyTime> times = new ArrayList<>();
        for (Section s : this.sections){
            times.addAll(s.getTimes());
            priorityScore += Class.NUM_PRIORITIES - 1 - s.getContainingClass().getPriority();
        }
        deadTime = MyTime.getTotalDeadTime(times);
        ID = UUID.randomUUID();
    }

    static final Comparator<Schedule> NUM_CLASSES = new Comparator<Schedule>(){
        public int compare(Schedule s1, Schedule s2){
            return Integer.compare(s2.getSections().size(), s1.getSections().size());
        }
    };

    static final Comparator<Schedule> NUM_AND_PRIORITY = new Comparator<Schedule>(){
        public int compare(Schedule s1, Schedule s2){
            int compareResult = Integer.compare(s2.getSections().size() + s2.getPriorityScore(), s1.getSections().size() + s1.getPriorityScore());
            if (compareResult == 0){
                compareResult = Integer.compare(s1.getDeadTime(), s2.getDeadTime());
            }
            return compareResult;
            //return Integer.compare(s2.getSections().size() + s2.getPriorityScore(), s1.getSections().size() + s1.getPriorityScore());
        }
    };



    public UUID getID() {
        return ID;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section section){sections.add(section); }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    //TODO: move schedule-creating algorithm to its own class
    //TODO: improve the algorithm. Allow customization (eg. max credit hours, preferred classes)

    /*
    *
    * */

    protected static void createSchedulesRecursive(Class thisClass, List<Class> otherClasses, List<List<Section>> sectionLists) {
        List<Class> newOtherClasses = new ArrayList<Class>();
        Class newThisClass = new Class();
        if(!otherClasses.isEmpty()) {
            newOtherClasses = new ArrayList<Class>();
            newOtherClasses.addAll(otherClasses);
            newThisClass = newOtherClasses.remove(0);
        }

        if (sectionLists.isEmpty()) {
            sectionLists = new ArrayList<List<Section>>();
            for (Section section : thisClass.getSections()) {
                ArrayList<Section> s = new ArrayList<Section>();
                s.add(section);
                sectionLists.add(s);
                for (List<Section> schedule : sectionLists){
                    if (meetsSpecifications(schedule)) {
                        staticSectionLists.add(schedule);
                    }
                }
            }
            if(sectionLists.isEmpty()){
                for (List<Section> schedule : sectionLists){
                    if (meetsSpecifications(schedule)) {
                        staticSectionLists.add(schedule);
                    }
                }
            }

            if(!otherClasses.isEmpty()) {
                createSchedulesRecursive(newThisClass, newOtherClasses, sectionLists);
            }

        } else {
            List<List<Section>> newSchedules = new ArrayList<List<Section>>();
            boolean classesCompatible = false;
            for (List<Section> schedule : sectionLists) {


                for (Section section : thisClass.getSections()) {
                    ArrayList<List<MyTime>> times = new ArrayList<List<MyTime>>();
                    List<Section> newSchedule = new ArrayList<Section>();
                    newSchedule.addAll(schedule);
                    //get times of the sections that are currently on the schedule
                    for (Section s: schedule) {
                        times.add(s.getTimes());
                    }

                    times.add(section.getTimes());
                    if (!MyTime.noConflicts(times)) {
                        times.remove(section.getTimes());
                    } else {
                        newSchedule.add(section);
                        newSchedules.add(newSchedule);
                        classesCompatible = true;
                    }

                }

            }

            if(classesCompatible){
                sectionLists.addAll(newSchedules);
                for (List<Section> schedule : sectionLists){
                    if (meetsSpecifications(schedule)) {
                        staticSectionLists.add(schedule);
                    }
                }

            }

            if(!otherClasses.isEmpty()) {
                createSchedulesRecursive(newThisClass, newOtherClasses, sectionLists);
            }
        }
    }



    public static List<Schedule> createSchedules(List<Class> classes, int minCreditHours, int maxCreditHours, int minNumClasses, int maxNumClasses, int changeType) {

        Schedule.minCreditHours = minCreditHours;
        Schedule.maxCreditHours = maxCreditHours;
        Schedule.minNumClasses = minNumClasses;
        Schedule.maxNumClasses = maxNumClasses;

        if (classes != null) {
            Collections.sort(classes, Class.PRIORITY);
        }
        staticSectionLists.clear();
        List<Schedule> schedules = new ArrayList<Schedule>();
        if (classes == null) {
            return schedules;
        }
        if (classes.isEmpty()){
            return schedules;
        }

        ExecutorService es = Executors.newCachedThreadPool();
        for (int i = 0; i < classes.size(); i++) {
            List<Class> otherClasses = new ArrayList<Class>();
            otherClasses.addAll(classes);
            Class thisClass = otherClasses.remove(i);
            es.execute(new createSchedulesThread(Integer.toString(i), thisClass, otherClasses, new ArrayList<List<Section>>()));

            // createSchedulesRecursive(thisClass, otherClasses, new ArrayList<List<Section>>());
        }
        es.shutdown();
        try {
            boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(List<Section> schedule : staticSectionLists){
            if(!schedules.contains(new Schedule(schedule))){
                schedules.add(new Schedule(schedule));
            }
        }


        Collections.sort(schedules, NUM_AND_PRIORITY);
        return schedules;
    }

    public static List<Schedule> updateSchedules(Context context, int minCreditHours, int maxCreditHours, int minNumClasses, int maxNumClasses, List<Schedule> currSchedule){
        Schedule.minCreditHours = minCreditHours;
        Schedule.maxCreditHours = maxCreditHours;
        Schedule.minNumClasses = minNumClasses;
        Schedule.maxNumClasses = maxNumClasses;
        List<Schedule> mCurrSchedule = new ArrayList<>();
//        if (currSchedule.size() < MAX_NUM_SCHEDULES){
//            mCurrSchedule.addAll(currSchedule);
//        }
//        else {
            mCurrSchedule = rebuildSchedules(context, ClassLoader.loadClasses(context), minCreditHours, maxCreditHours, minNumClasses, maxNumClasses);
//        }

        for (Iterator<Schedule> i = mCurrSchedule.iterator(); i.hasNext();){
            Schedule s = i.next();
            if(!meetsSpecifications(s.getSections())){
                i.remove();
            }
        }

        Set<Schedule> schedules = new HashSet<>();
        for (Schedule schedule : mCurrSchedule){
            schedules.add(new Schedule(schedule.getSections()));
        }

        ArrayList<Schedule> scheduleList = new ArrayList<>(schedules);
        Collections.sort(scheduleList, NUM_AND_PRIORITY);
        return scheduleList;

    }

    public static List<Schedule> updateSchedules(Context context, int minCreditHours, int maxCreditHours, int minNumClasses, int maxNumClasses,
                                                 Class newClass, Class oldClass, List<Schedule> currSchedules){
        Schedule.minCreditHours = minCreditHours;
        Schedule.maxCreditHours = maxCreditHours;
        Schedule.minNumClasses = minNumClasses;
        Schedule.maxNumClasses = maxNumClasses;
        updateSchedules(context, newClass, oldClass, currSchedules);
        // ArrayList<Schedule> schedules = new ArrayList<>();
        Set<Schedule> schedules = new HashSet<>();
        for (List<Section> schedule : staticSectionLists){
            schedules.add(new Schedule(schedule));
        }

        ArrayList<Schedule> scheduleList = new ArrayList<>(schedules);
        Collections.sort(scheduleList, NUM_AND_PRIORITY);
        return scheduleList;
    }

    public static List<Schedule> updateSchedules(Context context, int minCreditHours, int maxCreditHours, int minNumClasses, int maxNumClasses,
                                                 Section newSection, Section oldSection, List<Schedule> currSchedules, boolean ignoreMaxSchedules, boolean sort){
        Schedule.minCreditHours = minCreditHours;
        Schedule.maxCreditHours = maxCreditHours;
        Schedule.minNumClasses = minNumClasses;
        Schedule.maxNumClasses = maxNumClasses;
        List<Schedule> mCurrSchedules = new ArrayList<>();
//        if (currSchedules.size() <= MAX_NUM_SCHEDULES){
//            mCurrSchedules.addAll(currSchedules);
//        }
//        if (!ignoreMaxSchedules && currSchedules.size() == MAX_NUM_SCHEDULES){
        if (!ignoreMaxSchedules){
            mCurrSchedules = rebuildSchedules(context, ClassLoader.loadClasses(context), minCreditHours, maxCreditHours, minNumClasses, maxNumClasses);
        }
        else {
            mCurrSchedules.addAll(currSchedules);
        }
        updateSchedules(newSection, oldSection, mCurrSchedules);
       // ArrayList<Schedule> schedules = new ArrayList<>();
        Set<Schedule> schedules = new HashSet<>();
        for (List<Section> schedule : staticSectionLists){
            if (meetsSpecifications(schedule)) {
                schedules.add(new Schedule(schedule));
            }
        }

        ArrayList<Schedule> scheduleList = new ArrayList<>(schedules);
        if (sort) {
            Collections.sort(scheduleList, NUM_AND_PRIORITY);
        }
        if (scheduleList.size() > MAX_NUM_SCHEDULES){
            scheduleList.subList(MAX_NUM_SCHEDULES, scheduleList.size()).clear();
        }
        return scheduleList;
    }

    /*
    * This method checks if a newly created/edited section is compatible with this schedule
    * @param newSection - the newly created/edited section
    * @param originalSection - the pre-edit section or null if the section is new
    * @return true if the section schedule doesn't overlap, false otherwise*/
    public boolean isCompatible(Section newSection, Section originalSection){
        ArrayList<List<MyTime>> times = new ArrayList<List<MyTime>>();
        for (Section section : sections){
            if (originalSection != null){
                if (!section.equals(originalSection)){
                    times.add(section.getTimes());
                }
            }
            else {
                times.add(section.getTimes());
            }
        }
        times.add(newSection.getTimes());
        return MyTime.noConflicts(times);
    }

    protected static boolean noConflicts(Section section, List<Section> schedule){
        ArrayList<List<MyTime>> times = new ArrayList<List<MyTime>>();
        List<Section> newSchedule = new ArrayList<Section>();
        newSchedule.addAll(schedule);
        //get times of the sections that are currently on the schedule
        for (Section s: schedule) {
            if (s.getContainingClass().equals(section.getContainingClass())){
                return false;
            }
            times.add(s.getTimes());
        }
        times.add(section.getTimes());
        return MyTime.noConflicts(times);
    }


    @Override
    public boolean equals(Object o) {
        if (o.getClass() != com.kmbapps.classscheduler.Schedule.class) {
            return false;
        }

        Schedule s = (Schedule) o;

        if (sections.size() != s.getSections().size()) {
            return false;
        }

        for (Section section : sections) {
            if (!s.getSections().contains(section)) {
                return false;
            }
        }



        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                append(sections.toArray()).
                toHashCode();
    }

    public void dropClass(Section classToDrop){
        sections.remove(classToDrop);
    }

    public int getPriorityScore() {
        return priorityScore;
    }

    private static int getCreditHours(List<Section> schedule){
        int creditHours = 0;
        for (Section s : schedule){
            creditHours += s.getContainingClass().getCreditHours();
        }
        return creditHours;
    }

    private static boolean meetsSpecifications(List<Section> schedule){
        int creditHours = getCreditHours(schedule);
        return schedule.size() >= minNumClasses && schedule.size() <= maxNumClasses
                && creditHours >= minCreditHours && creditHours <= maxCreditHours;
    }

    private static void updateSchedules(Section newSection, Section oldSection, List<Schedule> currSchedules){
        staticSectionLists.clear();

        List<List<Section>> mCurrSchedules =  new ArrayList<>();
        for (Schedule schedule : currSchedules) {
            mCurrSchedules.add(schedule.getSections());
        }

        if (oldSection != null){
            for (Iterator<List<Section>> i = mCurrSchedules.iterator(); i.hasNext();){
                List<Section> schedule = i.next();
                schedule.remove(oldSection);
                if (schedule.isEmpty()){
                    i.remove();
                }
            }
        }

        List<List<Section>> newSchedules = new ArrayList<List<Section>>();
        for (List<Section> schedule : mCurrSchedules){
            ArrayList<Section> newSchedule = new ArrayList<Section>();
            newSchedule.addAll(schedule);
            newSchedules.add(newSchedule);
        }
        //newSchedules.addAll(staticSectionLists);

        if (newSection != null) {
            ExecutorService es = Executors.newCachedThreadPool();
            for (int i = 0; i < newSchedules.size(); i++) {
                es.execute(new createScheduleFromSection(Integer.toString(i), newSection, newSchedules.get(i)));
            }
            es.shutdown();
            try {
                boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (List<Section> schedule : mCurrSchedules){
            ArrayList<Section> newSchedule = new ArrayList<Section>();
            newSchedule.addAll(schedule);
            staticSectionLists.add(newSchedule);
        }



        if (currSchedules.size() <= MAX_NUM_SCHEDULES && newSection != null) {
            ArrayList<Section> solo = new ArrayList<>();
            solo.add(newSection);
            staticSectionLists.add(solo);
        }

    }

    private static void updateSchedules(Context context, Class newClass, Class oldClass, List<Schedule> currSchedules){

//        for (Schedule schedule : currSchedules){
//            staticSectionLists.add(schedule.getSections());
//        }

        List<Schedule> mSchedules = rebuildSchedules(context, ClassLoader.loadClasses(context), minCreditHours, maxCreditHours, minNumClasses, maxNumClasses);
        staticSectionLists.clear();
        for (Schedule schedule : mSchedules){
            staticSectionLists.add(schedule.getSections());
        }
//        if (oldClass != null){
//            for (Iterator<List<Section>> i = staticSectionLists.iterator(); i.hasNext();){
//                List<Section> schedule = i.next();
//                for (Iterator<Section> si = schedule.iterator(); si.hasNext();){
//                    Section s = si.next();
//                    if (s.getContainingClass().equals(oldClass)){
//                        if (newClass == null){
//                            si.remove();
//                        }
//                        else {
//                            s.setContainingClass(newClass);
//                        }
//                    }
//                }
//            }
//        }
    }

    private static List<Schedule> rebuildSchedules(Context context, List<Class> classes, int minCreditHours, int maxCreditHours, int minNumClasses, int maxNumClasses){
        List<Schedule> rebuiltSchedules = new ArrayList<Schedule>();
        int maxNumSections = 0;
        for (Class c : classes){
            if (c.getSections().size() > maxNumSections){
                maxNumSections = c.getSections().size();
            }
        }
        int i;
        int j = 0;
        for (i = 0; i < maxNumSections; i++){
//            if (rebuiltSchedules.size() >= MAX_NUM_SCHEDULES){
//                break;
//            }
            if (j >= MAX_SECTIONS_TO_CONSIDER){
                break;
            }
            for (Class c: classes){
                j++;
                if (i == 0){
                    Collections.sort(c.getSections(), Section.NUM_CONFLICTS);
                }
                if (i < c.getSections().size()) {
                    rebuiltSchedules = updateSchedules(context, Integer.MIN_VALUE, maxCreditHours, Integer.MIN_VALUE, maxNumClasses, c.getSection(i), null, rebuiltSchedules, true, true);
                }
                if (j >= MAX_SECTIONS_TO_CONSIDER){
                    break;
                }
            }
        }
        Schedule.minCreditHours = minCreditHours;
        Schedule.maxCreditHours = maxCreditHours;
        Schedule.minNumClasses = minNumClasses;
        Schedule.maxNumClasses = maxNumClasses;
        for (Iterator<Schedule> s = rebuiltSchedules.iterator(); s.hasNext();){
            Schedule schedule = s.next();
            if (!meetsSpecifications(schedule.getSections())){
                s.remove();
            }
        }
        return rebuiltSchedules;
    }

    public int getDeadTime() {
        return deadTime;
    }
}


class createSchedulesThread implements Runnable{
    Thread runner;
    Class thisClass;
    List<Class> otherClasses;
    List<List<Section>> sectionLists;

    public createSchedulesThread(String threadName, Class thisClass, List<Class> otherClasses, List<List<Section>> sectionLists){
        this.thisClass = thisClass;
        this.otherClasses = otherClasses;
        this.sectionLists = sectionLists;
        runner = new Thread(this, threadName);
    }

    public void run(){
        Schedule.createSchedulesRecursive(thisClass, otherClasses, sectionLists);
    }

    public void start(){
        runner.start();
    }

}

class createScheduleFromSection implements Runnable{
    Thread runner;
    Section newSection;
    List<Section> schedule;

    public createScheduleFromSection(String threadName, Section newSection, List<Section> schedule){
        this.newSection = newSection;
        this.schedule = schedule;
        runner = new Thread(this, threadName);
    }

    public void run() {
        if (Schedule.noConflicts(newSection, schedule)){
            schedule.add(newSection);
            Schedule.staticSectionLists.add(schedule);
        }
    }
    public void start() {
        runner.start();
    }
}



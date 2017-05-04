package com.kmbapps.classscheduler;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kyle on 3/2/2015.
 */

//TODO: implement Parcelable for faster passing between activities
public class Schedule implements Serializable {

    private List<Section> sections;
    private static final long serialVersionUID = 1000;
    private static List<List<Section>> staticSectionLists = new ArrayList<List<Section>>();
    private final UUID ID;


    public Schedule() {
        this.sections = new ArrayList<Section>();
        ID = UUID.randomUUID();
    }

    public Schedule(List<Section> sections) {
        this.sections = new ArrayList<Section>(sections);
        ID = UUID.randomUUID();
    }

    static final Comparator<Schedule> NUM_CLASSES = new Comparator<Schedule>(){
        public int compare(Schedule s1, Schedule s2){
            return Integer.compare(s2.getSections().size(), s1.getSections().size());
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

    public static void createSchedulesRecursive(Class thisClass, List<Class> otherClasses, List<List<Section>> sectionLists) {
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
                staticSectionLists = sectionLists;
            }
            if(sectionLists.isEmpty()){
                staticSectionLists = sectionLists;
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
                staticSectionLists = sectionLists;
            }

            if(!otherClasses.isEmpty()) {
                createSchedulesRecursive(newThisClass, newOtherClasses, sectionLists);
            }
        }
    }

    public static List<Schedule> createSchedules(List<Class> classes) {
        List<Schedule> schedules = new ArrayList<Schedule>();
        if (classes == null) {
            return schedules;
        }
        if (classes.isEmpty()){
            return schedules;
        }

        for (int i = 0; i < classes.size(); i++) {
            List<Class> otherClasses = new ArrayList<Class>();
            otherClasses.addAll(classes);
            Class thisClass = otherClasses.remove(i);
            createSchedulesRecursive(thisClass, otherClasses, new ArrayList<List<Section>>());
            for(List<Section> schedule : staticSectionLists){
                if(!schedules.contains(new Schedule(schedule))){
                    schedules.add(new Schedule(schedule));
                }
            }
        }


        Collections.sort(schedules, NUM_CLASSES);
        return schedules;
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

}

package com.kmbapps.classscheduler;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kyle on 9/23/2014.
 */

//TODO: implement parcelable for faster passing between activities
public class Section implements Serializable {

    private List<MyTime> times;
    private String professor;
    private String sectionNumber;
    private String notes;
    private Class containingClass;
    private int numConflicts;
    final UUID ID;
    private static final long serialVersionUID = 4444446;

    static final Comparator<Section> NUM_CONFLICTS= new Comparator<Section>(){
        public int compare(Section s1, Section s2){
            return Integer.compare(s1.getNumConflicts(), s2.getNumConflicts());
        }
    };

    @Override
    public boolean equals(Object object){
        if(object.getClass()!=com.kmbapps.classscheduler.Section.class){
            return false;
        }
        Section s = (Section) object;
        boolean timesEqual = times.equals(s.getTimes());
        boolean professorEqual = professor.equals(s.getProfessor());
        boolean sectionNumberEqual = sectionNumber.equals(s.getSectionNumber());
        boolean notesEqual = notes.equals(s.getNotes());
        boolean containingClassEqual = containingClass.equals(s.getContainingClass());

        return timesEqual && professorEqual && sectionNumberEqual && notesEqual && containingClassEqual;

//        return (times.equals(s.getTimes()))&&(professor.equals(s.getProfessor()))&&(sectionNumber.equals(s.getSectionNumber()))
//                &&(notes.equals(s.getNotes()))&&(containingClass.equals(s.getContainingClass()));
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                append(times).
                append(professor).
                append(sectionNumber).
                append(notes).
                append(containingClass).
                toHashCode();
    }

    public Class getContainingClass() {
        return containingClass;
    }

    public void setContainingClass(Class containingClass) {
        this.containingClass = containingClass;
    }

    public UUID getId() {
        return ID;
    }

    Section() {
        ID = UUID.randomUUID();
        times = new ArrayList<MyTime>();
        professor = "";
        sectionNumber = "";
        notes = "";
        containingClass = new Class();
    }

    Section(List<MyTime> times, String professor, String sectionNumber, String notes, Class containingClass) {
        ID = UUID.randomUUID();
        this.times = times;
        this.professor = professor;
        this.sectionNumber = sectionNumber;
        this.notes = notes;
        this.containingClass = containingClass;
    }



    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(String sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString(){
        String result = formatTime();
       //result = sectionNumber + " " + professor + " " + room;
        if (!professor.equals("")){

            result += "\n" + "Professor: " + professor;
        }
        return result;
    }

    public List<MyTime> getTimes() {
        return times;
    }

    public void setTimes(List<MyTime> times) {
        this.times = times;
    }

    public String formatTime(){

        String result = new String();
        for (int i = 0; i < times.size(); i++){

            //format the time
            result += times.get(i).getFormattedDays() + " from " + MyTime.to12HourFormat(times.get(i).getStartHour(),times.get(i).getStartMinute()) + " to " +
                    MyTime.to12HourFormat(times.get(i).getEndHour(), times.get(i).getEndMinute());

            //format the room number
            if(!times.get(i).getRoomNumber().equals("")) {
                result += " in " + times.get(i).getRoomNumber();
            }

            //add new line unless this is the last line
            if (i < times.size()-1){
                result += "\n";
            }

        }

        return result;
//        String currentDays;
//        int currentStartHour;
//        int currentStartMin;
//        int currentEndHour;
//        int currentEndMin;
//        ArrayList<String> days = new ArrayList<String>();
//        ArrayList<Integer> daysChecked = new ArrayList<Integer>();
//
//        for(int i = 0; i < times.size(); i++){
//            currentDays = "";
//            currentStartHour = -1;
//            currentStartMin = -1;
//            currentEndHour = -1;
//            currentEndMin = -1;
//            for(int j = i; j < times.size(); j++) {
//                if (!daysChecked.contains(times.get(j).getWeekday())) {
//                    if (currentDays.equals("")) {
//                        daysChecked.add(times.get(j).getWeekday());
//                        currentStartHour = times.get(j).getHour();
//                        currentStartMin = times.get(j).getMinute();
//                        currentEndHour = endTimes.get(j).getHour();
//                        currentEndMin = endTimes.get(j).getMinute();
//                        currentDays += intToWeekDay(times.get(j).getWeekday());
//
//                    } else if (times.get(j).getHour() == currentStartHour && times.get(j).getMinute() == currentStartMin
//                            && endTimes.get(j).getHour() == currentEndHour && endTimes.get(j).getMinute() == currentEndMin) {
//                        daysChecked.add(times.get(j).getWeekday());
//                        currentDays += intToWeekDay(times.get(j).getWeekday());
//                    }
//                }
//            }
//            if(!currentDays.equals("")) {
//                days.add(currentDays + " from " + MyTime.to12HourFormat(currentStartHour, currentStartMin) + " to " + MyTime.to12HourFormat(currentEndHour, currentEndMin));
//            }
//
//        }
//        String result = new String();
//        for (int i = 0; i < days.size(); i++){
//            result += days.get(i);
//
//            //newline after every line except the last one
//            if (i<(days.size()-1)){
//                result += "\n";
//            }
//        }
//        return result;
    }

    private String intToWeekDay(int weekday){
        switch(weekday){
            case 0:
                return "S";
            case 1:
                return "M";
            case 2:
                return "T";
            case 3:
                return "W";
            case 4:
                return "Th";
            case 5:
                return "F";
            case 6:
                return "S";
            default:
                return "";
        }
    }

    public int getNumConflicts() {
        return numConflicts;
    }

    public void setNumConflicts(int numConflicts) {
        this.numConflicts = numConflicts;
    }
}

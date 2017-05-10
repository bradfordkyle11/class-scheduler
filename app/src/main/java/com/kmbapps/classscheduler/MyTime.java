package com.kmbapps.classscheduler;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Kyle on 2/26/2015.
 */

//TODO: implement Parcelable for faster passing between activities
public class MyTime implements Serializable {

    private String days;
    private int weekday;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private String roomNumber;

    private boolean startTimeSet = true;
    private boolean endTimeSet = true;
    private static final long serialVersionUID = 55556;
    private static String[] dayAbbrevs = new String[7];

    static final Comparator<MyTime> START_TIME = new Comparator<MyTime>(){
        public int compare(MyTime t1, MyTime t2){
            return Integer.compare(toMinutes(t1.getStartHour(), t1.getStartMinute()), toMinutes(t2.getStartHour(), t2.getStartMinute()));
        }
    };


    MyTime(){
    }

    @Override
    public boolean equals(Object object){
        if(object.getClass()!=com.kmbapps.classscheduler.MyTime.class){
            return false;
        }

        MyTime myTime = (MyTime) object;
        return (days.equals(myTime.getDays()))&&(startHour==myTime.getStartHour())&&(startMinute==myTime.getStartMinute())
                &&(endHour==myTime.getEndHour())&&(endMinute==myTime.getEndMinute())&&(roomNumber.equals(myTime.getRoomNumber()));
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                append(days).
                append(startHour).
                append(startMinute).
                append(endHour).
                append(endMinute).
                append(roomNumber).
                toHashCode();
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        if(startHour<0||startHour>23){
            startTimeSet = false;
        }
        this.startHour = startHour;
    }


    public int getStartMinute(){
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        if(startMinute<0||startMinute>59){
            startTimeSet = false;
        }
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        if(endHour<0||endHour>23){
            endTimeSet = false;
        }
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        if(endMinute<0||endMinute>59){
            endTimeSet = false;
        }
        this.endMinute = endMinute;
    }

    public boolean startTimeSet(){
        return startTimeSet;
    }

    public boolean endTimeSet(){
        return endTimeSet;
    }




    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public String getDays() {
        return days;
    }

    public String getFormattedDays(){
        String result = days;
        return result;
    }

    public void setDays(List<Integer> days) {
        this.days = "";
        for (int i = 0; i < days.size(); i++){

            if (days.get(i) < dayAbbrevs.length) {
                this.days += dayAbbrevs[days.get(i)];
            }
        }
    }

    public static StringBuilder to12HourFormat(int hour, int minute) {
        StringBuilder time = new StringBuilder();
        boolean pm = false;
        if (hour > 11) {
            hour = hour - 12;
            pm = true;
        }
        if (hour == 0) {
            hour = hour + 12;
        }
        time.append(String.valueOf(hour)).append(":");
        if (minute < 10) {
            time.append("0");
        }
        time.append(String.valueOf(minute));

        if (pm) {
            time.append(MyApp.getContext().getString(R.string.abbreviation_pm));
        } else {
            time.append(MyApp.getContext().getString(R.string.abbreviation_am));
        }

        return time;
    }




    //returns true if the MyTimes in the arraylist don't overlap
    public static boolean noConflicts(ArrayList<MyTime> schedules){
        boolean noConflicts;
        for (int i = 0; i < schedules.size(); i++){
            MyTime schedule1 = schedules.get(i);
            for(int j = i + 1; j < schedules.size(); j++){
                MyTime schedule2 = schedules.get(j);
                for (String day : dayAbbrevs){
                    if (schedule1.getDays().contains(day) && schedule2.getDays().contains(day)){
                        noConflicts = !timeOverlap(schedule1, schedule2);
                        if(!noConflicts){return false;}//return false if there are conflicts
                    }
                }
//                if(schedule1.getDays().contains("Su") && schedule2.getDays().contains("Su")){
//                    noConflicts = !timeOverlap(schedule1, schedule2);
//                    if(!noConflicts){return false;}//return false if there are conflicts
//                }
//
//                if(schedule1.getDays().contains("M") && schedule2.getDays().contains("M")){
//                    noConflicts = !timeOverlap(schedule1, schedule2);
//                    if(!noConflicts){return false;}//return false if there are conflicts
//                }
//
//                if(schedule1.getDays().contains("Tu") && schedule2.getDays().contains("Tu")){
//                    noConflicts = !timeOverlap(schedule1, schedule2);
//                    if(!noConflicts){return false;}//return false if there are conflicts
//                }
//
//                if(schedule1.getDays().contains("W") && schedule2.getDays().contains("W")){
//                    noConflicts = !timeOverlap(schedule1, schedule2);
//                    if(!noConflicts){return false;}//return false if there are conflicts
//                }
//
//                if(schedule1.getDays().contains("Th") && schedule2.getDays().contains("Th")){
//                    noConflicts = !timeOverlap(schedule1, schedule2);
//                    if(!noConflicts){return false;}//return false if there are conflicts
//                }
//
//                if(schedule1.getDays().contains("F") && schedule2.getDays().contains("F")){
//                    noConflicts = !timeOverlap(schedule1, schedule2);
//                    if(!noConflicts){return false;}//return false if there are conflicts
//                }
//
//                if(schedule1.getDays().contains("Sa") && schedule2.getDays().contains("Sa")){
//                    noConflicts = !timeOverlap(schedule1, schedule2);
//                    if(!noConflicts){return false;}//return false if there are conflicts
//                }
            }
        }
        return true;
    }

    //test for conflicts between schedules
    public static boolean noConflicts(List<List<MyTime>> schedules){
        for (int i = 0; i < schedules.size(); i++){
            List<MyTime> schedule1 = schedules.get(i);
            for(int j = i + 1; j < schedules.size(); j++){
                List<MyTime> schedule2 = schedules.get(j);

                //don't check if they are the same
//                if(schedule1.equals(schedule2)){
//                    break;
//                }

                ArrayList<MyTime> combinedSchedule = new ArrayList<MyTime>();
                combinedSchedule.addAll(schedule1);
                combinedSchedule.addAll(schedule2);
                if(!noConflicts(combinedSchedule)){
                    return false;
                }
            }
        }
        return true;
    }

    //returns true if the time of day overlaps. doesn't consider the day of the week
    public static boolean timeOverlap(MyTime time1, MyTime time2){
        int time1Start = toMinutes(time1.getStartHour(), time1.getStartMinute());
        int time1End = toMinutes(time1.getEndHour(), time1.getEndMinute());
        int time2Start = toMinutes(time2.getStartHour(), time2.getStartMinute());
        int time2End = toMinutes(time2.getEndHour(), time2.getEndMinute());


        if(time1Start<time2Start && time2Start<time1End){
            return true;
        }
        if(time1Start>time2Start && time1Start<time2End) {
            return true;
        }

        return time1Start == time2Start;

    }

    public static int timeBetween (MyTime time1, MyTime time2){
        if (timeOverlap(time1, time2)){
            return 0;
        }
        else {
            int startTime;
            int endTime;
            if (toMinutes(time1.getStartHour(), time1.getStartMinute()) < toMinutes(time2.getStartHour(), time2.getStartMinute())){
                startTime = toMinutes(time1.getEndHour(), time1.getEndMinute());
                endTime = toMinutes(time2.getStartHour(), time2.getStartMinute());
            }
            else {
                startTime = toMinutes(time2.getEndHour(), time2.getEndMinute());
                endTime = toMinutes(time1.getStartHour(), time1.getStartMinute());
            }

            return endTime - startTime;
        }
    }

    public static int getTotalDeadTime (List<MyTime> times){
        int totalDeadTime = 0;
        Collections.sort(times, START_TIME);
        String[] days = {"Su", "M", "Tu", "W", "Th", "F", "Sa"};
        for (String day : days){
            for (int i = 0; i < times.size() - 1; i++){
                if (times.get(i).getDays().contains(day)) {
                    for (int j = i + 1; j < times.size(); j++) {
                        if (times.get(j).getDays().contains(day)) {
                            totalDeadTime += timeBetween(times.get(i), times.get(j));
                            break;
                        }
                    }
                }
            }
        }
        return totalDeadTime;

    }

    public static int toMinutes(int hour, int minute){
        return hour*60 + minute;
    }

    public static double toHours(int hour, int minute){
        double minutes = toMinutes(hour, minute);
        return minutes / 60.d;
    }

    private String intToWeekDay(int weekday){
        switch(weekday){
            case 0:
                return "Su";
            case 1:
                return "M";
            case 2:
                return "Tu";
            case 3:
                return "W";
            case 4:
                return "Th";
            case 5:
                return "F";
            case 6:
                return "Sa";
            default:
                return "";
        }
    }

    private static int sameDays(MyTime t1, MyTime t2){
        int result = 0;
        if (t1.getDays().contains("Su") && t2.getDays().contains("Su")){
            result += 1;
        }
        if (t1.getDays().contains("M") && t2.getDays().contains("M")){
            result += 1;
        }
        if (t1.getDays().contains("Tu") && t2.getDays().contains("Tu")){
            result += 1;
        }
        if (t1.getDays().contains("W") && t2.getDays().contains("W")){
            result += 1;
        }
        if (t1.getDays().contains("Th") && t2.getDays().contains("Th")){
            result += 1;
        }
        if (t1.getDays().contains("F") && t2.getDays().contains("F")){
            result += 1;
        }
        if (t1.getDays().contains("Sa") && t2.getDays().contains("Sa")){
            result += 1;
        }
        return result;
    }

    public static void setDayAbbrevs(String[] dayAbbrevs) {
        MyTime.dayAbbrevs = dayAbbrevs;
    }
}

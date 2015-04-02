package com.kmbapps.classscheduler;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
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
    private static final long serialVersionUID = 55556;


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
        this.startHour = startHour;
    }


    public int getStartMinute(){
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
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
        result = result.replace("u", "");
        //result = result.replace("u", "");
        result = result.replace("a", "");
        return result;
    }

    public void setDays(List<Integer> days) {
        this.days = "";
        for (int i = 0; i < days.size(); i++){

            this.days +=  intToWeekDay(days.get(i));
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
            time.append("p");
        } else {
            time.append("a");
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
                if(schedule1.getDays().contains("Su") && schedule2.getDays().contains("Su")){
                    noConflicts = !timeOverlap(schedule1, schedule2);
                    if(!noConflicts){return false;}//return false if there are conflicts
                }

                if(schedule1.getDays().contains("M") && schedule2.getDays().contains("M")){
                    noConflicts = !timeOverlap(schedule1, schedule2);
                    if(!noConflicts){return false;}//return false if there are conflicts
                }

                if(schedule1.getDays().contains("Tu") && schedule2.getDays().contains("Tu")){
                    noConflicts = !timeOverlap(schedule1, schedule2);
                    if(!noConflicts){return false;}//return false if there are conflicts
                }

                if(schedule1.getDays().contains("W") && schedule2.getDays().contains("W")){
                    noConflicts = !timeOverlap(schedule1, schedule2);
                    if(!noConflicts){return false;}//return false if there are conflicts
                }

                if(schedule1.getDays().contains("Th") && schedule2.getDays().contains("Th")){
                    noConflicts = !timeOverlap(schedule1, schedule2);
                    if(!noConflicts){return false;}//return false if there are conflicts
                }

                if(schedule1.getDays().contains("F") && schedule2.getDays().contains("F")){
                    noConflicts = !timeOverlap(schedule1, schedule2);
                    if(!noConflicts){return false;}//return false if there are conflicts
                }

                if(schedule1.getDays().contains("Sa") && schedule2.getDays().contains("Sa")){
                    noConflicts = !timeOverlap(schedule1, schedule2);
                    if(!noConflicts){return false;}//return false if there are conflicts
                }
            }
        }
        return true;
    }

    //test for conflicts between schedules
    public static boolean noConflicts(List<List<MyTime>> schedules){
        boolean noConflicts;
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

    public static int toMinutes(int hour, int minute){
        return hour*60 + minute;
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




}

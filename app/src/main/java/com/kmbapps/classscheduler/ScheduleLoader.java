package com.kmbapps.classscheduler;


import android.content.SharedPreferences;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Kyle on 5/6/2017.
 */

public class ScheduleLoader extends AsyncTaskLoader<List<Schedule>> {
    public static final int ALL_SCHEDULES_LOADER = 0;
    public static final int SELECT_SCHEDULES_LOADER = 1;
    private Context context;
    private int minCreditHours;
    private int maxCreditHours;
    private int minNumClasses;
    private int maxNumClasses;
    private List<Schedule> currSchedules;
    private Class newClass;
    private Class oldClass;
    private Section newSection;
    private Section oldSection;
    private boolean scheduleChanged = true;


    public ScheduleLoader(Context context) {
        super(context);
        scheduleChanged = true;
    }

//    public ScheduleLoader(Context context, int minCreditHours, int maxCreditHours, int minNumClasses, int maxNumClasses,
//                               Class newClass, Class oldClass, List<Schedule> currSchedules) {
//        super(context);
//        this.minCreditHours = minCreditHours;
//        this.maxCreditHours = maxCreditHours;
//        this.minNumClasses = minNumClasses;
//        this.maxNumClasses = maxNumClasses;
//        this.newClass = newClass;
//        this.oldClass = oldClass;
//        this.currSchedules = currSchedules;
//    }
//
//    public ScheduleLoader(Context context, int minCreditHours, int maxCreditHours, int minNumClasses, int maxNumClasses,
//                          Section newSection, Section oldSection, List<Schedule> currSchedules) {
//        super(context);
//        this.minCreditHours = minCreditHours;
//        this.maxCreditHours = maxCreditHours;
//        this.minNumClasses = minNumClasses;
//        this.maxNumClasses = maxNumClasses;
//        this.newSection = newSection;
//        this.oldSection = oldSection;
//        this.currSchedules = currSchedules;
//    }
//
//    public ScheduleLoader(Context context, int minCreditHours, int maxCreditHours, int minNumClasses, int maxNumClasses,
//                          Class newClass, Class oldClass, Section newSection, Section oldSection, List<Schedule> currSchedules) {
//        super(context);
//        this.minCreditHours = minCreditHours;
//        this.maxCreditHours = maxCreditHours;
//        this.minNumClasses = minNumClasses;
//        this.maxNumClasses = maxNumClasses;
//        this.oldClass = oldClass;
//        this.newClass = newClass;
//        this.newSection = newSection;
//        this.oldSection = oldSection;
//        this.currSchedules = currSchedules;
//    }

    @Override public List<Schedule> loadInBackground(){
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND + Process.THREAD_PRIORITY_MORE_FAVORABLE);
        //just options updated
        //if (currSchedules == null){
            currSchedules = ClassLoader.loadSchedules(getContext(), ClassLoader.ALL_SCHEDULES);
        //}
        switch(getId()){
            case ALL_SCHEDULES_LOADER:
                update(Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE,
                        ClassLoader.getNewClass(), ClassLoader.getOldClass(), ClassLoader.getNewSection(), ClassLoader.getOldSection());
                break;
            case SELECT_SCHEDULES_LOADER:
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
                String minCreditHours = pref.getString("min_credit_hours",  getContext().getResources().getString(R.string.pref_min_credit_hours));
                String maxCreditHours = pref.getString("max_credit_hours",  getContext().getResources().getString(R.string.pref_max_credit_hours));
                String minNumClasses = pref.getString("min_num_classes",  getContext().getResources().getString(R.string.pref_min_num_classes));
                String maxNumClasses = pref.getString("max_num_classes",  getContext().getResources().getString(R.string.pref_max_num_classes));
                update(Integer.parseInt(minCreditHours), Integer.parseInt(maxCreditHours), Integer.parseInt(minNumClasses), Integer.parseInt((maxNumClasses)),
                        ClassLoader.getNewClass(), ClassLoader.getOldClass(), ClassLoader.getNewSection(), ClassLoader.getOldSection());
                break;
        }
        if (scheduleChanged) {
            if (newClass == null && oldClass == null && newSection == null && oldSection == null && getId() == SELECT_SCHEDULES_LOADER) {
                currSchedules = Schedule.updateSchedules(getContext(), minCreditHours, maxCreditHours, minNumClasses, maxNumClasses, currSchedules);
            } else if ((newClass == null && oldClass == null) && (newSection != null || oldSection != null)) {
                currSchedules = Schedule.updateSchedules(getContext(), minCreditHours, maxCreditHours, minNumClasses, maxNumClasses, newSection, oldSection, currSchedules, false, true);
            } else if ((newSection == null && oldSection == null) && (newClass != null || oldClass != null)){
                currSchedules = Schedule.updateSchedules(getContext(), minCreditHours, maxCreditHours, minNumClasses, maxNumClasses, newClass, oldClass, currSchedules);
            }
        }
        scheduleChanged = false;
        switch(getId()){
            case ALL_SCHEDULES_LOADER:
                ClassLoader.setSchedules(currSchedules);
                break;
            case SELECT_SCHEDULES_LOADER:
                ClassLoader.setSelectSchedules(currSchedules);
                ClassLoader.save(getContext()); //TODO: save in in background thread?
        }
        return currSchedules;
    }

    public void update(int minCreditHours, int maxCreditHours, int minNumClasses, int maxNumClasses, Section newSection, Section oldSection){
        setMinCreditHours(minCreditHours);
        setMaxCreditHours(maxCreditHours);
        setMinNumClasses(minNumClasses);
        setMaxNumClasses(maxNumClasses);
        setNewSection(newSection);
        setOldSection(oldSection);

        //not updating a class, so don't want class to trigger scheduleChanged
        newClass = null;
        oldClass = null;

    }

    public void update(int minCreditHours, int maxCreditHours, int minNumClasses, int maxNumClasses, Class newClass, Class oldClass){
        setMinCreditHours(minCreditHours);
        setMaxCreditHours(maxCreditHours);
        setMinNumClasses(minNumClasses);
        setMaxNumClasses(maxNumClasses);
        setNewClass(newClass);
        setOldClass(oldClass);

        //not updating a section, so don't want section to trigger scheduleChanged
        newSection = null;
        oldSection = null;
    }

    public void update(int minCreditHours, int maxCreditHours, int minNumClasses, int maxNumClasses, Class newClass, Class oldClass,
                       Section newSection, Section oldSection){
        setMinCreditHours(minCreditHours);
        setMaxCreditHours(maxCreditHours);
        setMinNumClasses(minNumClasses);
        setMaxNumClasses(maxNumClasses);

        if (newClass != null || oldClass != null) {
            setNewClass(newClass);
            setOldClass(oldClass);

            //not updating a section, so don't want section to trigger scheduleChanged
            this.newSection = null;
            this.oldSection = null;
        }
        else if (newSection != null || oldSection != null){
            setNewSection(newSection);
            setOldSection(oldSection);

            //not updating a class, so don't want class to trigger scheduleChanged
            this.newClass = null;
            this.oldClass = null;
        }
        else {
            this.newClass = newClass;
            this.oldClass = oldClass;
            this.newSection = newSection;
            this.oldSection = oldSection;
        }
    }

    public int getMinCreditHours() {
        return minCreditHours;
    }

    public void setMinCreditHours(int minCreditHours) {
        if (minCreditHours != this.minCreditHours){
            scheduleChanged = true;
        }
        this.minCreditHours = minCreditHours;
    }

    public int getMaxCreditHours() {
        return maxCreditHours;
    }

    public void setMaxCreditHours(int maxCreditHours) {
        if (maxCreditHours != this.maxCreditHours){
            scheduleChanged = true;
        }
        this.maxCreditHours = maxCreditHours;
    }

    public int getMinNumClasses() {
        return minNumClasses;
    }

    public void setMinNumClasses(int minNumClasses) {
        if (minNumClasses != this.minNumClasses){
            scheduleChanged = true;
        }
        this.minNumClasses = minNumClasses;
    }

    public int getMaxNumClasses() {
        return maxNumClasses;
    }

    public void setMaxNumClasses(int maxNumClasses) {
        if (maxNumClasses != this.maxNumClasses){
            scheduleChanged = true;
        }
        this.maxNumClasses = maxNumClasses;
    }

    public Class getNewClass() {
        return newClass;
    }

    public void setNewClass(Class newClass) {
        if (newClass == null){
            if (this.newClass != null){
                scheduleChanged = true;
            }
        }
        else if (this.newClass == null){
            scheduleChanged = true;
        }
        else if (!newClass.equals(this.newClass)){
            scheduleChanged = true;
        }
        this.newClass = newClass;
    }

    public Class getOldClass() {
        return oldClass;
    }

    public void setOldClass(Class oldClass) {
        if (oldClass == null){
            if (this.oldClass != null){
                scheduleChanged = true;
            }
        }
        else if (this.oldClass == null){
            scheduleChanged = true;
        }
        else if (!oldClass.equals(this.oldClass)){
            scheduleChanged = true;
        }
        this.oldClass = oldClass;
    }

    public Section getNewSection() {
        return newSection;
    }

    public void setNewSection(Section newSection) {
        if (newSection == null){
            if (this.newSection != null){
                scheduleChanged = true;
            }
        }
        else if (this.newSection == null){
            scheduleChanged = true;
        }
        else if (!newSection.equals(this.newSection)){
            scheduleChanged = true;
        }
        this.newSection = newSection;
    }

    public Section getOldSection() {
        return oldSection;
    }

    public void setOldSection(Section oldSection) {
        if (oldSection == null){
            if (this.oldSection != null){
                scheduleChanged = true;
            }
        }
        else if (this.oldSection == null){
            scheduleChanged = true;
        }
        else if (!oldSection.equals(this.oldSection)){
            scheduleChanged = true;
        }
        this.oldSection = oldSection;
    }
}

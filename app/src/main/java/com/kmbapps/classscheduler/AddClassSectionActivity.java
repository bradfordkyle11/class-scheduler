package com.kmbapps.classscheduler;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;


public class AddClassSectionActivity extends ActionBarActivity implements ConfirmationDialogFragment.ConfirmationDialogListener{

    private static final int MONDAY = 1;
    private static final int TUESDAY = 2;
    private static final int WEDNESDAY = 3;
    private static final int THURSDAY = 4;
    private static final int FRIDAY = 5;

    private final static int DEFAULT_START_HOUR = 8;
    private final static int DEFAULT_START_MINUTE = 0;
    private Class myClass;
    private boolean newClass;
    private Section mSection;
    private boolean loadingSection; //true if loading a previously saved section
    private ArrayList<View> dayTimeLocationPickers = new ArrayList<View>();
    private ArrayList<MyTime> currentTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class_section);
        newDayTimeLocationPicker();

        Intent intent = getIntent();
        myClass = (Class) intent.getSerializableExtra("MyClass");
        newClass = intent.getBooleanExtra("newClass", true);
        //testFindTime();

        //restore saved info if editing a section
        if (!newClass){
            loadingSection = true;
            mSection = (Section) intent.getSerializableExtra("mSection");
            ArrayList<MyTime> times = (ArrayList<MyTime>) mSection.getTimes();

            EditText professor = (EditText) findViewById(R.id.edit_professor);
            professor.setText(mSection.getProfessor());

            EditText sectionNumber = (EditText) findViewById(R.id.edit_section_number);
            sectionNumber.setText(mSection.getSectionNumber());

            EditText notes = (EditText) findViewById(R.id.edit_notes);
            notes.setText(mSection.getNotes());

            //set up the dayTimeLocationPickers()
            for(int i = 0; i < times.size(); i++){
                MyTime time = times.get(i);

                //restore room number
                EditText roomNumber = (EditText) dayTimeLocationPickers.get(i).findViewById(R.id.edit_room_number);
                roomNumber.setText(time.getRoomNumber());

                TextView startTime = (TextView) dayTimeLocationPickers.get(i).findViewById(R.id.startTime);
                startTime.setText(MyTime.to12HourFormat(time.getStartHour(), time.getStartMinute()));

                TextView endTime = (TextView) dayTimeLocationPickers.get(i).findViewById(R.id.endTime);
                endTime.setText(MyTime.to12HourFormat(time.getEndHour(), time.getEndMinute()));

                //check the days
                boolean dayChecked = false;
                if(time.getDays().contains("Su")){
                    CheckBox sunday = (CheckBox) dayTimeLocationPickers.get(i).findViewById(R.id.sunCheck);
                    sunday.setChecked(true);
                    dayChecked = true;
                }
                if(time.getDays().contains("M")){
                    CheckBox monday = (CheckBox) dayTimeLocationPickers.get(i).findViewById(R.id.monCheck);
                    monday.setChecked(true);
                    dayChecked = true;
                }
                if(time.getDays().contains("Tu")){
                    CheckBox tuesday = (CheckBox) dayTimeLocationPickers.get(i).findViewById(R.id.tueCheck);
                    tuesday.setChecked(true);
                    dayChecked = true;
                }
                if(time.getDays().contains("W")){
                    CheckBox wednesday = (CheckBox) dayTimeLocationPickers.get(i).findViewById(R.id.wedCheck);
                    wednesday.setChecked(true);
                    dayChecked = true;
                }
                if(time.getDays().contains("Th")){
                    CheckBox thursday = (CheckBox) dayTimeLocationPickers.get(i).findViewById(R.id.thuCheck);
                    thursday.setChecked(true);
                    dayChecked = true;
                }
                if(time.getDays().contains("F")){
                    CheckBox friday = (CheckBox) dayTimeLocationPickers.get(i).findViewById(R.id.friCheck);
                    friday.setChecked(true);
                    dayChecked = true;
                }
                if(time.getDays().contains("Sa")){
                    CheckBox saturday = (CheckBox) dayTimeLocationPickers.get(i).findViewById(R.id.satCheck);
                    saturday.setChecked(true);
                    dayChecked = true;
                }

                //make views visible if a box is checked
                if(dayChecked) {
                    View startAndEndTimes = dayTimeLocationPickers.get(i).findViewById(R.id.startAndEndTimes);
                    startAndEndTimes.setVisibility(View.VISIBLE);
                    View room = dayTimeLocationPickers.get(i).findViewById(R.id.edit_room_number);
                    room.setVisibility(View.VISIBLE);
                }
               // newDayTimeLocationPicker();
            }
            loadingSection = false;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(newClass) {
            getMenuInflater().inflate(R.menu.add_class_section, menu);
        }
        else{
            getMenuInflater().inflate(R.menu.edit_class_section, menu);
        }
        ActionBar actionBar = getSupportActionBar();
        if(newClass) {
            actionBar.setTitle(getString(R.string.header_add_class_section));
        }
        else{
            actionBar.setTitle(getString(R.string.header_edit_class_section));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_save:
                createSectionAndReturn();
                break;

            case R.id.action_delete:
                deleteSectionAndReturn();
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    //TODO: override onSavedInstanceState to save the values in the dayTimeLocationPickers UI



    public void cancel(View view) {
        finish();
    }

    public void setTime(View view) {
        final TextView displayDate = (TextView) view;
        int mHour = 0, mMinute = 0;
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        displayDate.setText(MyTime.to12HourFormat(hourOfDay, minute).toString());
                    }
                }, mHour, mMinute, false);
        if(view.getId()==R.id.startTime){
            String formattedTime = ((TextView) view).getText().toString();
            if(!formattedTime.equals("")){
                int hour = findHour(formattedTime);
                int minute = findMinute(formattedTime);
                tpd.updateTime(hour, minute);
            }
            else {
                tpd.updateTime(DEFAULT_START_HOUR, DEFAULT_START_MINUTE);
            }
        }
        if(view.getId()==R.id.endTime){
            String formattedTime = ((TextView) view).getText().toString();
            if(!formattedTime.equals("")){
                int hour = findHour(formattedTime);
                int minute = findMinute(formattedTime);
                tpd.updateTime(hour, minute);
            }
            else {
                LinearLayout startAndEndTimes = (LinearLayout) view.getParent().getParent();
                TextView startTime = (TextView) startAndEndTimes.findViewById(R.id.startTime);
                if (!startTime.getText().toString().equals("")) {
                    tpd.updateTime(findHour(startTime.getText().toString()), findMinute(startTime.getText().toString()));
                }
                else{
                    tpd.updateTime(DEFAULT_START_HOUR, DEFAULT_START_MINUTE);
                }
            }
        }
        tpd.show();

//        LinearLayout startAndEndTimes = (LinearLayout) view.getParent().getParent();
//        LinearLayout dayTimeLocationPickerLayout = (LinearLayout) view.getParent().getParent().getParent();
//        TextView startTime = (TextView) startAndEndTimes.findViewById(R.id.startTime);
//        TextView endTime = (TextView) startAndEndTimes.findViewById(R.id.endTime);
//
//        if (!startTime.getText().toString().equals("") && !endTime.getText().toString().equals("")){
//            //add another dayTimeLocation picker so the class can have more than one time
//            Integer position = (Integer) dayTimeLocationPickerLayout.getTag();
//            if(dayTimeLocationPickers.size()==position+1) {
//                newDayTimeLocationPicker();
//            }
//        }
    }

    public void newDayTimeLocationPicker() {
        dayTimeLocationPickers.add(findViewById(R.id.dayTimeLocationPicker));

        //inflate the new layout
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dayTimeLocationPickers.set(dayTimeLocationPickers.size()-1, inflater.inflate(R.layout.day_time_location_picker, null));
        dayTimeLocationPickers.get(dayTimeLocationPickers.size()-1).setTag(dayTimeLocationPickers.size()-1);

        setTextChangedListeners();

        LinearLayout layout = (LinearLayout) findViewById(R.id.dayTimeLocationLayout);

        //add the most recent one

        layout.addView(dayTimeLocationPickers.get(dayTimeLocationPickers.size() - 1));
    }

    public void setTextChangedListeners(){
        TextView endTime = (TextView) dayTimeLocationPickers.get(dayTimeLocationPickers.size()-1).findViewById(R.id.endTime);
        endTime.addTextChangedListener(new SelectTimeTextWatcher(endTime) {
            @Override
            public void afterTextChanged(Editable s) {
                LinearLayout startAndEndTimes = (LinearLayout) getView().getParent().getParent();
                LinearLayout dayTimeLocationPickerLayout = (LinearLayout) getView().getParent().getParent().getParent().getParent();
                TextView startTime = (TextView) startAndEndTimes.findViewById(R.id.startTime);
                TextView endTime = (TextView) startAndEndTimes.findViewById(R.id.endTime);

                if (!startTime.getText().toString().equals("") && !endTime.getText().toString().equals("")) {
                    //add another dayTimeLocation picker so the class can have more than one time

                    boolean valid = true;
                    Time sTime = new Time();

                    sTime.hour = findHour(startTime.getText().toString());
                    sTime.minute = findMinute(startTime.getText().toString());

                    Time eTime = new Time();
                    eTime.hour = findHour(endTime.getText().toString());
                    eTime.minute = findMinute(endTime.getText().toString());

                    if(eTime.before(sTime)){
                        CharSequence text = getString(R.string.toast_invalid_ending_time);

                        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                        toast.show();

                        endTime.setText("");
                        valid = false;
                    }

                    Integer position = (Integer) dayTimeLocationPickerLayout.getTag();
                    if (valid && (dayTimeLocationPickers.indexOf(dayTimeLocationPickerLayout)==dayTimeLocationPickers.size()-1)) {
                        newDayTimeLocationPicker();
                    }
                }
                else if(!loadingSection){
                    removeEmptyDayTimeLocationPickers();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });

        TextView startTime = (TextView) dayTimeLocationPickers.get(dayTimeLocationPickers.size()-1).findViewById(R.id.startTime);
        startTime.addTextChangedListener(new SelectTimeTextWatcher(startTime) {
            @Override
            public void afterTextChanged(Editable s) {

                LinearLayout startAndEndTimes = (LinearLayout) getView().getParent().getParent();
                LinearLayout dayTimeLocationPickerLayout = (LinearLayout) getView().getParent().getParent().getParent().getParent();
                TextView startTime = (TextView) startAndEndTimes.findViewById(R.id.startTime);
                TextView endTime = (TextView) startAndEndTimes.findViewById(R.id.endTime);

                if (!startTime.getText().toString().equals("") && !endTime.getText().toString().equals("")) {
                    //add another dayTimeLocation picker so the class can have more than one time
                    boolean valid = true;
                    Time sTime = new Time();

                    sTime.hour = findHour(startTime.getText().toString());
                    sTime.minute = findMinute(startTime.getText().toString());

                    Time eTime = new Time();
                    eTime.hour = findHour(endTime.getText().toString());
                    eTime.minute = findMinute(endTime.getText().toString());

                    if(eTime.before(sTime)){
                        CharSequence text = getString(R.string.toast_invalid_starting_time);

                        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                        toast.show();

                        startTime.setText("");
                        valid = false;
                    }

                    Integer position = (Integer) dayTimeLocationPickerLayout.getTag();
                    if (valid && (dayTimeLocationPickers.indexOf(dayTimeLocationPickerLayout)==dayTimeLocationPickers.size()-1)) {
                        newDayTimeLocationPicker();
                    }
                }
                else if(!loadingSection){
                    removeEmptyDayTimeLocationPickers();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });
    }

    public void removeEmptyDayTimeLocationPickers(){
        for (int i = 0; i < dayTimeLocationPickers.size(); i++){
            View startAndEndTimesLayout = dayTimeLocationPickers.get(i).findViewById(R.id.startAndEndTimes);
            if(startAndEndTimesLayout.getVisibility()==View.GONE){

                LinearLayout layout = (LinearLayout) findViewById(R.id.dayTimeLocationLayout);

                //add the most recent one

                layout.removeView(dayTimeLocationPickers.get(i));
                dayTimeLocationPickers.remove(i);
            }
        }
    }

    public StringBuilder to12HourFormat(int hour, int minute) {
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
            time.append(" PM");
        } else {
            time.append(" AM");
        }

        return time;
    }

    public void showTimeAndLocation(View view) {
        //expands time selector rows if day is checked
        CheckBox checkbox = (CheckBox) view;
        View layout = (View) checkbox.getParent().getParent().getParent().getParent();
        View checkboxesLayout = (View) checkbox.getParent().getParent();

        ArrayList<View> checkboxes = checkboxesLayout.getTouchables();

        boolean dayChecked = false;
        for (int i = 0; i < checkboxes.size(); i++) {
            CheckBox box = (CheckBox) checkboxes.get(i);
            if (box.isChecked()) {
                dayChecked = true;
                break;
            }
        }

        //show time selector and room editor if a day is checked
        if (dayChecked) {
            View v = layout.findViewById(R.id.startAndEndTimes);
            v.setVisibility(View.VISIBLE);
            v = layout.findViewById(R.id.edit_room_number);
            v.setVisibility(View.VISIBLE);
        } else {
            //remove time selector and rome editor. also the entire view if it is not the last one
            View v = layout.findViewById(R.id.startAndEndTimes);
            v.setVisibility(View.GONE);
            v = layout.findViewById(R.id.edit_room_number);
            v.setVisibility(View.GONE);

            if (dayTimeLocationPickers.indexOf(layout)!=dayTimeLocationPickers.size()-1){ //if not the last view in the list
                dayTimeLocationPickers.remove(layout);
                LinearLayout l = (LinearLayout) findViewById(R.id.dayTimeLocationLayout);
                l.removeView(layout);
                findViewById(R.id.scrollView).invalidate();
            }
        }


//        switch (checkbox.getId()) {
//            case R.id.monCheck:
//                l = (LinearLayout) findViewById(R.id.monSetTimeRow);
//                if (checkbox.isChecked()) {
//                    l.setVisibility(View.VISIBLE);
//                } else {
//                    l.setVisibility(View.GONE);
//                }
//                break;
//            case R.id.tueCheck:
//                l = (LinearLayout) findViewById(R.id.tueSetTimeRow);
//                if (checkbox.isChecked()) {
//                    l.setVisibility(View.VISIBLE);
//                } else {
//                    l.setVisibility(View.GONE);
//                }
//                break;
//            case R.id.wedCheck:
//                l = (LinearLayout) findViewById(R.id.wedSetTimeRow);
//                if (checkbox.isChecked()) {
//                    l.setVisibility(View.VISIBLE);
//                } else {
//                    l.setVisibility(View.GONE);
//                }
//                break;
//            case R.id.thuCheck:
//                l = (LinearLayout) findViewById(R.id.thuSetTimeRow);
//                if (checkbox.isChecked()) {
//                    l.setVisibility(View.VISIBLE);
//                } else {
//                    l.setVisibility(View.GONE);
//                }
//                break;
//            case R.id.friCheck:
//                l = (LinearLayout) findViewById(R.id.friSetTimeRow);
//                if (checkbox.isChecked()) {
//                    l.setVisibility(View.VISIBLE);
//                } else {
//                    l.setVisibility(View.GONE);
//                }
//                break;
//        }


    }

    public void createSectionAndReturn() {
        boolean validSchedule = true;
        boolean hasSchedule = false;
        ArrayList<MyTime> times = new ArrayList<MyTime>();
        for (int i = 0; i < dayTimeLocationPickers.size(); i++) {
            MyTime time = new MyTime();
            View checkboxesLayout = dayTimeLocationPickers.get(i).findViewById(R.id.checkboxes);

            ArrayList<View> checkboxes = checkboxesLayout.getTouchables();
            ArrayList<Integer> days = new ArrayList<Integer>();
            for (int j = 0; j < checkboxes.size(); j++) {
                CheckBox box = (CheckBox) checkboxes.get(j);
                if (box.isChecked()) {
                    days.add(j);
                }
            }

            if (days.size() != 0) {
                hasSchedule = true;
                time.setDays(days);

                TextView startTime = (TextView) dayTimeLocationPickers.get(i).findViewById(R.id.startTime);

                if(startTime.getText().toString().equals("")){
                    String text = getString(R.string.toast_no_start_time);
                    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                    toast.show();
                    validSchedule = false;
                    return;
                }
                time.setStartHour(findHour(startTime.getText().toString()));
                time.setStartMinute(findMinute(startTime.getText().toString()));

                TextView endTime = (TextView) dayTimeLocationPickers.get(i).findViewById(R.id.endTime);
                if(endTime.getText().toString().equals("")){
                    String text = getString(R.string.toast_no_end_time);
                    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                    toast.show();
                    validSchedule = false;
                    return;
                }
                time.setEndHour(findHour(endTime.getText().toString()));
                time.setEndMinute(findMinute(endTime.getText().toString()));

                EditText roomNumber = (EditText) dayTimeLocationPickers.get(i).findViewById(R.id.edit_room_number);
                time.setRoomNumber(roomNumber.getText().toString());

                times.add(time);
            }

        }

        // TODO: highlight the offending schedule or time selector

        if(!MyTime.noConflicts(times)){
            validSchedule = false;
            String text = getString(R.string.toast_conflicting_schedules);
            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            toast.show();
        }

        if(!validSchedule){
            return;
        }
        if(!hasSchedule){
            String text = getString(R.string.toast_no_schedule);
            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            toast.show();
            validSchedule = false;
            return;
        }

        EditText et = (EditText) findViewById(R.id.edit_professor);
        String professor = et.getText().toString();

        et = (EditText) findViewById(R.id.edit_section_number);
        String sectionNumber = et.getText().toString();

        et = (EditText) findViewById(R.id.edit_notes);
        String notes = et.getText().toString();



        if(mSection==null) {
            mSection = new Section(times, professor, sectionNumber, notes, myClass);
            myClass.addSection(mSection);
        }
        else{
            int replaceIndex = myClass.getSections().indexOf(mSection);
            mSection.setTimes(times);
            mSection.setProfessor(professor);
            mSection.setSectionNumber(sectionNumber);
            mSection.setNotes(notes);
            mSection.setContainingClass(myClass);
            myClass.getSections().set(replaceIndex, mSection);
        }

        //TODO: use saveSection instead
        ClassLoader.saveClass(this, myClass);
        ClassLoader.updateSchedules();


        //return
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }


    //TODO: move these to MyTime
    //CAUTION: findMinute and findHour will break if the toString method of MyTime is changed

    private int findMinute(String time) {
        StringBuilder timeSB = new StringBuilder(time);
        StringBuilder minute = new StringBuilder("");
        minute.append(timeSB.charAt(timeSB.indexOf(":") + 1));
        minute.append(timeSB.charAt(timeSB.indexOf(":") + 2));
        return Integer.valueOf(minute.toString());

    }

    private int findHour(String time) {
        StringBuilder timeSB = new StringBuilder(time);
        StringBuilder hour = new StringBuilder("");

        //if AM
        if (!(timeSB.indexOf("a") < 0)) {
            switch (timeSB.length()) {
                case 5:
                    hour.append(timeSB.charAt(0));
                    break;
                case 6:
                    hour.append(timeSB.charAt(0));
                    hour.append(timeSB.charAt(1));
                    if (hour.toString().equals("12")) {
                        hour = new StringBuilder("0");
                    }
                    break;
            }
        }
        //if PM
        else {
            switch (timeSB.length()) {
                case 5:
                    hour.append(timeSB.charAt(0));
                    int newHour = Integer.valueOf(hour.toString()) + 12;
                    hour = new StringBuilder(Integer.toString(newHour));
                    break;
                case 6:
                    hour.append(timeSB.charAt(0));
                    hour.append(timeSB.charAt(1));

                    //add 12 to time unless it is 12 pm
                    if (!hour.toString().equals("12")) {
                        newHour = Integer.valueOf(hour.toString()) + 12;
                        hour = new StringBuilder(Integer.toString(newHour));
                    }
                    break;
            }
        }

        return Integer.valueOf(hour.toString());

    }

    public void testFindTime() {
        int hour;
        int minute;
        String colon = ":";
        String amOrPm = " AM";

        StringBuilder time = new StringBuilder();

        for (hour = 1; hour < 13; hour++) {
            for (minute = 0; minute < 60; minute++) {
                time = new StringBuilder("");
                if (minute < 10) {
                    time.append(hour).append(colon).append("0").append(minute).append(amOrPm);
                } else {
                    time.append(hour).append(colon).append(minute).append(amOrPm);
                }
                int newHour = findHour(time.toString());
                if (hour == 12) {
                    if (BuildConfig.DEBUG && newHour != 0) {
                        throw new AssertionError();
                    }
                } else {
                    if (BuildConfig.DEBUG && newHour != hour) {
                        throw new AssertionError();
                    }
                }

                int newMinute = findMinute(time.toString());
                if (BuildConfig.DEBUG && newMinute != minute) {
                    throw new AssertionError();
                }
            }
        }

        amOrPm = " PM";

        for (hour = 1; hour < 13; hour++) {
            for (minute = 0; minute < 60; minute++) {
                time = new StringBuilder("");
                if (minute < 10) {
                    time.append(hour).append(colon).append("0").append(minute).append(amOrPm);
                } else {
                    time.append(hour).append(colon).append(minute).append(amOrPm);
                }

                int newHour = findHour(time.toString());
                if (hour == 12) {
                    if (BuildConfig.DEBUG && newHour != 12) {
                        throw new AssertionError();
                    }
                } else {
                    if (BuildConfig.DEBUG && newHour != hour + 12) {
                        throw new AssertionError();
                    }
                }

                int newMinute = findMinute(time.toString());
                if (BuildConfig.DEBUG && newMinute != minute) {
                    throw new AssertionError();
                }
            }
        }


    }

    private void deleteSectionAndReturn(){
        ConfirmationDialogFragment.newInstance(getString(R.string.title_delete_section_confirmation), mSection, ConfirmationDialogFragment.ACTIVITY)
                .show(getSupportFragmentManager(), "confirmation");
    }

    //delete section confirmed
    @Override
    public void onConfirmationPositiveClick(ConfirmationDialogFragment dialog) {
        Class containingClass = mSection.getContainingClass();
        ClassLoader.removeSection(this, mSection, containingClass);

        //notify user
        Toast toast = Toast.makeText(this, getString(R.string.toast_section_deleted), Toast.LENGTH_SHORT);
        toast.show();

        //return
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    //delete section canceled
    @Override
    public void onConfirmationNegativeClick(ConfirmationDialogFragment dialog) {

    }
}

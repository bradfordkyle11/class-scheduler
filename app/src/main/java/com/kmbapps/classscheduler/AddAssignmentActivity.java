package com.kmbapps.classscheduler;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;


public class AddAssignmentActivity extends ActionBarActivity implements ConfirmationDialogFragment.ConfirmationDialogListener{

    public static final int NEW_ASSIGNMENT = 100;
    public static final int EDITED_ASSIGNMENT = 101;
    public static final int DELETE_ASSIGNMENT = 102;

    public static final int CREATE_ASSIGNMENT = 0;
    public static final int EDIT_ASSIGNMENT = 1;
    public static final int EDIT_GRADED_ASSIGNMENT = 2;

    private int mode;
    private Assignment mAssignment;

    private Calendar dueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment);

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", CREATE_ASSIGNMENT);

        if(mode==CREATE_ASSIGNMENT){
            //hide views that don't apply
            View gradeLayout = findViewById(R.id.gradeLayout);
            gradeLayout.setVisibility(View.GONE);
        }

        //restore assignment info if editing an assignment
        if(mode==EDIT_ASSIGNMENT){
            mAssignment = (Assignment) intent.getSerializableExtra("assignment");
            TextView typeTV = (TextView) findViewById(R.id.assignmentType);
            typeTV.setText(mAssignment.getType());
            TextView nameTV = (TextView) findViewById(R.id.assignmentName);
            nameTV.setText(mAssignment.getName());
            TextView detailsTV = (TextView) findViewById(R.id.assignmentDetails);
            detailsTV.setText(mAssignment.getDetails());

            TextView dueDateTV = (TextView) findViewById(R.id.assignmentDueDate);
            this.dueDate = mAssignment.getDueDate();
            if(dueDate!=null){
                dueDateTV.setText("Due " + (dueDate.get(Calendar.MONTH)+1) + "/" + dueDate.get(Calendar.DAY_OF_MONTH) + "/" + dueDate.get(Calendar.YEAR));

                TextView timeTV = (TextView) findViewById(R.id.assignmentDueTime);
                timeTV.setVisibility(View.VISIBLE);
                timeTV.setText(" at " + MyTime.to12HourFormat(dueDate.get(Calendar.HOUR_OF_DAY), dueDate.get(Calendar.MINUTE)));
            }



            //hide views that don't apply
            View gradeLayout = findViewById(R.id.gradeLayout);
            gradeLayout.setVisibility(View.GONE);
        }
        //restore assignment info if editing a graded assignment
        else if(mode==EDIT_GRADED_ASSIGNMENT){
            mAssignment = (Assignment) intent.getSerializableExtra("assignment");
            TextView typeTV = (TextView) findViewById(R.id.assignmentType);
            typeTV.setText(mAssignment.getType());
            TextView nameTV = (TextView) findViewById(R.id.assignmentName);
            nameTV.setText(mAssignment.getName());
            TextView detailsTV = (TextView) findViewById(R.id.assignmentDetails);
            detailsTV.setText(mAssignment.getDetails());

            TextView gradeTV = (TextView) findViewById(R.id.assignmentGrade);
            gradeTV.setText(mAssignment.getGrade());

            this.dueDate = mAssignment.getDueDate();

            //hide views that don't apply
            LinearLayout dueDateLayout = (LinearLayout) findViewById(R.id.dueDateLayout);
            dueDateLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(mode==EDIT_ASSIGNMENT){
            getMenuInflater().inflate(R.menu.edit_assignment, menu);
        }
        else if(mode==EDIT_GRADED_ASSIGNMENT){
            getMenuInflater().inflate(R.menu.edit_assignment, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.menu_add_assignment, menu);
        }

        //set action bar title
        ActionBar actionBar = getSupportActionBar();
        if(mode==EDIT_ASSIGNMENT||mode==EDIT_GRADED_ASSIGNMENT){
            actionBar.setTitle(getString(R.string.header_edit_assignment));
        }
        else{
            actionBar.setTitle(getString(R.string.header_add_assignment));
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_save:
                saveAssignment();
                return true;
            case R.id.action_delete:
                deleteAssignment();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void selectDate(View view){
        Calendar calendar = Calendar.getInstance();
        TextView dateTV = (TextView) view;

        if(dueDate==null) {
            DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    setDate(datePicker, year, monthOfYear, dayOfMonth);


                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            dpd.show();
        }
        else{
            DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    setDate(datePicker, year, monthOfYear, dayOfMonth);


                }
            }, dueDate.get(Calendar.YEAR), dueDate.get(Calendar.MONTH), dueDate.get(Calendar.DAY_OF_MONTH));

            dpd.show();
        }
    }

    public void selectTime(View view){
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        dueDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dueDate.set(Calendar.MINUTE, minute);
                        TextView timeTV = (TextView) findViewById(R.id.assignmentDueTime);
                        timeTV.setText(" at " + MyTime.to12HourFormat(dueDate.get(Calendar.HOUR_OF_DAY), dueDate.get(Calendar.MINUTE)));
                    }
                }, dueDate.get(Calendar.HOUR_OF_DAY), dueDate.get(Calendar.MINUTE), false);

        tpd.show();
    }

    private void setDate(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth){

        //create a new date if null
        if(dueDate==null) {
            dueDate = Calendar.getInstance();
            dueDate.setTimeInMillis(0);
            dueDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
        }

        //otherwise, just update the date
        else{
            dueDate.set(Calendar.YEAR, year);
            dueDate.set(Calendar.MONTH, monthOfYear);
            dueDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }

        TextView timeTV = (TextView) findViewById(R.id.assignmentDueTime);
        timeTV.setVisibility(View.VISIBLE);
        timeTV.setText(" at " + MyTime.to12HourFormat(dueDate.get(Calendar.HOUR_OF_DAY), dueDate.get(Calendar.MINUTE)));

        String date = "Due " + (monthOfYear+1) + "/" + dayOfMonth + "/" + year;
        TextView dateTV = (TextView) findViewById(R.id.assignmentDueDate);
        dateTV.setText(date);

    }

    private void saveAssignment(){
        TextView typeTV = (TextView) findViewById(R.id.assignmentType);
        String type = typeTV.getText().toString();
        TextView nameTV = (TextView) findViewById(R.id.assignmentName);
        String name = nameTV.getText().toString();
        TextView detailsTV = (TextView) findViewById(R.id.assignmentDetails);
        String details = detailsTV.getText().toString();

        Assignment assignment = new Assignment(dueDate, type, name, details, "");

        if(mode==EDIT_GRADED_ASSIGNMENT){
            TextView gradeTV = (TextView) findViewById(R.id.assignmentGrade);
            String grade = gradeTV.getText().toString();
            assignment.setGrade(grade);
            assignment.setCompletionDate(mAssignment.getCompletionDate());
        }

        Intent intent = new Intent();
        intent.putExtra("newAssignment", assignment);
        if(mode==EDIT_ASSIGNMENT){
            intent.putExtra("oldAssignment", mAssignment);
            setResult(EDITED_ASSIGNMENT, intent);
        }
        else if(mode==EDIT_GRADED_ASSIGNMENT){
            intent.putExtra("oldAssignment", mAssignment);
            setResult(EDITED_ASSIGNMENT, intent);
        }
        else {
            setResult(NEW_ASSIGNMENT, intent);
        }
        finish();
    }

    private void deleteAssignment(){
        ConfirmationDialogFragment.newInstance(getString(R.string.title_delete_assignment_confirmation), mAssignment, ConfirmationDialogFragment.ACTIVITY)
                .show(getSupportFragmentManager(), "confirmation");
    }

    //delete assignment confirmed
    @Override
    public void onConfirmationPositiveClick(ConfirmationDialogFragment dialog) {
        Intent intent = new Intent();
        intent.putExtra("assignmentToDelete", mAssignment);
        setResult(DELETE_ASSIGNMENT, intent);
        finish();
    }

    //delete assignment canceled
    @Override
    public void onConfirmationNegativeClick(ConfirmationDialogFragment dialog) {

    }
}

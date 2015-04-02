package com.kmbapps.classscheduler;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;


public class AddAssignmentActivity extends ActionBarActivity {

    public static final int NEW_ASSIGNMENT = 100;
    public static final int EDITED_ASSIGNMENT = 101;

    private boolean editMode;
    private Assignment mAssignment;

    private Calendar dueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment);

        Intent intent = getIntent();
        editMode = intent.getBooleanExtra("editMode", false);

        //restore assignment info if editing an assignment
        if(editMode==true){
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
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_assignment, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.header_add_assignment));
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

            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void selectDate(View view){
        Calendar calendar = Calendar.getInstance();
        final TextView dateTV = (TextView) view;

        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                dateTV.setText("Due " + (monthOfYear+1) + "/" + dayOfMonth + "/" + year);
                dueDate = Calendar.getInstance();
                dueDate.setTimeInMillis(0);
                dueDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0);

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dpd.show();
    }

    private void saveAssignment(){
        TextView typeTV = (TextView) findViewById(R.id.assignmentType);
        String type = typeTV.getText().toString();
        TextView nameTV = (TextView) findViewById(R.id.assignmentName);
        String name = nameTV.getText().toString();
        TextView detailsTV = (TextView) findViewById(R.id.assignmentDetails);
        String details = detailsTV.getText().toString();



        Assignment assignment = new Assignment(dueDate, type, name, details, 0);

        Intent intent = new Intent();
        intent.putExtra("newAssignment", assignment);
        if(editMode){
            intent.putExtra("oldAssignment", mAssignment);
            setResult(EDITED_ASSIGNMENT, intent);
        }
        else {
            setResult(NEW_ASSIGNMENT, intent);
        }
        finish();
    }
}

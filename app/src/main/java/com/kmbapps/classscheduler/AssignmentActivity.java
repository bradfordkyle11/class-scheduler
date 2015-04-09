package com.kmbapps.classscheduler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class AssignmentActivity extends ActionBarActivity {

    public final static int EDITED_ASSIGNMENT = 100;
    public final static int DELETE_ASSIGNMENT = 101;

    public final static int UPCOMING_ASSIGNMENT = 0;
    public final static int GRADED_ASSIGNMENT = 1;

    public final static int ASSIGNMENT_CREATOR_REQUEST = 0;

    private Assignment mAssignment;
    private Assignment newAssignment;

    private Section mSection;

    private int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        mAssignment = (Assignment) getIntent().getSerializableExtra("assignment");
        mSection = (Section) getIntent().getSerializableExtra("section");
        mode = getIntent().getIntExtra("mode", UPCOMING_ASSIGNMENT);

        TextView typeAndName = (TextView) findViewById(R.id.assignmentTypeAndName);
        typeAndName.setText(mAssignment.getType() + ": " + mAssignment.getName());
        TextView dueDateTV = (TextView) findViewById(R.id.assignmentDueDate);
        if(mode == UPCOMING_ASSIGNMENT) {
            Calendar dueDate = mAssignment.getDueDate();
            if (dueDate != null) {
                String dueDateString = (dueDate.get(Calendar.MONTH) + 1) + "/" + dueDate.get(Calendar.DAY_OF_MONTH) + "/" + dueDate.get(Calendar.YEAR)
                        + " at " + MyTime.to12HourFormat(dueDate.get(Calendar.HOUR_OF_DAY), dueDate.get(Calendar.MINUTE));
                dueDateTV.setText(Html.fromHtml("<b>Due:</b> " + dueDateString));
            } else {
                dueDateTV.setText(Html.fromHtml("<i>No due date</i>"));
            }

            //hide unneeded views
            TextView gradeTV = (TextView) findViewById(R.id.assignmentGrade);
            gradeTV.setVisibility(View.GONE);
        }
        else if (mode == GRADED_ASSIGNMENT){
            Calendar completionDate = mAssignment.getCompletionDate();
            if (completionDate != null) {
                String completionDateString = (completionDate.get(Calendar.MONTH) + 1) + "/" + completionDate.get(Calendar.DAY_OF_MONTH) + "/" + completionDate.get(Calendar.YEAR)
                        + " at " + MyTime.to12HourFormat(completionDate.get(Calendar.HOUR_OF_DAY), completionDate.get(Calendar.MINUTE));
                dueDateTV.setText(Html.fromHtml("<b>Completed:</b> " + completionDateString));
            }

            TextView gradeTV = (TextView) findViewById(R.id.assignmentGrade);
            gradeTV.setText(Html.fromHtml("<b>Grade:</b> " + mAssignment.getGrade()));
        }
        TextView details = (TextView) findViewById(R.id.assignmentDetails);
        details.setText(Html.fromHtml("<b>Details:</b> " + mAssignment.getDetails()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_assignment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent();
                if(newAssignment!=null){
//                    intent.putExtra("newAssignment", newAssignment);
//                    intent.putExtra("oldAssignment", mAssignment);
                    setResult(EDITED_ASSIGNMENT, intent);
                }
                else{
                    setResult(Activity.RESULT_CANCELED);
                }
                finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case ASSIGNMENT_CREATOR_REQUEST:

                if(resultCode==AddAssignmentActivity.EDITED_ASSIGNMENT){
                    Assignment newAssignment = (Assignment) data.getSerializableExtra("newAssignment");
                    if(mAssignment.equals(newAssignment)){
                        //notify the user that an assignment was not changed
                        Toast toast = Toast.makeText(this, getString(R.string.toast_assignment_no_change), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else {
                        this.newAssignment = newAssignment;
                        if (mode==UPCOMING_ASSIGNMENT) {
                            ClassLoader.saveAssignment(this, newAssignment, mAssignment, mSection);
                        }
                        else if (mode==GRADED_ASSIGNMENT){
                            ClassLoader.saveGradedAssignment(this, newAssignment, mAssignment, mSection);
                        }


                        mAssignment = newAssignment;

                        //notify the user that an assignment was edited
                        Toast toast = Toast.makeText(this, getString(R.string.toast_assignment_edited), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    updateAssignmentView(newAssignment);

                }
                else if(resultCode==AddAssignmentActivity.DELETE_ASSIGNMENT){
                    Intent intent = new Intent();
                    intent.putExtra("assignmentToDelete", mAssignment);
                    setResult(DELETE_ASSIGNMENT, intent);
                    finish();
                }
                break;
        }

    }

    public void editAssignment(View v){
        Intent intent = new Intent(this, AddAssignmentActivity.class);
        if(mode==UPCOMING_ASSIGNMENT) {
            intent.putExtra("mode", AddAssignmentActivity.EDIT_ASSIGNMENT);
        }
        else if(mode==GRADED_ASSIGNMENT){
            intent.putExtra("mode", AddAssignmentActivity.EDIT_GRADED_ASSIGNMENT);
        }
        if(newAssignment==null) {
            intent.putExtra("assignment", mAssignment);
        }
        else{
            intent.putExtra("assignment", newAssignment);
        }
        startActivityForResult(intent, ASSIGNMENT_CREATOR_REQUEST);
    }

    //updates the UI to show the edited assignment
    private void updateAssignmentView(Assignment newAssignment){
        TextView typeAndName = (TextView) findViewById(R.id.assignmentTypeAndName);
        typeAndName.setText(newAssignment.getType() + ": " + newAssignment.getName());


        TextView details = (TextView) findViewById(R.id.assignmentDetails);
        details.setText(Html.fromHtml("<b>Details:</b> " + newAssignment.getDetails()));

        if(mode==UPCOMING_ASSIGNMENT) {
            TextView dueDateTV = (TextView) findViewById(R.id.assignmentDueDate);
            Calendar dueDate = newAssignment.getDueDate();
            if(dueDate!=null) {
                String dueDateString = (dueDate.get(Calendar.MONTH) + 1) + "/" + dueDate.get(Calendar.DAY_OF_MONTH) + "/" + dueDate.get(Calendar.YEAR)
                        + " at " + MyTime.to12HourFormat(dueDate.get(Calendar.HOUR_OF_DAY), dueDate.get(Calendar.MINUTE));
                dueDateTV.setText(Html.fromHtml("<b>Due:</b> " + dueDateString));
            }
        }
        else if(mode==GRADED_ASSIGNMENT){
            TextView gradeTV = (TextView) findViewById(R.id.assignmentGrade);
            gradeTV.setText(Html.fromHtml("<b>Grade:</b> " + mAssignment.getGrade()));
        }


    }


}

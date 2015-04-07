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

    public final static int ASSIGNMENT_CREATOR_REQUEST = 0;

    private Assignment mAssignment;
    private Assignment newAssignment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        mAssignment = (Assignment) getIntent().getSerializableExtra("assignment");

        TextView typeAndName = (TextView) findViewById(R.id.assignmentTypeAndName);
        typeAndName.setText(mAssignment.getType() + ": " + mAssignment.getName());
        TextView dueDateTV = (TextView) findViewById(R.id.assignmentDueDate);
        Calendar dueDate = mAssignment.getDueDate();
        if(dueDate!=null) {
            dueDateTV.setText(Html.fromHtml("<b>Due:</b> " + (dueDate.get(Calendar.MONTH) + 1) + "/" + dueDate.get(Calendar.DAY_OF_MONTH) + "/" + dueDate.get(Calendar.YEAR)));
        }
        else{
            dueDateTV.setText(Html.fromHtml("<i>No due date</i>"));
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
                    intent.putExtra("newAssignment", newAssignment);
                    intent.putExtra("oldAssignment", mAssignment);
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
                        ClassLoader.updateNotebooks(this);
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
        intent.putExtra("editMode", true);
        intent.putExtra("assignment", mAssignment);
        startActivityForResult(intent, ASSIGNMENT_CREATOR_REQUEST);
    }

    //updates the UI to show the edited assignment
    private void updateAssignmentView(Assignment newAssignment){
        TextView typeAndName = (TextView) findViewById(R.id.assignmentTypeAndName);
        typeAndName.setText(newAssignment.getType() + ": " + newAssignment.getName());
        TextView dueDateTV = (TextView) findViewById(R.id.assignmentDueDate);
        Calendar dueDate = newAssignment.getDueDate();
        dueDateTV.setText("Due: " + (dueDate.get(Calendar.MONTH)+1) + "/" + dueDate.get(Calendar.DAY_OF_MONTH) + "/" + dueDate.get(Calendar.YEAR));
        TextView details = (TextView) findViewById(R.id.assignmentDetails);
        details.setText("Details:\n" + newAssignment.getDetails());
    }


}

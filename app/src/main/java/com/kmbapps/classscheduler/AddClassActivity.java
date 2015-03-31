package com.kmbapps.classscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;


public class AddClassActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        Spinner spinner = (Spinner) findViewById(R.id.creditHours);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.credit_hours_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_class, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.header_add_class));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent intent = new Intent(this, Home.class);
        switch (item.getItemId()) {
            case R.id.action_save:
                createClass();
                //return
                startActivity(intent);
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void createClass() {
        EditText classDepartment = (EditText) findViewById(R.id.classDepartment);
        String department = classDepartment.getText().toString();

        EditText classNumber = (EditText) findViewById(R.id.classNumber);
        String number = classNumber.getText().toString();

        EditText className = (EditText) findViewById(R.id.className);
        String name = className.getText().toString();

        Spinner creditHours = (Spinner) findViewById(R.id.creditHours);
        int hours = Integer.parseInt(creditHours.getSelectedItem().toString());

        Class newClass = new Class(department, number, name, hours);

        //save the new class
        ClassLoader.saveClass(this.getApplicationContext(), newClass);

    }



}

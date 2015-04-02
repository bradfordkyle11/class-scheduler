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


public class EditClassActivity extends ActionBarActivity {

    Class mClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mClass = (Class) intent.getSerializableExtra("MyClass");

        setContentView(R.layout.activity_add_class);

        Spinner spinner = (Spinner) findViewById(R.id.creditHours);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.credit_hours_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(mClass.getCreditHours() - 1);

        EditText department = (EditText) findViewById(R.id.classDepartment);
        department.setText(mClass.getDepartment());

        EditText number = (EditText) findViewById(R.id.classNumber);
        number.setText(mClass.getNumber());

        EditText name = (EditText) findViewById(R.id.className);
        name.setText(mClass.getName());

        //TODO: show the sections of this class in the UI as well, and allow them to be edited

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_class, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.header_edit_class));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = new Intent(this, Home.class);
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteClass();
                //return
                startActivity(intent);
                break;
            case R.id.action_save:
                saveClass();
                //return
                startActivity(intent);
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void saveClass() {
        EditText classDepartment = (EditText) findViewById(R.id.classDepartment);
        mClass.setDepartment(classDepartment.getText().toString());

        EditText classNumber = (EditText) findViewById(R.id.classNumber);
        mClass.setNumber(classNumber.getText().toString());

        EditText className = (EditText) findViewById(R.id.className);
        mClass.setName(className.getText().toString());

        Spinner creditHours = (Spinner) findViewById(R.id.creditHours);
        mClass.setCreditHours(Integer.parseInt(creditHours.getSelectedItem().toString()));


        //save the changes
        ClassLoader.saveClass(this.getApplicationContext(), mClass);

        //return
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);

    }

    public void deleteClass(){
        ClassLoader.removeClass(this, mClass);
    }
}

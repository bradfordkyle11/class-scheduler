package com.kmbapps.classscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class EditClassActivity extends AppCompatActivity implements ConfirmationDialogFragment.ConfirmationDialogListener{

    Class mClass;
    Class updatedClass;
    int where;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mClass = (Class) intent.getSerializableExtra("MyClass");
        where = intent.getIntExtra("where", ClassLoader.DESIRED_CLASSES);

        setContentView(R.layout.activity_add_class);

        Spinner spinner = (Spinner) findViewById(R.id.creditHours);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.credit_hours_array, R.layout.spinner_dropdown_item);
        // Specify the layout to use when the list of choices appears
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(mClass.getCreditHours() - 1); //credit hours from 1+ so subtract 1

        Spinner priority = (Spinner) findViewById(R.id.priority);
        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, R.layout.spinner_dropdown_item);
        // Specify the layout to use when the list of choices appears
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        priority.setAdapter(priorityAdapter);
        priority.setSelection(mClass.getPriority()); //priority from 0+

        EditText department = (EditText) findViewById(R.id.classDepartment);
        department.setText(mClass.getDepartment());

        EditText number = (EditText) findViewById(R.id.classNumber);
        number.setText(mClass.getNumber());

        EditText name = (EditText) findViewById(R.id.className);
        name.setText(mClass.getName());

        //TODO: show the sections of this class in the UI as well, and allow them to be edited

    }

    @Override
    protected void onPause() {
        //ClassLoader.save(this);
        super.onPause();
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

                break;
            case R.id.action_save:
                if (saveClass()){
                    startActivity(intent);
                }
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public boolean saveClass() {
        updatedClass = new Class();
        EditText classDepartment = (EditText) findViewById(R.id.classDepartment);
        updatedClass.setDepartment(classDepartment.getText().toString());

        EditText classNumber = (EditText) findViewById(R.id.classNumber);
        updatedClass.setNumber(classNumber.getText().toString());

        EditText className = (EditText) findViewById(R.id.className);
        updatedClass.setName(className.getText().toString());

        Spinner creditHours = (Spinner) findViewById(R.id.creditHours);
        updatedClass.setCreditHours(Integer.parseInt(creditHours.getSelectedItem().toString()));

        Spinner classPriority = (Spinner) findViewById(R.id.priority);
        updatedClass.setPriority(classPriority.getSelectedItemPosition());

        if(mClass!=null){
            updatedClass.setSections(mClass.getSections());
        }


        //save the changes
        if (ClassLoader.saveClass(this.getApplicationContext(), updatedClass, mClass, where)){
            return true;
        }

        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.toast_class_already_exists), Toast.LENGTH_SHORT);
        toast.show();
        return false;

    }

    public void deleteClass(){
        ConfirmationDialogFragment.newInstance(getString(R.string.title_delete_class_confirmation), mClass, ConfirmationDialogFragment.ACTIVITY)
                .show(getSupportFragmentManager(), "dialog");
    }

    //delete class confirmed
    @Override
    public void onConfirmationPositiveClick(ConfirmationDialogFragment dialog) {
        ClassLoader.removeClass(this, mClass);
        Toast toast = Toast.makeText(this, getString(R.string.toast_class_deleted), Toast.LENGTH_SHORT);
        toast.show();

        //return
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    //delete class canceled
    @Override
    public void onConfirmationNegativeClick(ConfirmationDialogFragment dialog) {

    }
}

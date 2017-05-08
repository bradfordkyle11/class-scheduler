package com.kmbapps.classscheduler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;


public class ClassGradesFragment extends Fragment implements ConfirmationDialogFragment.ConfirmationDialogListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GRADES = "grades";
    private static final String ARG_SECTION = "section";
    private static final int ASSIGNMENT_CREATOR_REQUEST = 0;
    private static final int OPEN_ASSIGNMENT_REQUEST = 1;

    private int sortingMode;

    private ActionMode mActionMode;

    private ArrayList<Assignment> mGradedAssignments;
    private Section mSection;

    private OnGradesFragmentInteractionListener mListener;


    public static ClassGradesFragment newInstance(ArrayList<Assignment> grades, Section section) {
        ClassGradesFragment fragment = new ClassGradesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_GRADES, grades);
        args.putSerializable(ARG_SECTION, section);
        fragment.setArguments(args);
        return fragment;
    }

    public ClassGradesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGradedAssignments = (ArrayList<Assignment>) getArguments().getSerializable(ARG_GRADES);
            mSection = (Section) getArguments().getSerializable(ARG_SECTION);
        }
        if(mGradedAssignments ==null){
            sortingMode = 1;
        }
        else if(mGradedAssignments.isEmpty()){
            sortingMode = 1;
        }
        else{
            sortingMode = mGradedAssignments.get(0).getSortingMode();
        }
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_class_grades, container, false);

        //add the assignments to the view
        LinearLayout assignmentsList = (LinearLayout) v.findViewById(R.id.gradesList);

        Collections.sort(mGradedAssignments);

        ArrayList<String> types = new ArrayList<>();
        for(Assignment gradedAssignment : mGradedAssignments){
            if(!types.contains(gradedAssignment.getType())){
                types.add(gradedAssignment.getType());
            }
        }

        for(String type : types) {

            //set up type group
            ArrayList<String> grades = new ArrayList<>();
            LinearLayout gradeTypeListGroup = (LinearLayout) inflater.inflate(R.layout.list_group_grade_type, null);
            LinearLayout typedAssignmentList = (LinearLayout) gradeTypeListGroup.findViewById(R.id.assignmentsList);

            TextView typeTV = (TextView) gradeTypeListGroup.findViewById(R.id.assignmentType);
            typeTV.setText(type);


            for (Assignment gradedAssignment : mGradedAssignments) {

                if (gradedAssignment.getType().equals(type)) {
                    grades.add(gradedAssignment.getGrade());

                    LinearLayout gradedAssignmentListItem = (LinearLayout) inflater.inflate(R.layout.list_item_graded_assignment, null);
                    gradedAssignmentListItem.setTag(gradedAssignment);
                    TextView assignmentName = (TextView) gradedAssignmentListItem.findViewById(R.id.assignmentName);
                    assignmentName.setText(gradedAssignment.getName());
                    TextView assignmentGrade = (TextView) gradedAssignmentListItem.findViewById(R.id.assignmentGrade);
                    assignmentGrade.setText(gradedAssignment.getGrade());

                    //add action menu listener
                    gradedAssignmentListItem.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            actionModeSetup(v);
                            return true;
                        }
                    });

                    gradedAssignmentListItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mActionMode==null) {
                                openAssignment(v);
                            }
                            else{
                                actionModeSetup(v);
                            }
                        }
                    });

                    typedAssignmentList.addView(gradedAssignmentListItem);
                }
            }

            //update the grade and add the view
            TextView gradeTV = (TextView) gradeTypeListGroup.findViewById(R.id.grade);
            gradeTV.setText(Double.toString(average(grades)));
            if(gradeTV.getText().toString().equals("NaN")){
                gradeTV.setText(getString(R.string.text_no_grades_for_type));
            }
            assignmentsList.addView(gradeTypeListGroup);

        }

        //add a buffer to the bottom of the list
        LinearLayout buffer = (LinearLayout) inflater.inflate(R.layout.scroll_view_buffer, null);
        assignmentsList.addView(buffer);

        return v;
    }

    public void onActionModeChanged() {
        if (mListener != null) {
            mListener.onActionModeChanged(mActionMode);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = null;
        if (context instanceof Activity){
            activity = (Activity) context;
        }
        // Verify that the host activity implements the callback interface
        if (activity != null) {
            try {
                mListener = (OnGradesFragmentInteractionListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_class_assignments, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_sort_by:
                new SortByDialogFragment().newInstance(sortingMode).show(getActivity().getSupportFragmentManager(), "SortByDialogFragment");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public interface OnGradesFragmentInteractionListener {
        void onActionModeChanged(ActionMode actionMode);
        void onGradesChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case ASSIGNMENT_CREATOR_REQUEST:
                if(resultCode==AddAssignmentActivity.NEW_ASSIGNMENT){
                    Assignment newAssignment = (Assignment) data.getSerializableExtra("newAssignment");
                    //TODO: newGrade method
 //                   newAssignment(newAssignment);
                }
                else if(resultCode==AddAssignmentActivity.EDITED_ASSIGNMENT){
                    Assignment newAssignment = (Assignment) data.getSerializableExtra("newAssignment");
                    Assignment oldAssignment = (Assignment) data.getSerializableExtra("oldAssignment");
                    editGradedAssignment(newAssignment, oldAssignment);
                }
                else if(resultCode==AddAssignmentActivity.DELETE_ASSIGNMENT){
                    Assignment assignmentToDelete = (Assignment) data.getSerializableExtra("assignmentToDelete");
                    deleteGradedAssignment(assignmentToDelete);
                }
                break;

            case OPEN_ASSIGNMENT_REQUEST:
                if (resultCode==AssignmentActivity.EDITED_ASSIGNMENT){
                    if(mListener!=null){
                        mListener.onGradesChanged();
                    }
                }
                else if (resultCode==AssignmentActivity.DELETE_ASSIGNMENT){
                    Assignment assignmentToDelete = (Assignment) data.getSerializableExtra("assignmentToDelete");
                    deleteGradedAssignment(assignmentToDelete);
                }
        }

    }


    //action mode that is activated when long pressing an assignment
    private ActionMode.Callback gradeActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu_grade_list_item, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    View v = (View) mode.getTag();

                    Intent intent = new Intent(getActivity().getApplicationContext(), AddAssignmentActivity.class);
                    intent.putExtra("mode", AddAssignmentActivity.EDIT_GRADED_ASSIGNMENT);
                    intent.putExtra("assignment", (Assignment) v.getTag());
                    startActivityForResult(intent, ASSIGNMENT_CREATOR_REQUEST);
                    mode.finish();
                    return true;

                case R.id.action_delete:
                    if(mode.getTag() instanceof View){
                        v = (View) mode.getTag();
                        Assignment gradeToDelete = (Assignment) v.getTag();
                        deleteGradeConfirmation(gradeToDelete);
                    }
                    else if (mode.getTag() instanceof ArrayList){
                        ArrayList<View> views = (ArrayList) mode.getTag();
                        ArrayList<Assignment> gradesToDelete = new ArrayList<>();
                        for(View view : views){
                            gradesToDelete.add((Assignment) view.getTag());
                        }
                        deleteGradesConfirmation(gradesToDelete);
                    }
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if(mode.getTag() instanceof View) {
                View view = (View) mode.getTag();
                view.setSelected(false);
            }
            else if(mode.getTag() instanceof ArrayList){
                ArrayList<View> views = (ArrayList) mode.getTag();
                for(View view : views){
                    view.setSelected(false);
                }
            }
            Window window = getActivity().getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.app_secondary_color));
            }
            mActionMode = null;
        }
    };

    @Override
    public void onConfirmationPositiveClick(ConfirmationDialogFragment dialog) {
        if(dialog.getExtra() instanceof Assignment){
            deleteGradedAssignment((Assignment) dialog.getExtra());
        }
        else if(dialog.getExtra() instanceof ArrayList){
            deleteGradedAssignments((ArrayList) dialog.getExtra());
        }



    }

    @Override
    public void onConfirmationNegativeClick(ConfirmationDialogFragment dialog) {

    }

    private void deleteGradedAssignment(Assignment gradeToDelete){

        //remove the assignment
        mGradedAssignments.remove(gradeToDelete);

        //notify the user
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_grade_deleted), Toast.LENGTH_SHORT);
        toast.show();

        //update the notebook
        ClassLoader.updateNotebooks(getActivity().getApplicationContext());

        //update the ui
        if(mListener!=null){
            mListener.onGradesChanged();
        }
    }

    private void deleteGradedAssignments(ArrayList<Assignment> gradesToDelete){
        //remove the assignment
        for(Assignment gradeToDelete : gradesToDelete) {
            mGradedAssignments.remove(gradeToDelete);
        }

        //notify the user
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_grades_deleted), Toast.LENGTH_SHORT);
        toast.show();

        //update the notebook
        ClassLoader.updateNotebooks(getActivity().getApplicationContext());

        //update the ui
        if(mListener!=null){
            mListener.onGradesChanged();
        }
    }

    private void editGradedAssignment(Assignment newAssignment, Assignment oldAssignment){
        newAssignment.setSortingMode(sortingMode);
        if(oldAssignment.equals(newAssignment)){
            //notify the user that an assignment was not changed
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_grade_no_change), Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            mGradedAssignments.remove(oldAssignment);
            mGradedAssignments.add(newAssignment);
            ClassLoader.updateNotebooks(getActivity().getApplicationContext());
            //notify the user that an assignment was edited
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_grade_edited), Toast.LENGTH_SHORT);
            toast.show();
        }

        if(mListener!=null){
            mListener.onGradesChanged();
        }
    }

    private void deleteGradeConfirmation(Assignment gradeToDelete){
        ConfirmationDialogFragment confirmation = ConfirmationDialogFragment.newInstance(getString(R.string.title_delete_grade_confirmation), gradeToDelete, ConfirmationDialogFragment.FRAGMENT);
        confirmation.setTargetFragment(this, 1);
        confirmation.show(getActivity().getSupportFragmentManager(), "confirmation");
    }

    private void deleteGradesConfirmation(ArrayList<Assignment> gradesToDelete){
        ConfirmationDialogFragment confirmation = ConfirmationDialogFragment.newInstance(getString(R.string.title_delete_grades_confirmation), gradesToDelete, ConfirmationDialogFragment.FRAGMENT);
        confirmation.setTargetFragment(this, 1);
        confirmation.show(getActivity().getSupportFragmentManager(), "confirmation");
    }

    private void openAssignment(View v){
        Assignment assignment = (Assignment) v.getTag();
        Intent intent = new Intent(getActivity().getApplicationContext(), AssignmentActivity.class);
        intent.putExtra("assignment", assignment);
        intent.putExtra("section", mSection);
        intent.putExtra("mode", AssignmentActivity.GRADED_ASSIGNMENT);

        startActivityForResult(intent, OPEN_ASSIGNMENT_REQUEST);
    }

//    private void editAssignment(Assignment newAssignment, Assignment oldAssignment){
//        newAssignment.setSortingMode(sortingMode);
//        if(oldAssignment.equals(newAssignment)){
//            //notify the user that an assignment was not changed
//            Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_assignment_no_change), Toast.LENGTH_SHORT);
//            toast.show();
//        }
//        else {
//            mGradedAssignments.remove(oldAssignment);
//            mGradedAssignments.add(newAssignment);
//            ClassLoader.updateNotebooks(getActivity().getApplicationContext());
//            //notify the user that an assignment was edited
//            Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_assignment_edited), Toast.LENGTH_SHORT);
//            toast.show();
//        }
//
//        onAssignmentsChanged();
//    }

    private double average(ArrayList<String> values){
        double total = (double) 0;
        int numValues = 0;
        for (String value : values){
            //ignore items with no value
            if(value.equals("")){
                //values.remove(value);
            }
            else {
                total += Double.parseDouble(value);
                numValues ++;
            }
        }

        return total/(double) numValues;
    }

    private void actionModeSetup(View v){
        if (!v.isSelected()) {
            v.setSelected(true);
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            if(mActionMode==null) {
                mActionMode = activity.startSupportActionMode(gradeActionModeCallback);
                mActionMode.setTag(v);

                //show the number of selected items
                mActionMode.setTitle("1");
                Window window = getActivity().getWindow();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.highlight_status_bar));
                }
            }
            else{
                //if more than one were selected
                if(mActionMode.getTag() instanceof ArrayList){
                    ArrayList<View> selectedViews = (ArrayList) mActionMode.getTag();
                    selectedViews.add(v);

                    //show the number of selected items
                    mActionMode.setTitle(Integer.toString(selectedViews.size()));
                }
                //if only one was selected
                else{
                    ArrayList<View> selectedViews = new ArrayList<View>();

                    //add the two selected views to a new ArrayList
                    selectedViews.add((View) mActionMode.getTag());
                    selectedViews.add(v);
                    mActionMode.setTag(selectedViews);

                    //show the number of selected items
                    mActionMode.setTitle(Integer.toString(selectedViews.size()));

                    //hide the menu items that don't apply to multiple views
                    MenuItem deleteAction = mActionMode.getMenu().findItem(R.id.action_edit);
                    deleteAction.setVisible(false);
                }
            }
            onActionModeChanged();

        } else {
            //if v is the only view selected, finish the actionmode
            if(mActionMode.getTag() instanceof View){
                mActionMode.finish();
            }
            //v is not the only selected view
            else if (mActionMode.getTag() instanceof ArrayList){
                //unselect the view
                ArrayList<View> selectedViews = (ArrayList) mActionMode.getTag();
                selectedViews.remove(v);
                v.setSelected(false);


                //show the number of selected items
                mActionMode.setTitle(Integer.toString(selectedViews.size()));

                if(selectedViews.size()==1){
                    mActionMode.setTag(selectedViews.get(0));

                    //show the menu items that only apply when one view is selected
                    MenuItem deleteAction = mActionMode.getMenu().findItem(R.id.action_edit);
                    deleteAction.setVisible(true);
                }

            }
            onActionModeChanged();

        }
    }

}

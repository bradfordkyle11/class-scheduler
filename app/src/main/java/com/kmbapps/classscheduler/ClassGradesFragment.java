package com.kmbapps.classscheduler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;


public class ClassGradesFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GRADES = "grades";
    private static final int ASSIGNMENT_CREATOR_REQUEST = 0;
    private static final int OPEN_ASSIGNMENT_REQUEST = 1;

    private int sortingMode;

    private ActionMode mActionMode;

    private ArrayList<Assignment> mGrades;

    private OnGradesFragmentInteractionListener mListener;


    public static ClassGradesFragment newInstance(ArrayList<Assignment> grades) {
        ClassGradesFragment fragment = new ClassGradesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_GRADES, grades);
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
            mGrades = (ArrayList<Assignment>) getArguments().getSerializable(ARG_GRADES);
        }
        if(mGrades==null){
            sortingMode = 0;
        }
        else if(mGrades.isEmpty()){
            sortingMode = 0;
        }
        else{
            sortingMode = mGrades.get(0).getSortingMode();
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_class_grades, container, false);

        //add the assignments to the view
        LinearLayout assignmentsList = (LinearLayout) v.findViewById(R.id.gradesList);

        Collections.sort(mGrades);

        //TODO: change the name of these to reflect grades instead of assignments
        for (Assignment assignment : mGrades){
            LinearLayout assignmentListItem = (LinearLayout) inflater.inflate(R.layout.list_item_graded_assignment, null);
            assignmentListItem.setTag(assignment);
            TextView assignmentName = (TextView) assignmentListItem.findViewById(R.id.assignmentName);
            assignmentName.setText(assignment.getName());
            TextView assignmentType = (TextView) assignmentListItem.findViewById(R.id.assignmentType);
            assignmentType.setText(assignment.getType());
            TextView assignmentGrade = (TextView) assignmentListItem.findViewById(R.id.assignmentGrade);
            assignmentGrade.setText(assignment.getGrade());

            //add action menu listener
            assignmentListItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!v.isSelected()) {
                        v.setSelected(true);
                        ActionBarActivity activity = (ActionBarActivity) v.getContext();
                        mActionMode = activity.startSupportActionMode(classSectionActionModeCallback);
                        mActionMode.setTag(v);
                        onActionModeChanged();
                        return true;
                    } else {
                        mActionMode.finish();
                        onActionModeChanged();
                        return true;
                    }

                }
            });

            assignmentListItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: change this to open grade
                    openAssignment(v);
                }
            });

            assignmentsList.addView(assignmentListItem);
        }

        return v;
    }

    public void onActionModeChanged() {
        if (mListener != null) {
            mListener.onActionModeChanged(mActionMode);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnGradesFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
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
        public void onActionModeChanged(ActionMode actionMode);
        public void onGradesChanged();
    }


    //action mode that is activated when long pressing an assignment
    private ActionMode.Callback classSectionActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu_assignment_list_item, menu);
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
//                    View v = (View) mode.getTag();
//
//                    Intent intent = new Intent(getActivity().getApplicationContext(), AddAssignmentActivity.class);
//                    intent.putExtra("editMode", true);
//                    intent.putExtra("assignment", (Assignment) v.getTag());
//                    startActivityForResult(intent, ASSIGNMENT_CREATOR_REQUEST);
//                    mode.finish();
                    return true;

                //TODO: confirmation dialog for deleting
                case R.id.action_delete:
                    View v = (View) mode.getTag();

                    //remove the assignment
                    Assignment removeThis = (Assignment) v.getTag();
                    mGrades.remove(removeThis);

                    //notify the user
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_grade_deleted), Toast.LENGTH_SHORT);
                    toast.show();

                    //update the notebook
                    ClassLoader.updateNotebooks(getActivity().getApplicationContext());

                    //update the ui
                    if(mListener!=null){
                        mListener.onGradesChanged();
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
            View view = (View) mode.getTag();
            view.setSelected(false);
            mActionMode = null;
        }
    };

    private void openAssignment(View v){
        Assignment assignment = (Assignment) v.getTag();
        Intent intent = new Intent(getActivity().getApplicationContext(), AssignmentActivity.class);
        intent.putExtra("assignment", assignment);

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
//            mGrades.remove(oldAssignment);
//            mGrades.add(newAssignment);
//            ClassLoader.updateNotebooks(getActivity().getApplicationContext());
//            //notify the user that an assignment was edited
//            Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_assignment_edited), Toast.LENGTH_SHORT);
//            toast.show();
//        }
//
//        onAssignmentsChanged();
//    }

}

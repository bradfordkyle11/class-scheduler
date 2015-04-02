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

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


//TODO: open an assignment activity when clicking on an assignment

public class ClassAssignmentsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ASSIGNMENTS = "assignments";
    private static final int NEW_ASSIGNMENT_REQUEST = 0;
    private static final int EDIT_ASSIGNMENT_REQUEST = 1;

    private int sortingMode;

    private ActionMode mActionMode;

    private ArrayList<Assignment> mAssignments;

    private OnAssignmentFragmentInteractionListener mListener;


    public static ClassAssignmentsFragment newInstance(ArrayList<Assignment> assignments) {
        ClassAssignmentsFragment fragment = new ClassAssignmentsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ASSIGNMENTS, assignments);
        fragment.setArguments(args);
        return fragment;
    }

    public ClassAssignmentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAssignments = (ArrayList<Assignment>) getArguments().getSerializable(ARG_ASSIGNMENTS);
        }
        if(mAssignments==null){
            sortingMode = 0;
        }
        else{
            sortingMode = mAssignments.get(0).getSortingMode();
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_class_assignments, container, false);

        //add the assignments to the view
        LinearLayout assignmentsList = (LinearLayout) v.findViewById(R.id.assignmentsList);

        Collections.sort(mAssignments);

        for (Assignment assignment : mAssignments){
            LinearLayout assignmentListItem = (LinearLayout) inflater.inflate(R.layout.list_item_upcoming_assignment, null);
            assignmentListItem.setTag(assignment);
            TextView assignmentName = (TextView) assignmentListItem.findViewById(R.id.assignmentName);
            assignmentName.setText(assignment.getName());
            TextView assignmentType = (TextView) assignmentListItem.findViewById(R.id.assignmentType);
            assignmentType.setText(assignment.getType());
            TextView assignmentDueDate = (TextView) assignmentListItem.findViewById(R.id.assignmentDueDate);
            Calendar dueDate = assignment.getDueDate();
            if(dueDate!=null) {
                assignmentDueDate.setText((dueDate.get(Calendar.MONTH)+1) + "/" + dueDate.get(Calendar.DAY_OF_MONTH));
            }

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

            assignmentsList.addView(assignmentListItem);
        }

        //set up the fab
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.add_assignment);
        fab.setOnClickListener(addAssignmentListener);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode==AddAssignmentActivity.NEW_ASSIGNMENT){
            Assignment newAssignment = (Assignment) data.getSerializableExtra("newAssignment");
            newAssignment.setSortingMode(sortingMode);
            mAssignments.add(newAssignment);

            //notify the user that new assignment was created
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_assignment_added), Toast.LENGTH_SHORT);
            toast.show();

            onAssignmentsChanged();
        }
        else if(resultCode==AddAssignmentActivity.EDITED_ASSIGNMENT){
            Assignment newAssignment = (Assignment) data.getSerializableExtra("newAssignment");
            newAssignment.setSortingMode(sortingMode);
            Assignment oldAssignment = (Assignment) data.getSerializableExtra("oldAssignment");

            if(oldAssignment.equals(newAssignment)){
                //notify the user that an assignment was not changed
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_assignment_no_change), Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                mAssignments.remove(oldAssignment);
                mAssignments.add(newAssignment);

                //notify the user that an assignment was edited
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_assignment_edited), Toast.LENGTH_SHORT);
                toast.show();
            }

            onAssignmentsChanged();
        }
    }

    public void onAssignmentsChanged() {
        if (mListener != null) {
            mListener.onAssignmentsChanged(mAssignments);
        }
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
            mListener = (OnAssignmentFragmentInteractionListener) activity;
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

    public interface OnAssignmentFragmentInteractionListener {
        public void onAssignmentsChanged(List<Assignment> assignments);
        public void onActionModeChanged(ActionMode actionMode);
    }

    View.OnClickListener addAssignmentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity().getApplicationContext(), AddAssignmentActivity.class);
           // intent.putExtra("Assignments", mAssignments);
            startActivityForResult(intent, NEW_ASSIGNMENT_REQUEST);
        }
    };

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
                    View v = (View) mode.getTag();

                    Intent intent = new Intent(getActivity().getApplicationContext(), AddAssignmentActivity.class);
                    intent.putExtra("editMode", true);
                    intent.putExtra("assignment", (Assignment) v.getTag());
                    startActivityForResult(intent, EDIT_ASSIGNMENT_REQUEST);
                    mode.finish();
                    return true;

                //TODO: confirmation dialog for deleting
                case R.id.action_delete:
                    v = (View) mode.getTag();

                    //remove the assignment
                    Assignment removeThis = (Assignment) v.getTag();
                    mAssignments.remove(removeThis);

                    //notify the user
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_assignment_deleted), Toast.LENGTH_SHORT);
                    toast.show();

                    //update the ui
                    onAssignmentsChanged();

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


}

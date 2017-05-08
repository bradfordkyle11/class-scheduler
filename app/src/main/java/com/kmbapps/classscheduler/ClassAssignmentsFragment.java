package com.kmbapps.classscheduler;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;




public class ClassAssignmentsFragment extends Fragment implements SetGradeDialogFragment.SetGradeDialogListener,
        ConfirmationDialogFragment.ConfirmationDialogListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ASSIGNMENTS = "assignments";
    private static final String ARG_SECTION = "section";
    private static final int ASSIGNMENT_CREATOR_REQUEST = 0;
    private static final int OPEN_ASSIGNMENT_REQUEST = 1;

    private int sortingMode;

    private ActionMode mActionMode;

    private ArrayList<Assignment> mAssignments;
    private Section mSection;

    private OnAssignmentFragmentInteractionListener mListener;
    private ShowDialog showDialog;

    //this needs to be tracked to uncheck if the user cancels completing an assignment
    private CheckBox completeAssignmentCheckbox;


    public static ClassAssignmentsFragment newInstance(ArrayList<Assignment> assignments, Section section) {
        ClassAssignmentsFragment fragment = new ClassAssignmentsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ASSIGNMENTS, assignments);
        args.putSerializable(ARG_SECTION, section);
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
            mSection = (Section) getArguments().getSerializable(ARG_SECTION);
        }
        if(mAssignments==null){
            sortingMode = 0;
        }
        else if(mAssignments.isEmpty()){
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

        //set up the fab
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.add_assignment);
        fab.setOnClickListener(addAssignmentListener);

        //add the assignments to the view
        LinearLayout assignmentsList = (LinearLayout) v.findViewById(R.id.assignmentsList);

        Collections.sort(mAssignments);
        int id = 1;
        for (Assignment assignment : mAssignments){
            LinearLayout assignmentListItem = (LinearLayout) inflater.inflate(R.layout.list_item_upcoming_assignment, null);
            assignmentListItem.setId(id);
            id++;
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

            //open action mode on long click
            assignmentListItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    actionModeSetup(v);
                    return true;

                }
            });

            //open assignments on click, if action mode is not active
            assignmentListItem.setOnClickListener(new View.OnClickListener() {
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

            //add listener on the checkbox and set its tag
            CheckBox checkBox = (CheckBox) assignmentListItem.findViewById(R.id.checkboxCompleteAssignment);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCompleteAssignmentDialog(v);
                }
            });
            checkBox.setTag(assignment);
            checkBox.setChecked(false);

            assignmentsList.addView(assignmentListItem);
        }

        //add a buffer to the bottom of the list
        LinearLayout buffer = (LinearLayout) inflater.inflate(R.layout.scroll_view_buffer, null);
        assignmentsList.addView(buffer);



        return v;
    }

    private void actionModeSetup(View v){
        if (!v.isSelected()) {
            v.setSelected(true);
            ActionBarActivity activity = (ActionBarActivity) v.getContext();
            if(mActionMode==null) {
                mActionMode = activity.startSupportActionMode(classSectionActionModeCallback);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case ASSIGNMENT_CREATOR_REQUEST:
                if(resultCode==AddAssignmentActivity.NEW_ASSIGNMENT){
                    Assignment newAssignment = (Assignment) data.getSerializableExtra("newAssignment");
                    newAssignment(newAssignment);
                }
                else if(resultCode==AddAssignmentActivity.EDITED_ASSIGNMENT){
                    Assignment newAssignment = (Assignment) data.getSerializableExtra("newAssignment");
                    Assignment oldAssignment = (Assignment) data.getSerializableExtra("oldAssignment");
                    editAssignment(newAssignment, oldAssignment);
                }
                else if(resultCode==AddAssignmentActivity.DELETE_ASSIGNMENT){
                    Assignment assignmentToDelete = (Assignment) data.getSerializableExtra("assignmentToDelete");
                    deleteAssignment(assignmentToDelete);
                }
                break;

            case OPEN_ASSIGNMENT_REQUEST:
                if (resultCode==AssignmentActivity.EDITED_ASSIGNMENT){
                    onAssignmentsChanged();
                }
                else if (resultCode==AssignmentActivity.DELETE_ASSIGNMENT){
                    Assignment assignmentToDelete = (Assignment) data.getSerializableExtra("assignmentToDelete");
                    deleteAssignment(assignmentToDelete);
                }
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

        try {
            showDialog = (ShowDialog) activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString()
                    + " must implement ShowDialog");
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
                SortByDialogFragment.newInstance(sortingMode).show(getActivity().getSupportFragmentManager(), "SortByDialogFragment");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public interface OnAssignmentFragmentInteractionListener {
        public void onAssignmentsChanged(List<Assignment> assignments);
        public void onActionModeChanged(ActionMode actionMode);
        public void onCompleteAssignment(Assignment assignment);
    }

    View.OnClickListener addAssignmentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity().getApplicationContext(), AddAssignmentActivity.class);
           // intent.putExtra("Assignments", mAssignments);
            startActivityForResult(intent, ASSIGNMENT_CREATOR_REQUEST);
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
                    intent.putExtra("mode", AddAssignmentActivity.EDIT_ASSIGNMENT);
                    intent.putExtra("assignment", (Assignment) v.getTag());
                    startActivityForResult(intent, ASSIGNMENT_CREATOR_REQUEST);
                    mode.finish();
                    return true;

                case R.id.action_delete:

                    if(mode.getTag() instanceof  View){
                        v = (View) mode.getTag();
                        Assignment removeThis = (Assignment) v.getTag();
                        deleteAssignmentConfirmation(removeThis);
                    }
                    else if(mode.getTag() instanceof ArrayList){
                        ArrayList<View> views = (ArrayList) mode.getTag();
                        ArrayList<Assignment> assignmentsToDelete = new ArrayList<>();
                        for(View view : views){
                            assignmentsToDelete.add((Assignment) view.getTag());
                        }
                        deleteAssignmentsConfirmation(assignmentsToDelete);
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
                window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.highlight_status_bar));
            }
            mActionMode = null;
        }
    };

    private void openAssignment(View v){
        Assignment assignment = (Assignment) v.getTag();
        Intent intent = new Intent(getActivity().getApplicationContext(), AssignmentActivity.class);
        intent.putExtra("assignment", assignment);
        intent.putExtra("section", mSection);

        startActivityForResult(intent, OPEN_ASSIGNMENT_REQUEST);
    }

    private void openCompleteAssignmentDialog(View v){
        completeAssignmentCheckbox = (CheckBox) v;
        Assignment completedAssignment = (Assignment) v.getTag();
        SetGradeDialogFragment dialog = SetGradeDialogFragment.newInstance("", completedAssignment);
        dialog.setTargetFragment(this, 1);
        showDialog.showDialog(dialog);

    }

    private void newAssignment(Assignment newAssignment){
        newAssignment.setSortingMode(sortingMode);
        mAssignments.add(newAssignment);
        ClassLoader.updateNotebooks(getActivity().getApplicationContext());
        //notify the user that new assignment was created
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_assignment_added), Toast.LENGTH_SHORT);
        toast.show();

        onAssignmentsChanged();
    }

    private void editAssignment(Assignment newAssignment, Assignment oldAssignment){
        newAssignment.setSortingMode(sortingMode);
        if(oldAssignment.equals(newAssignment)){
            //notify the user that an assignment was not changed
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_assignment_no_change), Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            mAssignments.remove(oldAssignment);
            mAssignments.add(newAssignment);
            ClassLoader.updateNotebooks(getActivity().getApplicationContext());
            //notify the user that an assignment was edited
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_assignment_edited), Toast.LENGTH_SHORT);
            toast.show();
        }

        onAssignmentsChanged();
    }

    public void deleteAssignmentConfirmation(Assignment assignmentToRemove){
        ConfirmationDialogFragment confirmation = ConfirmationDialogFragment.newInstance(getString(R.string.title_delete_assignment_confirmation), assignmentToRemove, ConfirmationDialogFragment.FRAGMENT);
        confirmation.setTargetFragment(this, 1);
        confirmation.show(getActivity().getSupportFragmentManager(), "confirmation");
    }

    public void deleteAssignmentsConfirmation(ArrayList<Assignment> assignmentsToRemove){
        ConfirmationDialogFragment confirmation = ConfirmationDialogFragment.newInstance(getString(R.string.title_delete_assignments_confirmation), assignmentsToRemove, ConfirmationDialogFragment.FRAGMENT);
        confirmation.setTargetFragment(this, 1);
        confirmation.show(getActivity().getSupportFragmentManager(), "confirmation");
    }

    public void deleteAssignment(Assignment assignmentToRemove){
        mAssignments.remove(assignmentToRemove);

        //notify the user
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_assignment_deleted), Toast.LENGTH_SHORT);
        toast.show();

        //update the notebook
        ClassLoader.updateNotebooks(getActivity().getApplicationContext());

        //update the ui
        onAssignmentsChanged();
    }

    public void deleteAssignments(ArrayList<Assignment> assignmentsToRemove){
        for(Assignment assignmentToRemove : assignmentsToRemove) {
            mAssignments.remove(assignmentToRemove);
        }

        //notify the user
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_assignments_deleted), Toast.LENGTH_SHORT);
        toast.show();

        //update the notebook
        ClassLoader.updateNotebooks(getActivity().getApplicationContext());

        //update the ui
        onAssignmentsChanged();
    }

    //remove assignment confirmed
    @Override
    public void onConfirmationPositiveClick(ConfirmationDialogFragment dialog) {
        if(dialog.getExtra() instanceof Assignment) {
            Assignment assignmentToRemove = (Assignment) dialog.getExtra();
            deleteAssignment(assignmentToRemove);
        }
        else if(dialog.getExtra() instanceof ArrayList){
            ArrayList<Assignment> assignmentsToRemove = (ArrayList) dialog.getExtra();
            deleteAssignments(assignmentsToRemove);
        }

    }

    //remove assignment canceled
    @Override
    public void onConfirmationNegativeClick(ConfirmationDialogFragment dialog) {

    }

    @Override
    public void onSetGradePositiveClick(String grade, Assignment assignment) {
        mAssignments.remove(assignment);
        assignment.setCompletionDate(Calendar.getInstance());
        assignment.setGrade(grade);
        assignment.setSortingMode(Assignment.COMPARE_TYPE);
        completeAssignmentCheckbox.setChecked(false);
        if(mListener!=null){
            mListener.onCompleteAssignment(assignment);
        }
    }

    @Override
    public void onSetGradeNegativeClick() {
        completeAssignmentCheckbox.setChecked(false);
    }
}

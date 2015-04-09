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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMyClassesFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyClassesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyClassesFragment extends Fragment implements ConfirmationDialogFragment.ConfirmationDialogListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SCHEDULE = "schedule";

    private Schedule mSchedule;
    private Notebook mNotebook;

    private ActionMode mActionMode;

    private OnMyClassesFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyClassesFragment.
     */
    public static MyClassesFragment newInstance(Schedule schedule) {
        MyClassesFragment fragment = new MyClassesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SCHEDULE, schedule);
        fragment.setArguments(args);
        return fragment;
    }

    public MyClassesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSchedule = (Schedule) getArguments().getSerializable(ARG_SCHEDULE);
        }

        mNotebook = ClassLoader.loadNotebooks(getActivity().getApplicationContext()).get(mSchedule);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_classes, container, false);

        LinearLayout classes = (LinearLayout) view.findViewById(R.id.classes);

        for (Section section : mSchedule.getSections()) {
            View v = inflater.inflate(R.layout.list_item_my_classes, null);
            TextView classDetails = (TextView) v.findViewById(R.id.classTextView);
            classDetails.setText(section.getContainingClass().getDepartment() + " " + section.getContainingClass().getNumber() + " " + section.getContainingClass().getName());
            v.setTag(section);
            v.setOnClickListener(classSelectedListener);
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    actionModeSetup(v);
                    return true;
                }
            });
            classes.addView(v);
        }
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMyClassesFragmentInteractionListener) activity;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMyClassesFragmentInteractionListener {
        public void onClassesDropped();
    }

    public View.OnClickListener classSelectedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mActionMode==null) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MyClassActivity.class);
                intent.putExtra("MySection", (Section) v.getTag());
                intent.putExtra("schedule", mSchedule);
                startActivity(intent);
            }
            else{
                actionModeSetup(v);
            }
        }
    };

    //action mode that is activated when long pressing an assignment
    private ActionMode.Callback classActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu_my_classes_list_item, menu);
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
//                    intent.putExtra("mode", AddAssignmentActivity.EDIT_GRADED_ASSIGNMENT);
//                    intent.putExtra("assignment", (Assignment) v.getTag());
//                    startActivityForResult(intent, ASSIGNMENT_CREATOR_REQUEST);
//                    mode.finish();
//                    return true;

                case R.id.action_delete:

                        ArrayList<View> views = (ArrayList) mode.getTag();
                        ArrayList<Section> classesToDrop = new ArrayList<>();
                        for(View view : views){
                            classesToDrop.add((Section) view.getTag());
                        }
                        dropClassesConfirmation(classesToDrop);
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {

            ArrayList<View> views = (ArrayList) mode.getTag();
            for (View view : views) {
                view.setSelected(false);
            }

            mActionMode = null;
        }
    };

    @Override
    public void onConfirmationPositiveClick(ConfirmationDialogFragment dialog) {
        dropClasses((ArrayList) dialog.getExtra());
    }

    @Override
    public void onConfirmationNegativeClick(ConfirmationDialogFragment dialog) {

    }

    private void dropClasses(ArrayList<Section> classesToDrop){
        //remove the notebook associated with the current schedule
        //this is because the schedule will change and the notebook will no longer be properly mapped to it
        ClassLoader.removeNotebook(getActivity().getApplicationContext(), mSchedule);

        for(Section classToDrop : classesToDrop){
            mSchedule.dropClass(classToDrop);
            mNotebook.erase(classToDrop);
        }
        ClassLoader.updateNotebook(getActivity().getApplicationContext(), mSchedule, mNotebook);

        ClassLoader.setCurrentSchedule(getActivity().getApplicationContext(), mSchedule);
        ClassLoader.updateNotebooks(getActivity().getApplicationContext());

        if(classesToDrop.size()==1) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_class_dropped), Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_classes_dropped), Toast.LENGTH_SHORT);
            toast.show();
        }

        if(mListener!=null){
            mListener.onClassesDropped();
        }
    }

    private void dropClassesConfirmation(ArrayList<Section> classesToDrop){
        if(classesToDrop.size()==1){
            ConfirmationDialogFragment confirmation = ConfirmationDialogFragment.newInstance(getString(R.string.title_drop_class_confirmation), classesToDrop, ConfirmationDialogFragment.FRAGMENT);
            confirmation.setTargetFragment(this, 1);
            confirmation.show(getActivity().getSupportFragmentManager(), "confirmation");
        }
        else {
            ConfirmationDialogFragment confirmation = ConfirmationDialogFragment.newInstance(getString(R.string.title_drop_classes_confirmation), classesToDrop, ConfirmationDialogFragment.FRAGMENT);
            confirmation.setTargetFragment(this, 1);
            confirmation.show(getActivity().getSupportFragmentManager(), "confirmation");
        }
    }

    private void actionModeSetup(View v){
        if (!v.isSelected()) {
            v.setSelected(true);
            ActionBarActivity activity = (ActionBarActivity) v.getContext();

            //selectin
            if(mActionMode==null) {
                mActionMode = activity.startSupportActionMode(classActionModeCallback);
                ArrayList<View> selectedViews = new ArrayList<>();
                selectedViews.add(v);
                mActionMode.setTag(selectedViews);

                //show the number of selected items
                mActionMode.setTitle("1");
            }
            else{
                //at least one already selected
                ArrayList<View> selectedViews = (ArrayList) mActionMode.getTag();

                //hide views if going from 1 selected to more
                if(selectedViews.size()==1){
                    MenuItem editAction = mActionMode.getMenu().findItem(R.id.action_edit);
                    editAction.setVisible(false);
                }

                selectedViews.add(v);

                //show the number of selected items
                mActionMode.setTitle(Integer.toString(selectedViews.size()));
            }

        //deselecting
        } else {
            ArrayList<View> selectedViews = (ArrayList) mActionMode.getTag();
            //if v is the only view selected, finish the actionmode
            if(selectedViews.size()==1){
                mActionMode.finish();
            }
            //v is not the only selected view
            else {
                //unselect the view
                selectedViews.remove(v);
                v.setSelected(false);


                //show the number of selected items
                mActionMode.setTitle(Integer.toString(selectedViews.size()));

                if(selectedViews.size()==1){
                    //show the menu items that only apply when one view is selected
                    MenuItem deleteAction = mActionMode.getMenu().findItem(R.id.action_edit);
                    deleteAction.setVisible(true);
                }

            }

        }
    }

}

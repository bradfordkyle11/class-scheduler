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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DesiredClassesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DesiredClassesFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */

//TODO: OnClick for classes and sections takes you to the edit page
public class DesiredClassesFragment extends Fragment implements ConfirmationDialogFragment.ConfirmationDialogListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int MENU_EDIT = 0;
    private static final int MENU_DELETE = 1;

    private ActionMode mActionMode;

    LinearLayout classesAndSections;
    List<Class> desiredClasses;

    private String mParam1;
    private String mParam2;

    //used to remove a view when returning from confirmation dialog
    private View selectedView;

    private OnFragmentInteractionListener mListener;
    private List<Class> classes;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DesiredClassesFragment.
     */
    public static DesiredClassesFragment newInstance(String param1, String param2) {
        DesiredClassesFragment fragment = new DesiredClassesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public DesiredClassesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        classes = ClassLoader.loadClasses(getActivity());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_desired_classes, container, false);

        //show "you have no classes text"
        TextView textview = (TextView) view.findViewById(R.id.no_classes);
        if(classes==null){
            textview.setVisibility(View.VISIBLE);
        }
        else if(classes.isEmpty()) {
            textview.setVisibility(View.VISIBLE);
        }
        else{
            textview.setVisibility(View.GONE);
            desiredClasses = ClassLoader.loadClasses(getActivity());

            //layout that will contain list of classes and sections
            classesAndSections = (LinearLayout) view.findViewById(R.id.classes_and_sections);

            if (desiredClasses == null) {
                desiredClasses = new ArrayList<Class>();
            }
            for(int i = 0; i < desiredClasses.size(); i++){

                //layout for an individual class and its sections
                View v = inflater.inflate(R.layout.class_and_section_layout, null);
                TextView classInfo = (TextView) v.findViewById(R.id.classInfo);
                classInfo.setText(desiredClasses.get(i).getDepartment() + " " + desiredClasses.get(i).getNumber() + "\n" + desiredClasses.get(i).getName());

                //add tags to the add and edit buttons on the class view
                ImageButton addSection = (ImageButton) v.findViewById(R.id.add_class_section);
                addSection.setFocusable(false);
                addSection.setTag(desiredClasses.get(i));

                //add the context menu to the class
                LinearLayout course = (LinearLayout) v.findViewById(R.id.course);
                course.setTag(desiredClasses.get(i));
                course.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(!v.isSelected()){
                            v.findViewById(R.id.add_class_section).setSelected(false);
                            v.setSelected(true);
                            v.findViewById(R.id.add_class_section).setSelected(false);
                            ActionBarActivity activity = (ActionBarActivity) v.getContext();
                            mActionMode = activity.startSupportActionMode(classActionModeCallback);
                            mActionMode.setTag(v);
                            onActionModeChanged();
                            return true;

                        }
                        else{
                            mActionMode.finish();
                            onActionModeChanged();
                            return true;
                        }

                    }
                });

                //add each section under the class
                ArrayList<Section> sections = (ArrayList) desiredClasses.get(i).getSections();
                LinearLayout sectionLayout = (LinearLayout) v.findViewById(R.id.sections);
                for (int j = 0; j < sections.size(); j++){
                    View s = inflater.inflate(R.layout.list_item, null);
                    s.setTag(sections.get(j));
                    TextView sectionInfo = (TextView) s.findViewById(R.id.schedule);
                    sectionInfo.setText(sections.get(j).toString());
                    sections.get(j).setContainingClass(desiredClasses.get(i));

                    s.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if(!v.isSelected()){
                                v.setSelected(true);
                                ActionBarActivity activity = (ActionBarActivity) v.getContext();
                                mActionMode = activity.startSupportActionMode(classSectionActionModeCallback);
                                mActionMode.setTag(v);
                                onActionModeChanged();
                                return true;
                            }
                            else{
                                mActionMode.finish();
                                onActionModeChanged();
                                return true;
                            }

                        }
                    });
                    sectionLayout.addView(s);
                }

                classesAndSections.addView(v);
            }

            //add a buffer to the end of the scrollview so you can scroll past the end
            View buffer = inflater.inflate(R.layout.scroll_view_buffer, null);
            classesAndSections.addView(buffer);

        }

        //add listener to floating action button and bring it to the front
        View fab = view.findViewById(R.id.add_class);
        fab.setOnClickListener(addClassListener);
        fab.bringToFront();

//        registerForContextMenu(expListView);

        return view;
    }

    /*
     * Preparing the list data
     */
    private void prepareClassList() {

    }

    private void onActionModeChanged(){
        if(mListener!=null){
            mListener.onActionModeChanged(mActionMode);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnFragmentInteractionListener) activity;
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        public void onAddClassClick(View view);

        public void onSectionDeleted();

        public void onActionModeChanged(ActionMode actionMode);
    }



    View.OnClickListener addClassListener = new View.OnClickListener() {
        public void onClick(View view) {
            addClass(view);
        }
    };

    private void addClass(View view) {
        if(mListener!=null){
            getFragmentManager().beginTransaction()
                    .addToBackStack("desired classes")
                    .commit();

            //tell home layout to go to add class page
            mListener.onAddClassClick(view);
        }
    }

    private ActionMode.Callback classActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu_class, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // class menu
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    View v = (View) mode.getTag();
                    Intent intent = new Intent(getActivity().getApplicationContext(), EditClassActivity.class);
                    intent.putExtra("MyClass", (Class) v.getTag());
                    startActivity(intent);
                    return true;
                case R.id.action_delete:
                    v = (View) mode.getTag();
                    selectedView = v;
                    Class classToDelete = (Class) v.getTag();
                    ConfirmationDialogFragment confirmation = ConfirmationDialogFragment.newInstance(getString(R.string.title_delete_class_confirmation), classToDelete, ConfirmationDialogFragment.FRAGMENT);
                    confirmation.setTargetFragment(DesiredClassesFragment.this, 1);
                    confirmation.show(getActivity().getSupportFragmentManager(), "deleteClass");

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

    private ActionMode.Callback classSectionActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu_class_section, menu);
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
                    Intent intent = new Intent(getActivity(), AddClassSectionActivity.class);

                    intent.putExtra("newClass", false);
                    intent.putExtra("mSection", (Section) v.getTag());
                    intent.putExtra("MyClass", ((Section)v.getTag()).getContainingClass());
                    startActivity(intent);
                    return true;
                case R.id.action_delete:
                    v = (View) mode.getTag();
                    selectedView = v;
                    Section sectionToDelete = (Section) v.getTag();
                    ConfirmationDialogFragment confirmation = ConfirmationDialogFragment.newInstance(getString(R.string.title_delete_section_confirmation), sectionToDelete, ConfirmationDialogFragment.FRAGMENT);
                    confirmation.setTargetFragment(DesiredClassesFragment.this, 1);
                    confirmation.show(getActivity().getSupportFragmentManager(), "deleteSection");

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

    @Override
    public void onConfirmationPositiveClick(ConfirmationDialogFragment dialog) {
        switch(dialog.getTag()){
            case "deleteClass":
                Class classToRemove = (Class) dialog.getExtra();
                //remove the class
                ClassLoader.removeClass(getActivity(), classToRemove);

                //remove the view
                ((ViewGroup)selectedView.getParent().getParent()).removeView((View) selectedView.getParent());
                mListener.onSectionDeleted();

                Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_class_deleted), Toast.LENGTH_SHORT);
                toast.show();
                break;
            case "deleteSection":
                //remove the section
                Section sectionToRemove = (Section) dialog.getExtra();
                Class c = sectionToRemove.getContainingClass();
                ClassLoader.removeSection(getActivity(), sectionToRemove, c);

                //remove the view
                ((ViewGroup)selectedView.getParent()).removeView(selectedView);
                mListener.onSectionDeleted();

                toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_section_deleted), Toast.LENGTH_SHORT);
                toast.show();
                break;

        }
    }

    @Override
    public void onConfirmationNegativeClick(ConfirmationDialogFragment dialog) {

    }
}

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

public class DesiredClassesFragment extends Fragment implements ConfirmationDialogFragment.ConfirmationDialogListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int MENU_EDIT = 0;
    private static final int MENU_DELETE = 1;


    private static final int CLASS_ACTION_MODE = 0;
    private static final int SECTION_ACTION_MODE = 1;

    private ActionMode mActionMode;
    private int currentActionModeType = -1;

    LinearLayout classesAndSections;
    List<Class> desiredClasses;

    //used to remove a view when returning from confirmation dialog
    private View selectedView;
    private ArrayList<View> selectedViews;

    private OnFragmentInteractionListener mListener;
    private List<Class> classes;
    private boolean loading;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DesiredClassesFragment.
     */
    public static DesiredClassesFragment newInstance(Boolean loading) {
        DesiredClassesFragment fragment = new DesiredClassesFragment();
        Bundle args = new Bundle();
        args.putBoolean("loading", loading);
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
            loading = getArguments().getBoolean("loading");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        classes = ClassLoader.loadClasses(getActivity());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_desired_classes, container, false);
        View progressIndicator = view.findViewById(R.id.progress_loader);
        progressIndicator.bringToFront();
        if (loading){
           progressIndicator.setVisibility(View.VISIBLE);
        }
        else{
            progressIndicator.setVisibility(View.GONE);
        }
        //show/hide "you have no classes text"
        TextView textview = (TextView) view.findViewById(R.id.no_classes);
        if(classes==null){
            textview.setVisibility(View.VISIBLE);
            textview.setText(getString(R.string.text_no_classes));
        }
        else if(classes.isEmpty()) {
            textview.setVisibility(View.VISIBLE);
            textview.setText(getString(R.string.text_no_classes));
        }
        else{
            if (classes.size() == 1 && classes.get(0).getSections().size() == 0){
                    textview.setVisibility(View.VISIBLE);
                    textview.setText(getString(R.string.text_no_sections));
            }
            else {
                textview.setVisibility(View.GONE);
            }
            desiredClasses = new ArrayList<Class>();
            desiredClasses.addAll(classes);

            //layout that will contain list of classes and sections
            classesAndSections = (LinearLayout) view.findViewById(R.id.classes_and_sections);

            if (desiredClasses == null) {
                desiredClasses = new ArrayList<Class>();
            }
            for(int i = 0; i < desiredClasses.size(); i++){

                //layout for an individual class and its sections
                View v = inflater.inflate(R.layout.class_and_section_layout, null);
                v.findViewById(R.id.classColorBar).setBackgroundColor(desiredClasses.get(i).getColor());
                TextView classInfo = (TextView) v.findViewById(R.id.classInfo);
                classInfo.setText(desiredClasses.get(i).getDepartment() + " " + desiredClasses.get(i).getNumber() + "\n" + desiredClasses.get(i).getName());

                //add tags to the add and edit buttons on the class view
                ImageButton addSection = (ImageButton) v.findViewById(R.id.add_class_section);
                addSection.setFocusable(false);
                addSection.setTag(desiredClasses.get(i));

                //add the context menu to the class
                LinearLayout course = (LinearLayout) v.findViewById(R.id.course);
                course.setTag(desiredClasses.get(i));

                //open the action mode on long click
                course.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        actionModeSetup(v, CLASS_ACTION_MODE);
                        return true;

                    }
                });

                course.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //select view
                        if(mActionMode!=null){
                            actionModeSetup(v, CLASS_ACTION_MODE);
                        }

                        //open class editing mode
                        else{
                            Intent intent = new Intent(getActivity().getApplicationContext(), EditClassActivity.class);
                            intent.putExtra("MyClass", (Class) v.getTag());
                            startActivity(intent);
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

                    //open action mode on long click
                    s.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            actionModeSetup(v, SECTION_ACTION_MODE);
                            return true;
                        }
                    });


                    s.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //select view
                            if(mActionMode!=null){
                                actionModeSetup(v, SECTION_ACTION_MODE);
                            }

                            //open section editing mode
                            else{
                                Intent intent = new Intent(getActivity(), AddClassSectionActivity.class);

                                intent.putExtra("newClass", false);
                                intent.putExtra("mSection", (Section) v.getTag());
                                intent.putExtra("MyClass", ((Section)v.getTag()).getContainingClass());
                                startActivity(intent);
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

    public void actionModeSetup(View v, int type){
        boolean newType = currentActionModeType!=type;
        currentActionModeType = type;

        switch(type){
            case CLASS_ACTION_MODE:
                //selecting
                if(!v.isSelected()){
                    //select the view
                    v.setSelected(true);
                    v.findViewById(R.id.add_class_section).setSelected(false);

                    //nothing was selected before, or new type of selection was made
                    if(mActionMode==null||newType){

                        //if selecting a new type, finish the action mode
                        if(mActionMode!=null){
                            mActionMode.finish();
                        }

                        //start new action mode
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        mActionMode = activity.startSupportActionMode(classActionModeCallback);
                        if (mActionMode != null) {
                            mActionMode.setTag(v);
                            mActionMode.setTitle("1");
                        }
                        onActionModeChanged();
                        Window window = getActivity().getWindow();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.highlight_status_bar));
                        }
                    }
                    //something was already selected and is the same type
                    else{
                        //hide the actions that don't apply to more than one view
                        MenuItem editAction = mActionMode.getMenu().findItem(R.id.action_edit);
                        editAction.setVisible(false);

                        //only one was selected
                        if(mActionMode.getTag() instanceof View){
                            ArrayList<View> selectedViews = new ArrayList<>();
                            selectedViews.add(v);
                            selectedViews.add((View) mActionMode.getTag());
                            mActionMode.setTag(selectedViews);
                            mActionMode.setTitle(Integer.toString(selectedViews.size()));
                        }
                        //more than one selected
                        else if(mActionMode.getTag() instanceof ArrayList){
                            ArrayList<View> selectedViews = (ArrayList) mActionMode.getTag();
                            selectedViews.add(v);
                            mActionMode.setTitle(Integer.toString(selectedViews.size()));
                        }
                    }
                }

                //deselecting
                else{
                    //v was the only thing selected, so finish the action mode
                    if(mActionMode.getTag() instanceof View) {
                        mActionMode.finish();
                        onActionModeChanged();
                    }
                    else{
                        //deselect the view
                        ArrayList<View> selectedViews = (ArrayList) mActionMode.getTag();
                        selectedViews.remove(v);
                        v.setSelected(false);
                        mActionMode.setTitle(Integer.toString(selectedViews.size()));

                        if(selectedViews.size()==1){
                            mActionMode.setTag(selectedViews.get(0));

                            //show the actions that only apply to one view
                            MenuItem editAction = mActionMode.getMenu().findItem(R.id.action_edit);
                            editAction.setVisible(true);
                        }
                    }
                }
                break;
            case SECTION_ACTION_MODE:
                //selecting
                if(!v.isSelected()){
                    //select the view
                    v.setSelected(true);

                    //nothing was selected before, or new type of selection was made
                    if(mActionMode==null||newType){

                        //if selecting a new type, finish the action mode
                        if(mActionMode!=null){
                            mActionMode.finish();
                        }

                        //start new action mode
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        mActionMode = activity.startSupportActionMode(classSectionActionModeCallback);
                        if (mActionMode != null) {
                            mActionMode.setTag(v);
                            mActionMode.setTitle("1");
                        }
                        onActionModeChanged();
                        Window window = getActivity().getWindow();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.highlight_status_bar));
                        }

                    }
                    //something was already selected and is the same type
                    else{
                        //hide the actions that don't apply to more than one view
                        MenuItem editAction = mActionMode.getMenu().findItem(R.id.action_edit);
                        editAction.setVisible(false);

                        //only one was selected
                        if(mActionMode.getTag() instanceof View){
                            ArrayList<View> selectedViews = new ArrayList<>();
                            selectedViews.add(v);
                            selectedViews.add((View) mActionMode.getTag());
                            mActionMode.setTag(selectedViews);
                            mActionMode.setTitle(Integer.toString(selectedViews.size()));
                        }
                        //more than one selected
                        else if(mActionMode.getTag() instanceof ArrayList){
                            ArrayList<View> selectedViews = (ArrayList) mActionMode.getTag();
                            selectedViews.add(v);
                            mActionMode.setTitle(Integer.toString(selectedViews.size()));
                        }
                    }
                }

                //deselecting
                else{
                    //v was the only thing selected, so finish the action mode
                    if(mActionMode.getTag() instanceof View) {
                        mActionMode.finish();
                        onActionModeChanged();
                    }
                    else{
                        //deselect the view
                        ArrayList<View> selectedViews = (ArrayList) mActionMode.getTag();
                        selectedViews.remove(v);
                        v.setSelected(false);
                        mActionMode.setTitle(Integer.toString(selectedViews.size()));

                        if(selectedViews.size()==1){
                            mActionMode.setTag(selectedViews.get(0));

                            //show the actions that only apply to one view
                            MenuItem editAction = mActionMode.getMenu().findItem(R.id.action_edit);
                            editAction.setVisible(true);
                        }
                    }
                }
                break;
        }
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
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = null;
        if (context instanceof Activity){
            activity = (Activity) context;
        }
        // Verify that the host activity implements the callback interface
        if (activity != null) {

            try {
                mListener = (OnFragmentInteractionListener) activity;
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

        void onAddClassClick(View view);

        void onSectionDeleted();

        void onActionModeChanged(ActionMode actionMode);
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
                    intent.putExtra("where", ClassLoader.DESIRED_CLASSES);
                    startActivity(intent);
                    return true;
                case R.id.action_delete:
                    if(mode.getTag() instanceof View) {
                        v = (View) mode.getTag();
                        selectedView = v;
                        Class classToDelete = (Class) v.getTag();
                        ConfirmationDialogFragment confirmation = ConfirmationDialogFragment.newInstance(getString(R.string.title_delete_class_confirmation), classToDelete, ConfirmationDialogFragment.FRAGMENT);
                        confirmation.setTargetFragment(DesiredClassesFragment.this, 1);
                        confirmation.show(getActivity().getSupportFragmentManager(), "deleteClass");
                    }
                    else if(mode.getTag() instanceof ArrayList){
                        ArrayList<View> views = (ArrayList) mode.getTag();
                        selectedViews = views;

                        ArrayList<Class> classesToDelete = new ArrayList<>();
                        for(View view : views){
                            classesToDelete.add((Class) view.getTag());
                        }

                        ConfirmationDialogFragment confirmation = ConfirmationDialogFragment.newInstance(getString(R.string.title_delete_classes_confirmation), classesToDelete, ConfirmationDialogFragment.FRAGMENT);
                        confirmation.setTargetFragment(DesiredClassesFragment.this, 1);
                        confirmation.show(getActivity().getSupportFragmentManager(), "deleteClass");
                    }

                    mode.finish();
                    return true;
                default:
                    Window window = getActivity().getWindow();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.app_secondary_color));
                    }
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
            else if (mode.getTag() instanceof ArrayList){
                ArrayList<View> selectedViews = (ArrayList) mode.getTag();
                for(View view : selectedViews){
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
                    intent.putExtra("where", ClassLoader.DESIRED_CLASSES);
                    startActivity(intent);
                    return true;
                case R.id.action_delete:


                    if(mode.getTag() instanceof View) {
                        v = (View) mode.getTag();
                        selectedView = v;
                        Section sectionToDelete = (Section) v.getTag();
                        ConfirmationDialogFragment confirmation = ConfirmationDialogFragment.newInstance(getString(R.string.title_delete_section_confirmation), sectionToDelete, ConfirmationDialogFragment.FRAGMENT);
                        confirmation.setTargetFragment(DesiredClassesFragment.this, 1);
                        confirmation.show(getActivity().getSupportFragmentManager(), "deleteSection");
                    }
                    else if(mode.getTag() instanceof ArrayList){
                        ArrayList<View> views = (ArrayList) mode.getTag();
                        selectedViews = views;

                        ArrayList<Section> sectionsToDelete = new ArrayList<>();
                        for(View view : views){
                            sectionsToDelete.add((Section) view.getTag());
                        }

                        ConfirmationDialogFragment confirmation = ConfirmationDialogFragment.newInstance(getString(R.string.title_delete_sections_confirmation), sectionsToDelete, ConfirmationDialogFragment.FRAGMENT);
                        confirmation.setTargetFragment(DesiredClassesFragment.this, 1);
                        confirmation.show(getActivity().getSupportFragmentManager(), "deleteSection");
                    }

                    mode.finish();
                    return true;
                default:
                    Window window = getActivity().getWindow();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.app_secondary_color));
                    }
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
            else if (mode.getTag() instanceof ArrayList){
                ArrayList<View> selectedViews = (ArrayList) mode.getTag();
                for(View view : selectedViews){
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
            mActionMode = null;
        }
    };

    @Override
    public void onConfirmationPositiveClick(ConfirmationDialogFragment dialog) {
        switch(dialog.getTag()){
            case "deleteClass":
                if(dialog.getExtra() instanceof Class) {
                    Class classToRemove = (Class) dialog.getExtra();
                    //remove the class
                    ClassLoader.removeClass(getActivity(), classToRemove);

                    //remove the view
                    ((ViewGroup) selectedView.getParent().getParent()).removeView((View) selectedView.getParent());
                    mListener.onSectionDeleted();

                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_class_deleted), Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if(dialog.getExtra() instanceof ArrayList){
                    ArrayList<Class> classesToRemove = (ArrayList) dialog.getExtra();

                    //remove the classes
                    ClassLoader.removeClasses(getActivity().getApplicationContext(), classesToRemove);

                    //remove the views
                    for(View view : selectedViews){
                        ((ViewGroup) view.getParent().getParent()).removeView((View) view.getParent());
                    }
                    mListener.onSectionDeleted();

                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_classes_deleted), Toast.LENGTH_SHORT);
                    toast.show();

                }
                break;
            case "deleteSection":
                if(dialog.getExtra() instanceof Section) {
                    //remove the section
                    Section sectionToRemove = (Section) dialog.getExtra();
                    ClassLoader.removeSection(getActivity(), sectionToRemove, ClassLoader.DESIRED_CLASSES);

                    //remove the view
                    ((ViewGroup) selectedView.getParent()).removeView(selectedView);
                    mListener.onSectionDeleted();

                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_section_deleted), Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if (dialog.getExtra() instanceof ArrayList) {
                    ArrayList<Section> sectionsToRemove = (ArrayList) dialog.getExtra();

                    //remove the classes
                    ClassLoader.removeSections(getActivity().getApplicationContext(), sectionsToRemove, ClassLoader.DESIRED_CLASSES);

                    //remove the views
                    for(View view : selectedViews){
                        ((ViewGroup) view.getParent()).removeView(view);
                    }
                    mListener.onSectionDeleted();

                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_sections_deleted), Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;

        }
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        View v = getView();
        if (loading && v != null) {
            getView().findViewById(R.id.progress_loader).setVisibility(View.VISIBLE);
        }
        else if (v != null) {
            getView().findViewById(R.id.progress_loader).setVisibility(View.GONE);
        }
    }

    @Override
    public void onConfirmationNegativeClick(ConfirmationDialogFragment dialog) {

    }
}

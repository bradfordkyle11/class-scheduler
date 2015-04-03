package com.kmbapps.classscheduler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MyClassActivity extends ActionBarActivity implements ClassAssignmentsFragment.OnAssignmentFragmentInteractionListener,
        SortByDialogFragment.SortByDialogListener, ClassGradesFragment.OnGradesFragmentInteractionListener{

    private final static int DETAILS = 0;
    private final static int ASSIGNMENTS = 1;
    private final static int GRADES = 2;

    private static final String CURRENT_PAGE = "currentPage";
    private static final String CURRENT_SECTION = "currentSection";
    private static final String CURRENT_NOTEBOOK = "currentNotebook";
    private static final String CURRENT_SCHEDULE = "currentSchedule";

    private Section mSection;
    private Notebook mNotebook;
    private Schedule mSchedule;

    private MyClassPagerAdapter myClassPagerAdapter;
    private ViewPager mViewPager;
    private int mCurrentPage = 1;

    private ActionMode currentActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_class);

        Intent intent = getIntent();
        mSection = (Section) intent.getSerializableExtra("MySection");
        mSchedule = (Schedule) intent.getSerializableExtra("schedule");
        mNotebook = ClassLoader.loadNotebooks(this).get(mSchedule);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mSection.getContainingClass().getName());

        if(savedInstanceState!=null&&mSection==null){
            mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE);
            mSection = (Section) savedInstanceState.getSerializable(CURRENT_SECTION);
            mNotebook = (Notebook) savedInstanceState.getSerializable(CURRENT_NOTEBOOK);
        }

        myClassPagerAdapter =
                new MyClassPagerAdapter(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.classPager);
        mViewPager.setAdapter(myClassPagerAdapter);
        mViewPager.setOnPageChangeListener(myOnPageChangeListener);
        mViewPager.setCurrentItem(mCurrentPage);


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){

        savedInstanceState.putInt(CURRENT_PAGE, mViewPager.getCurrentItem());
        savedInstanceState.putSerializable(CURRENT_SECTION, mSection);
        savedInstanceState.putSerializable(CURRENT_NOTEBOOK, mNotebook);
        super.onSaveInstanceState(savedInstanceState);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class MyClassPagerAdapter extends FragmentStatePagerAdapter {


        public MyClassPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int i) {

            switch(i) {
                case DETAILS:
                    return ClassDetailsFragment.newInstance(mSection);
                case ASSIGNMENTS:
                    return ClassAssignmentsFragment.newInstance((ArrayList) mNotebook.getAssignments(mSection));
                case GRADES:
                    return ClassGradesFragment.newInstance((ArrayList) mNotebook.getGrades(mSection));
                default:
                    return null;
            }
        }

        @Override
        public int getItemPosition(Object o){
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case DETAILS:
                    return getString(R.string.title_details);
                case ASSIGNMENTS:
                    return getString(R.string.title_assignments);
                case GRADES:
                    return getString(R.string.title_grades);
                default:
                    return "";
            }
        }
    }

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

    }

    public void onAssignmentsChanged(List<Assignment> assignments){
        mNotebook = ClassLoader.loadNotebooks(this).get(mSchedule);
        myClassPagerAdapter.notifyDataSetChanged();
    }

    public void onActionModeChanged(ActionMode actionMode){
        currentActionMode = actionMode;
    }

    public void onAssignmentCompleted(Assignment assignment){
        mNotebook = ClassLoader.loadNotebooks(this).get(mSchedule);

        //add the grade to the notebook
        mNotebook.getGrades().get(mSection).add(assignment);
        ClassLoader.updateNotebooks(this);


        myClassPagerAdapter.notifyDataSetChanged();
    }

    public void onGradesChanged(){
        mNotebook = ClassLoader.loadNotebooks(this).get(mSchedule);
        myClassPagerAdapter.notifyDataSetChanged();
    }

    public void onAssignmentsSortingKeySelected(int which){
        //CAUTION: must update the sorting key if the order of the sorting option string array changes

        mNotebook.changeSortingMethod(mSection, Notebook.ASSIGNMENTS, which);
        ClassLoader.updateNotebook(this, mSchedule, mNotebook);

        //notify the user of the update
        String[] sortingModes = getResources().getStringArray(R.array.sort_by_dialog_array);
        Toast toast = Toast.makeText(this, getString(R.string.toast_sort_by) + sortingModes[which].toLowerCase(), Toast.LENGTH_SHORT);
        toast.show();


        myClassPagerAdapter.notifyDataSetChanged();
    }

    private ViewPager.OnPageChangeListener myOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position!=mCurrentPage&&currentActionMode!=null){
                currentActionMode.finish();
            }
            mCurrentPage = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class MyClassActivity extends ActionBarActivity {

    private final static int DETAILS = 0;
    private final static int ASSIGNMENTS = 1;
    private final static int NOTES = 2;

    private static final String CURRENT_PAGE = "currentPage";

    private Section mSection;
    private Notebook mNotebook;

    private MyClassPagerAdapter myClassPagerAdapter;
    private ViewPager mViewPager;
    private int mCurrentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_class);

        Intent intent = getIntent();
        mSection = (Section) intent.getSerializableExtra("MySection");
        mNotebook = (Notebook) intent.getSerializableExtra("MyNotebook");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mSection.getContainingClass().getName());

        if(savedInstanceState!=null){
            mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE);
        }

        myClassPagerAdapter =
                new MyClassPagerAdapter(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(myClassPagerAdapter);
        mViewPager.setCurrentItem(mCurrentPage);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){

        savedInstanceState.putInt(CURRENT_PAGE, mViewPager.getCurrentItem());
        super.onSaveInstanceState(savedInstanceState);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_class, menu);
        return true;
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
                case NOTES:
                    return PlaceholderFragment.newInstance(NOTES);
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
                case NOTES:
                    return getString(R.string.title_notes);
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
}

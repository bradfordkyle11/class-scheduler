package com.kmbapps.classscheduler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

//TODO: make new app icon (this needs to be saved as ic_launcher.png in the drawable folders)
//TODO: put list TextViews in LinearLayouts and give them padding inside view
//TODO: navdrawer accessible from other activities


public class Home extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, ScheduleFragment.OnScheduleInteractionListener,
        DesiredClassesFragment.OnFragmentInteractionListener, MyClassesFragment.OnMyClassesFragmentInteractionListener{

    private static final int MENU_EDIT = 0;
    private static final int MENU_DELETE = 1;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle = "Class Scheduler";
    private int mCurrentPage;


    private final String CURRENT_PAGE = "currentPage";
    private AdView mAdView;

    private Schedule currentSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE);
        }

        currentSchedule = ClassLoader.loadCurrentSchedule(this);

        setContentView(R.layout.activity_home);


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
       // mTitle = getTitle();
        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
                findViewById(R.id.close_ad).setVisibility(View.VISIBLE);
            }
        });
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("A127310EFE19D504207566F24866F68C").build();
        mAdView.loadAd(adRequest);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putInt(CURRENT_PAGE, mCurrentPage);

        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        mCurrentPage = position;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = getFragment(position);
        Fragment existingFragment = fragmentManager.findFragmentById(R.id.container);
        if(existingFragment!=null&&existingFragment.getClass().equals(fragment.getClass())){
            return;
        }

        fragmentManager.beginTransaction()
               // .addToBackStack("blah")
                .replace(R.id.container, fragment)
                .commit();

    }

    public void reloadPage(int position) {
        mCurrentPage = position;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                //.addToBackStack("blah")
                .replace(R.id.container, getFragment(position))
                .commit();
        getSupportActionBar().setTitle(mTitle);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case NavigationDrawerListAdapter.SCHEDULE:
                mTitle = getString(R.string.title_schedule);
                break;
            case NavigationDrawerListAdapter.SCHEDULE_DESIGNER:
                mTitle = getString(R.string.title_design_a_schedule);
                break;
            case NavigationDrawerListAdapter.MY_CLASSES:
                mTitle = getString(R.string.title_my_classes);
                break;
            case NavigationDrawerListAdapter.MY_CALENDAR:
                mTitle = getString(R.string.title_my_calendar);
                break;
            case NavigationDrawerListAdapter.MY_ASSIGNMENTS:
                mTitle = getString(R.string.title_my_assignments);
                break;

        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    protected void onPause() {
        //ClassLoader.save(this);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.home, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);

    }

    private Fragment getFragment(int position) {
        switch (position) {
            case NavigationDrawerListAdapter.SCHEDULE:
                mTitle = getString(R.string.header_schedule);
                currentSchedule = ClassLoader.loadCurrentSchedule(this);
                return ScheduleFragment.newInstance(currentSchedule, true);

            case NavigationDrawerListAdapter.SCHEDULE_DESIGNER:
                mTitle = getString(R.string.header_design_a_schedule);
                return CreateScheduleFragment.newInstance("hello", "world");

            case NavigationDrawerListAdapter.MY_CLASSES:
                mTitle = getString(R.string.title_my_classes);
                currentSchedule = ClassLoader.loadCurrentSchedule(this);
                return MyClassesFragment.newInstance(currentSchedule);

            //TODO: MyCalendar fragment
            case NavigationDrawerListAdapter.MY_CALENDAR:
                mTitle = getString(R.string.title_my_calendar);
                return PlaceholderFragment.newInstance(position);

            //TODO: MyAssignments fragment
            case NavigationDrawerListAdapter.MY_ASSIGNMENTS:
                mTitle = getString(R.string.title_my_assignments);
                return PlaceholderFragment.newInstance(position);

            default:
                return PlaceholderFragment.newInstance(position);
        }
    }

    public void onCreateScheduleClick(View view){
        mNavigationDrawerFragment.selectItem(NavigationDrawerListAdapter.SCHEDULE_DESIGNER);
        onNavigationDrawerItemSelected(NavigationDrawerListAdapter.SCHEDULE_DESIGNER);
    }

    public void onSectionDeleted(){
        reloadPage(NavigationDrawerListAdapter.SCHEDULE_DESIGNER);

    }

    public void onAddClassClick(View view){
        Intent intent = new Intent(this, AddClassActivity.class);
        startActivity(intent);
    }

    public void onSetScheduleClick(){
        reloadPage(NavigationDrawerListAdapter.SCHEDULE_DESIGNER);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
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
        public void onAttach(Context context) {
            super.onAttach(context);
            Activity activity = null;
            if (context instanceof Activity){
                activity = (Activity) context;
            }
            // Verify that the host activity implements the callback interface
            if (activity != null) {
                ((Home) activity).onSectionAttached(
                        getArguments().getInt(ARG_SECTION_NUMBER));
            }
        }


    }

    public void addClassSection(View view){
        Intent intent = new Intent(this, AddClassSectionActivity.class);
        intent.putExtra("MyClass", (Class) view.getTag());
        intent.putExtra("newClass", true);
        startActivity(intent);
    }

    public void editClass(View view){
        Intent intent = new Intent(this, EditClassActivity.class);
        intent.putExtra("MyClass", (Class) view.getTag());
        startActivity(intent);
    }

    public void onActionModeChanged(ActionMode actionMode){
        if(getSupportFragmentManager().findFragmentById(R.id.container) instanceof CreateScheduleFragment) {
            CreateScheduleFragment createScheduleFragment = (CreateScheduleFragment) getSupportFragmentManager().findFragmentById(R.id.container);
            if (createScheduleFragment != null) {
                createScheduleFragment.onActionModeChanged(actionMode);
            }
        }
    }

    public void closeAd(View v){
        v.setVisibility(View.GONE);
        mAdView.destroy();
        mAdView.setVisibility(View.GONE);
    }

    @Override
    public void onClassesDropped() {
        reloadPage(NavigationDrawerListAdapter.MY_CLASSES);
    }
}

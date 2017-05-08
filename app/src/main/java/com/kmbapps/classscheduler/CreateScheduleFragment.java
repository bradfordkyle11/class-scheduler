package com.kmbapps.classscheduler;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */

//TODO: toast when going from 0 to 1 schedules, telling the user they can swipe to see the schedules
public class CreateScheduleFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Schedule>>{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int DESIRED_CLASSES = 0;
    private static final int NUM_PAGES_OTHER_THAN_SCHEDULES = 1;

    private static final String CURRENT_PAGE = "currentPage";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private List<Schedule> potentialSchedules;
    ScheduleDesignerPagerAdapter mScheduleDesignerPagerAdapter;
    ViewPager mViewPager;
    private int mCurrentPage = 0;

    private String mParam1;
    private String mParam2;

    private ActionMode currentActionMode;

    private OnFragmentInteractionListener mListener;
    private boolean loading = true;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateScheduleFragment.
     */
    public static CreateScheduleFragment newInstance(String param1, String param2) {
        CreateScheduleFragment fragment = new CreateScheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public CreateScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(ScheduleLoader.ALL_SCHEDULES_LOADER, null, this).forceLoad();
        loading = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }

        if(savedInstanceState!=null){
            mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE);
        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.create_schedule, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), ScheduleDesignerSettingsActivity.class);
            intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, ScheduleDesignerSettingsActivity.ScheduleDesignerPreferenceFragment.class.getName());
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Schedule>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ScheduleLoader.ALL_SCHEDULES_LOADER:
                return new ScheduleLoader(getActivity());
            case ScheduleLoader.SELECT_SCHEDULES_LOADER:
                return new ScheduleLoader(getActivity());
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Schedule>> loader, List<Schedule> data) {
        int id = loader.getId();
        switch (id){
            case ScheduleLoader.ALL_SCHEDULES_LOADER:
                getLoaderManager().initLoader(ScheduleLoader.SELECT_SCHEDULES_LOADER, null, this).forceLoad();
                break;
            case ScheduleLoader.SELECT_SCHEDULES_LOADER:
                int count;
                //getView().findViewById(R.id.progress_loader).setVisibility(View.GONE);
                potentialSchedules = data;
                if(potentialSchedules==null){
                    count = NUM_PAGES_OTHER_THAN_SCHEDULES;
                }
                else {
                    count = potentialSchedules.size() + NUM_PAGES_OTHER_THAN_SCHEDULES;
                }
                loading = false;
                DesiredClassesFragment dcf = (DesiredClassesFragment) mViewPager.getAdapter().instantiateItem(mViewPager, mViewPager.getCurrentItem());
                dcf.setLoading(loading);
                mScheduleDesignerPagerAdapter.setCount(count);
                mScheduleDesignerPagerAdapter.notifyDataSetChanged();

                //set the current page once the schedules have been loaded
                //mViewPager.setCurrentItem(mCurrentPage);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Schedule>> loader) {

    }

    public class ScheduleDesignerPagerAdapter extends FragmentStatePagerAdapter {


        private int count = NUM_PAGES_OTHER_THAN_SCHEDULES;

        public ScheduleDesignerPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int i) {

            switch(i) {
                case DESIRED_CLASSES:
                    return DesiredClassesFragment.newInstance(loading);

                default:
                    return ScheduleFragment.newInstance(potentialSchedules.get(i-1), false);
            }
        }

        @Override
        public int getItemPosition(Object o){
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case DESIRED_CLASSES:
                    return getString(R.string.header_desired_classes);
                default:
                    int scheduleNum = position + 1 - NUM_PAGES_OTHER_THAN_SCHEDULES;
                    return getString(R.string.header_potential_schedule) + " " + scheduleNum;
            }
        }

        public void setCount(int count){
            this.count = count;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((Home)getActivity()).getSupportActionBar().setTitle(getString(R.string.header_design_a_schedule));



        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_schedule, container, false);
        mScheduleDesignerPagerAdapter =
                new ScheduleDesignerPagerAdapter(
                        getFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.createSchedulePager);
        mViewPager.setAdapter(mScheduleDesignerPagerAdapter);
        mViewPager.setOnPageChangeListener(myOnPageChangeListener);
        mViewPager.setCurrentItem(mCurrentPage);

        //.findViewById(R.id.progress_loader).setVisibility(View.VISIBLE);

        //load the schedules
        //new LoadClassesAndCreateSchedulesTask().execute(new Void[1]);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){

       savedInstanceState.putInt(CURRENT_PAGE, mViewPager.getCurrentItem());
       super.onSaveInstanceState(savedInstanceState);

    }



    @Override
    public void onResume(){
        //set the actionbar title
        super.onResume();
        ((Home)getActivity()).getSupportActionBar().setTitle(getString(R.string.header_design_a_schedule));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
           // mListener = (OnFragmentInteractionListener) activity;
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
    }

    private class LoadClassesAndCreateSchedulesTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... v){

            //load classes so that they are not null
            ClassLoader.loadClasses(MyApp.getContext());

            //calculate schedules
            ClassLoader.updateSchedules(getActivity());
            potentialSchedules =  ClassLoader.loadSchedules(getActivity(), ClassLoader.SELECT_SCHEDULES);


            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            int count;
            if(potentialSchedules==null){
                count = NUM_PAGES_OTHER_THAN_SCHEDULES;
            }
            else {
                count = potentialSchedules.size() + NUM_PAGES_OTHER_THAN_SCHEDULES;
            }
            mScheduleDesignerPagerAdapter.setCount(count);
            mScheduleDesignerPagerAdapter.notifyDataSetChanged();

            //set the current page once the schedules have been loaded

        }
    }

    public void onActionModeChanged(ActionMode actionMode){
        currentActionMode = actionMode;
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

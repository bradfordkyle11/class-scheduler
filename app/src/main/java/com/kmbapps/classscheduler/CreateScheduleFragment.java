package com.kmbapps.classscheduler;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class CreateScheduleFragment extends Fragment{
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

    private OnFragmentInteractionListener mListener;

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

    public class ScheduleDesignerPagerAdapter extends FragmentStatePagerAdapter {


        private int count = NUM_PAGES_OTHER_THAN_SCHEDULES;

        public ScheduleDesignerPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int i) {

            switch(i) {
                case DESIRED_CLASSES:
                    return DesiredClassesFragment.newInstance("hello", "world");

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
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(mScheduleDesignerPagerAdapter);
        mViewPager.setCurrentItem(mCurrentPage);

        //load the schedules
        new LoadClassesAndCreateSchedulesTask().execute(new Void[1]);

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
            ClassLoader.loadClasses(getActivity().getApplicationContext());

            //calculate schedules
            ClassLoader.updateSchedules();
            potentialSchedules =  ClassLoader.loadSchedules();


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
            mViewPager.setCurrentItem(mCurrentPage);
        }
    }


}

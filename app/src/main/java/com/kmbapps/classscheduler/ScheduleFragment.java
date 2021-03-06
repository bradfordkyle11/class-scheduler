package com.kmbapps.classscheduler;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.ads.AdView;

import java.util.Hashtable;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduleFragment.OnScheduleInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ScheduleFragment extends Fragment implements ConfirmationDialogFragment.ConfirmationDialogListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SCHEDULE = "schedule";
    private static final String ARG_IS_MAIN_SCHEDULE = "isMainSchedule";

    private int SCROLL_VIEW_TOP_PADDING_DP;
    private final int SCROLL_VIEW_TOP_PADDING = 5;
    private int lowestPosition = 10000000;

    private static final int NUM_HOURS_TO_DISPLAY = 24;

    private double HOUR_DP_SCALE;

    private static int CALENDAR_SCALE = 1; //the larger the number, the shorter the class blocks on the calendar
    //CAUTION: you must change the height of the calendar itself in xml if you change this
    private static final double LINES_PER_HOUR_BASE = 27.d/8.d;
    private static final double HEIGHT_USED_TO_CALCULATE_LINES_PER_HOUR = 1080;



    private Schedule schedule;
    private boolean mainSchedule;
    private AdView mAdView;

    private OnScheduleInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ScheduleFragment.
     */
    public static ScheduleFragment newInstance(Schedule schedule, boolean isMainSchedule) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SCHEDULE, schedule);
        args.putBoolean(ARG_IS_MAIN_SCHEDULE, isMainSchedule);
        fragment.setArguments(args);
        return fragment;
    }
    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources r = getResources();
        SCROLL_VIEW_TOP_PADDING_DP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SCROLL_VIEW_TOP_PADDING, r.getDisplayMetrics());
        if (getArguments() != null) {
            schedule = (Schedule) getArguments().getSerializable(ARG_SCHEDULE);
            mainSchedule = getArguments().getBoolean(ARG_IS_MAIN_SCHEDULE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        HOUR_DP_SCALE = (double) getResources().getDimension(R.dimen.schedule_height) / NUM_HOURS_TO_DISPLAY;
        //show fab if not on the main schedule
        if(!mainSchedule){
            View fab = view.findViewById(R.id.select_schedule);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(setScheduleListener);
        }
        else{
            View fab = view.findViewById(R.id.select_schedule);
            fab.setVisibility(View.GONE);
        }

        //gray out fab if this is the current schedule
        Schedule currentSchedule = ClassLoader.loadCurrentSchedule(getActivity().getApplicationContext());
        if(currentSchedule!=null) {
            if (schedule.equals(currentSchedule)) {
                FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.select_schedule);
                fab.setEnabled(false);
            }
        }

        Button noSchedule = (Button) view.findViewById(R.id.noSchedules);
        if (schedule.isEmpty()) {
            noSchedule.setVisibility(View.VISIBLE);
            noSchedule.setOnClickListener(createScheduleListener);
        }
        else{
            noSchedule.setVisibility(View.INVISIBLE);

            int calendarHeight = 1440/CALENDAR_SCALE;

            int[] colors = getActivity().getResources().getIntArray(R.array.classCalendarColors);

            //add onClickListeners to the calendar
            RelativeLayout su = (RelativeLayout) view.findViewById(R.id.sunday);
            su.setOnClickListener(hideClassInfoListener);
            RelativeLayout m = (RelativeLayout) view.findViewById(R.id.monday);
            m.setOnClickListener(hideClassInfoListener);
            RelativeLayout tu = (RelativeLayout) view.findViewById(R.id.tuesday);
            tu.setOnClickListener(hideClassInfoListener);
            RelativeLayout w = (RelativeLayout) view.findViewById(R.id.wednesday);
            w.setOnClickListener(hideClassInfoListener);
            RelativeLayout th = (RelativeLayout) view.findViewById(R.id.thursday);
            th.setOnClickListener(hideClassInfoListener);
            RelativeLayout f = (RelativeLayout) view.findViewById(R.id.friday);
            f.setOnClickListener(hideClassInfoListener);
            RelativeLayout sa = (RelativeLayout) view.findViewById(R.id.saturday);
            sa.setOnClickListener(hideClassInfoListener);

            //TODO: set colors in the desired classes page instead of here, so that colors are consistent across all potential schedules

            //display the classes on the schedule
            int colorPosition = 0;
            for(Section section: schedule.getSections()) {
                for(MyTime time: section.getTimes()) {

                    Resources r = getResources();

                    double height = (MyTime.toHours(time.getEndHour(), time.getEndMinute()) - MyTime.toHours(time.getStartHour(), time.getStartMinute()))
                            * HOUR_DP_SCALE;
                    double scheduleHeight = (double) getResources().getDimension(R.dimen.schedule_height);
                    double baseScheduleHeightDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) HEIGHT_USED_TO_CALCULATE_LINES_PER_HOUR, getResources().getDisplayMetrics());
                    int maxLines = (int) ((MyTime.toHours(time.getEndHour(), time.getEndMinute()) - MyTime.toHours(time.getStartHour(), time.getStartMinute()))
                        * LINES_PER_HOUR_BASE * (scheduleHeight / baseScheduleHeightDp));
                    //float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) height, r.getDisplayMetrics());
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            (int) height
                    );
                    double pos = MyTime.toHours(time.getStartHour(), time.getStartMinute()) * HOUR_DP_SCALE;
                    //float posInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) pos, r.getDisplayMetrics());



                    //set lowest position allowing the view to be scrolled to the earliest thing on the schedule
                    if ((int) pos < lowestPosition){
                        lowestPosition = (int) pos;
                    }

                    String suAbbrev = getResources().getString(R.string.sunday_abbreviation);
                    String mAbbrev = getResources().getString(R.string.monday_abbreviation);
                    String tuAbbrev = getResources().getString(R.string.tuesday_abbreviation);
                    String wAbbrev = getResources().getString(R.string.wednesday_abbreviation);
                    String thAbbrev = getResources().getString(R.string.thursday_abbreviation);
                    String fAbbrev = getResources().getString(R.string.friday_abbreviation);
                    String saAbbrev = getResources().getString(R.string.saturday_abbreviation);

                    params.setMargins(0, (int) pos, 0, 0);

                    //LinearLayout.LayoutParams calendarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, calendarHeight, 1);

                    String[] days = getResources().getStringArray(R.array.day_abbreviation_array);
                    for (String day : days){
                        if (time.getDays().contains(day)){
                            TextView tv = (TextView) inflater.inflate(R.layout.class_calendar_representation, null);
                            tv.setText(section.getContainingClass().getHtmlScheduleOverview(time));
                            tv.setLayoutParams(params);
                            tv.setBackgroundColor(section.getContainingClass().getColor());
                            tv.setTag(R.id.TAG_SECTION, section);
                            tv.setTag(R.id.TAG_COLOR, section.getContainingClass().getColor());
                            tv.setOnClickListener(selectClassListener);
                            tv.setMaxLines(maxLines);
                            if (day.equals(suAbbrev)) {
                                su.addView(tv);
                            }
                            else if (day.equals(mAbbrev)){
                                m.addView(tv);
                            }
                            else if (day.equals(tuAbbrev)){
                                tu.addView(tv);
                            }
                            else if (day.equals(wAbbrev)){
                                w.addView(tv);
                            }
                            else if (day.equals(thAbbrev)){
                                th.addView(tv);
                            }
                            else if (day.equals(fAbbrev)){
                                f.addView(tv);
                            }
                            else if (day.equals(saAbbrev)){
                                sa.addView(tv);
                            }
                            else {
                                throw new NullPointerException(getResources().getString(R.string.exception_no_such_day, day));
                            }
                        }
                    }

//                    if(time.getDays().contains("Su")){
//                        TextView tv = (TextView) inflater.inflate(R.layout.class_calendar_representation, null);
//                        tv.setText(section.getContainingClass().getHtmlScheduleOverview(time));
//                        tv.setLayoutParams(params);
//                        tv.setBackgroundColor(section.getContainingClass().getColor());
//                        tv.setTag(R.id.TAG_SECTION, section);
//                        tv.setTag(R.id.TAG_COLOR, section.getContainingClass().getColor());
//                        tv.setOnClickListener(selectClassListener);
//                        //su.setLayoutParams(calendarParams);
//                        su.addView(tv);
//
//
//                    }
//                    if(time.getDays().contains("M")){
//                        TextView tv = (TextView) inflater.inflate(R.layout.class_calendar_representation, null);
//                        tv.setText(section.getContainingClass().getHtmlScheduleOverview(time));
//                        tv.setLayoutParams(params);
//                        tv.setBackgroundColor(section.getContainingClass().getColor());
//                        tv.setTag(R.id.TAG_SECTION, section);
//                        tv.setTag(R.id.TAG_COLOR, section.getContainingClass().getColor());
//                        tv.setOnClickListener(selectClassListener);
//                        //m.setLayoutParams(calendarParams);
//                        m.addView(tv);
//
//                    }
//
//                    if(time.getDays().contains("Tu")){
//                        TextView tv = (TextView) inflater.inflate(R.layout.class_calendar_representation, null);
//                        tv.setText(section.getContainingClass().getHtmlScheduleOverview(time));
//                        tv.setLayoutParams(params);
//                        tv.setBackgroundColor(section.getContainingClass().getColor());
//                        tv.setTag(R.id.TAG_SECTION, section);
//                        tv.setTag(R.id.TAG_COLOR, section.getContainingClass().getColor());
//                        tv.setOnClickListener(selectClassListener);
//                        //tu.setLayoutParams(calendarParams);
//                        tu.addView(tv);
//
//                    }
//
//                    if(time.getDays().contains("W")){
//                        TextView tv = (TextView) inflater.inflate(R.layout.class_calendar_representation, null);
//                        tv.setText(section.getContainingClass().getHtmlScheduleOverview(time));
//                        tv.setLayoutParams(params);
//                        tv.setBackgroundColor(section.getContainingClass().getColor());
//                        tv.setTag(R.id.TAG_SECTION, section);
//                        tv.setTag(R.id.TAG_COLOR, section.getContainingClass().getColor());
//                        tv.setOnClickListener(selectClassListener);
//                        //w.setLayoutParams(calendarParams);
//                        w.addView(tv);
//
//                    }
//
//                    if(time.getDays().contains("Th")){
//                        TextView tv = (TextView) inflater.inflate(R.layout.class_calendar_representation, null);
//                        tv.setText(section.getContainingClass().getHtmlScheduleOverview(time));
//                        tv.setLayoutParams(params);
//                        tv.setBackgroundColor(section.getContainingClass().getColor());
//                        tv.setTag(R.id.TAG_SECTION, section);
//                        tv.setTag(R.id.TAG_COLOR, section.getContainingClass().getColor());
//                        tv.setOnClickListener(selectClassListener);
//                        //th.setLayoutParams(calendarParams);
//                        th.addView(tv);
//
//                    }
//
//                    if(time.getDays().contains("F")){
//                        TextView tv = (TextView) inflater.inflate(R.layout.class_calendar_representation, null);
//                        tv.setText(section.getContainingClass().getHtmlScheduleOverview(time));
//                        tv.setLayoutParams(params);
//                        tv.setBackgroundColor(section.getContainingClass().getColor());
//                        tv.setTag(R.id.TAG_SECTION, section);
//                        tv.setTag(R.id.TAG_COLOR, section.getContainingClass().getColor());
//                        tv.setOnClickListener(selectClassListener);
//                        //f.setLayoutParams(calendarParams);
//                        f.addView(tv);
//
//                    }
//
//                    if(time.getDays().contains("Sa")){
//                        TextView tv = (TextView) inflater.inflate(R.layout.class_calendar_representation, null);
//                        tv.setText(section.getContainingClass().getHtmlScheduleOverview(time));
//                        tv.setLayoutParams(params);
//                        tv.setBackgroundColor(section.getContainingClass().getColor());
//                        tv.setTag(R.id.TAG_SECTION, section);
//                        tv.setTag(R.id.TAG_COLOR, section.getContainingClass().getColor());
//                        tv.setOnClickListener(selectClassListener);
//                        //sa.setLayoutParams(calendarParams);
//                        sa.addView(tv);
//
//                    }
                }

                colorPosition += 1;
                if (colorPosition==colors.length){
                    colorPosition = 0;
                }
            }

            final ScrollView scrollView = (ScrollView) view.findViewById(R.id.calendarScrollView);

            scrollView.postDelayed(new Runnable() {

                @Override
                public void run() {
                    scrollView.scrollTo(0, lowestPosition - SCROLL_VIEW_TOP_PADDING_DP);
                }
            }, 100);

//            ViewTreeObserver vto = scrollView.getViewTreeObserver();
//            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                public void onGlobalLayout() {
//                    scrollView.scrollTo(0, lowestPosition - SCROLL_VIEW_TOP_PADDING_DP);
//                }
//            });
        }

        return view;
    }

    public void onButtonPressed(View view) {
        if (mListener != null) {
            mListener.onCreateScheduleClick(view);
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
                mListener = (OnScheduleInteractionListener) activity;
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
    public interface OnScheduleInteractionListener {
        void onCreateScheduleClick(View view);

        void onSetScheduleClick();
    }

    View.OnClickListener createScheduleListener = new View.OnClickListener() {
        public void onClick(View view) {
            openScheduleDesigner(view);
        }
    };

    View.OnClickListener hideClassInfoListener = new View.OnClickListener() {
        public void onClick(View view){
            hideClassInfo();
        }
    };

    View.OnClickListener selectClassListener = new View.OnClickListener() {
        public void onClick(View view){
            selectClass(view);
        }
    };

    View.OnClickListener setScheduleListener = new View.OnClickListener() {
        public void onClick(View view){
            Schedule currSchedule = ClassLoader.loadCurrentSchedule(getActivity());
            if (currSchedule.getSections().isEmpty()){
                setAsSchedule();
            }
            else {
                ConfirmationDialogFragment confirmation = ConfirmationDialogFragment.newInstance(getString(R.string.title_set_schedule_confirmation), schedule, ConfirmationDialogFragment.FRAGMENT);
                confirmation.setTargetFragment(ScheduleFragment.this, 1);
                confirmation.show(getActivity().getSupportFragmentManager(), "confirmation");
            }
        }
    };

    public void selectClass(View view){
        View parent = getView();
        View adClose =  null;
        while (parent != null && adClose == null){
            parent = (View) parent.getParent();
            adClose = parent.findViewById(R.id.close_ad);
        }

        if (adClose != null){
            adClose.setVisibility(View.GONE);
        }
        View totalView = getView();
        if (totalView != null) {
            View layout = totalView.findViewById(R.id.scheduleClassInfoLayout);
            layout.setVisibility(View.VISIBLE);
        }


        Section section = (Section) view.getTag(R.id.TAG_SECTION);
        Class mClass = section.getContainingClass();

        //set color
        View colorBar = getView().findViewById(R.id.classColorBar);
        Integer color = (Integer) view.getTag(R.id.TAG_COLOR);
        colorBar.setBackgroundColor(color);

        //set class title
        TextView title = (TextView) getView().findViewById(R.id.classTitle);
        String classInfo = mClass.getDepartment() + " " + mClass.getNumber();
        if (!mClass.getName().equals("")){
            classInfo += " - " + mClass.getName();
        }
        title.setText(classInfo);

        //set class info
        TextView info = (TextView) getView().findViewById(R.id.classInfo);
        info.setText(section.toString());

    }

    public void hideClassInfo(){
        View totalView = getView();
        if (totalView != null) {
            View classInfo = getView().findViewById(R.id.scheduleClassInfoLayout);
            classInfo.setVisibility(View.GONE);
        }
        View parent = getView();
        View adClose =  null;
        View ad = null;
        while (parent != null && adClose == null){
            parent = (View) parent.getParent();
            adClose = parent.findViewById(R.id.close_ad);
            ad = parent.findViewById(R.id.adView);
        }

        if (adClose != null && ad != null){
            if (ad.getVisibility() == View.VISIBLE) {
                adClose.setVisibility(View.VISIBLE);
            }
        }

    }

    public void setAsSchedule(){
        Schedule currentSchedule = new Schedule(schedule.getSections());
        ClassLoader.setCurrentSchedule(getActivity().getApplicationContext(), currentSchedule);
        View scheduleView = getView();
        Hashtable<Schedule, Notebook> notebooks = ClassLoader.loadNotebooks(getActivity().getApplicationContext());

        if (!notebooks.containsKey(currentSchedule)) {
            notebooks.put(currentSchedule, new Notebook(currentSchedule.getSections()));
            ClassLoader.saveNotebooks(getActivity().getApplicationContext(), notebooks);
        }
        if (scheduleView != null) {
            ViewPager pager = (ViewPager) scheduleView.getParent();
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), getText(R.string.toast_set_current_schedule), Toast.LENGTH_SHORT);
            toast.show();
            pager.getAdapter().notifyDataSetChanged();
        }



    }

    public void openScheduleDesigner(View view){
        mListener.onCreateScheduleClick(view);
    }

    //delete section confirmed
    @Override
    public void onConfirmationPositiveClick(ConfirmationDialogFragment dialog) {
        setAsSchedule();
    }

    //delete section canceled
    @Override
    public void onConfirmationNegativeClick(ConfirmationDialogFragment dialog) {

    }

}

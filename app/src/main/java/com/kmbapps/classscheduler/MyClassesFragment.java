package com.kmbapps.classscheduler;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyClassesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyClassesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyClassesFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SCHEDULE = "schedule";

    private Schedule mSchedule;
    private Notebook mNotebook;


    private OnFragmentInteractionListener mListener;

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
        View view =  inflater.inflate(R.layout.fragment_my_classes, container, false);

        LinearLayout classes = (LinearLayout) view.findViewById(R.id.classes);

        for(Section section : mSchedule.getSections()){
            View v = inflater.inflate(R.layout.list_item_my_classes, null);
            TextView classDetails = (TextView) v.findViewById(R.id.classTextView);
            classDetails.setText(section.getContainingClass().getDepartment() + " " + section.getContainingClass().getNumber() + " " + section.getContainingClass().getName());
            v.setTag(section);
            v.setOnClickListener(classSelectedListener);
            classes.addView(v);
        }
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    public View.OnClickListener classSelectedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity().getApplicationContext(), MyClassActivity.class);
            intent.putExtra("MySection", (Section) v.getTag());
            intent.putExtra("MyNotebook", mNotebook);
            startActivity(intent);
        }
    };

}

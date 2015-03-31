package com.kmbapps.classscheduler;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClassAssignmentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClassAssignmentsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ASSIGNMENTS = "assignments";

    // TODO: Rename and change types of parameters
    private ArrayList<Assignment> mAssignments;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ClassAssignmentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClassAssignmentsFragment newInstance(ArrayList<Assignment> assignments) {
        ClassAssignmentsFragment fragment = new ClassAssignmentsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ASSIGNMENTS, assignments);
        fragment.setArguments(args);
        return fragment;
    }

    public ClassAssignmentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAssignments = (ArrayList<Assignment>) getArguments().getSerializable(ARG_ASSIGNMENTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_class_assignments, container, false);
    }


}

package com.kmbapps.classscheduler;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ClassDetailsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION = "section";

    private Section mSection;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ClassDetailsFragment.
     */
    public static ClassDetailsFragment newInstance(Section section) {
        ClassDetailsFragment fragment = new ClassDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SECTION, section);
        fragment.setArguments(args);
        return fragment;
    }

    public ClassDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSection = (Section) getArguments().getSerializable(ARG_SECTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_class_details, container, false);

        TextView classTitle = (TextView) v.findViewById(R.id.classTitle);
        classTitle.setText(HtmlCompat.fromHtml("<b>Class:</b> " + mSection.getContainingClass().getDepartment() + " " + mSection.getContainingClass().getNumber() + " " + mSection.getContainingClass().getName()));
        TextView classSchedule = (TextView) v.findViewById(R.id.classSchedule);

        //TODO: HTML format time method so that line breaks are properly added
        classSchedule.setText(HtmlCompat.fromHtml("<b>Schedule:</b><br>" + mSection.formatTime()));
        TextView professor = (TextView) v.findViewById(R.id.professor);
        professor.setText(HtmlCompat.fromHtml("<b>Professor:</b> " + mSection.getProfessor()));
        TextView uniqueNumber = (TextView) v.findViewById(R.id.uniqueNumber);
        uniqueNumber.setText(HtmlCompat.fromHtml("<b>Section number:</b> " + mSection.getSectionNumber()));
        TextView creditHours = (TextView) v.findViewById(R.id.creditHours);
        creditHours.setText(HtmlCompat.fromHtml("<b>Credit hours:</b> " + mSection.getContainingClass().getCreditHours()));
        TextView otherInfo = (TextView) v.findViewById(R.id.otherInfo);
        otherInfo.setText(mSection.getNotes());

        return v;
    }


}

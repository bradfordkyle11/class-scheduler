package com.kmbapps.classscheduler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ConfirmationDialogFragment extends DialogFragment {

    public final static int ACTIVITY = 0;
    public final static int FRAGMENT = 1;

    private ConfirmationDialogListener mListener;

    private final static int DELETE_CLASS = 0;
    private final static int DELETE_SECTION = 1;
    private final static int DELETE_ASSIGNMENT = 2;

    private int dialogType;
    private int mParent;

    private Class classToDelete;
    private Section sectionToDelete;
    private Assignment assignmentToDelete;

    private String mTitle;

    //dialog for deleting a class
    static ConfirmationDialogFragment newInstance(String title, Class classToDelete, int parent) {
        ConfirmationDialogFragment f = new ConfirmationDialogFragment();

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putSerializable("classToDelete", classToDelete);
        args.putInt("dialogType", DELETE_CLASS);
        args.putInt("parent", parent);
        f.setArguments(args);

        return f;
    }

    //dialog for deleting a section
    static ConfirmationDialogFragment newInstance(String title, Section sectionToDelete, int parent) {
        ConfirmationDialogFragment f = new ConfirmationDialogFragment();

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putSerializable("sectionToDelete", sectionToDelete);
        args.putInt("dialogType", DELETE_SECTION);
        args.putInt("parent", parent);
        f.setArguments(args);

        return f;
    }

    //dialog for deleting an assignment
    static ConfirmationDialogFragment newInstance(String title, Assignment assignmentToDelete, int parent) {
        ConfirmationDialogFragment f = new ConfirmationDialogFragment();

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putSerializable("assignmentToDelete", assignmentToDelete);
        args.putInt("dialogType", DELETE_ASSIGNMENT);
        args.putInt("parent", parent);
        f.setArguments(args);

        return f;
    }

    public ConfirmationDialogFragment(){

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mParent = getArguments().getInt("parent");

        if(mParent==ACTIVITY){
            try{
                mListener = (ConfirmationDialogListener) activity;
            } catch (ClassCastException e) {
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(activity.toString() + " must implement ConfirmationDialogListener");
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialogType = getArguments().getInt("dialogType");
        mTitle = getArguments().getString("title");

        switch(dialogType){
            case DELETE_CLASS:
                classToDelete = (Class) getArguments().getSerializable("classToDelete");
                break;
            case DELETE_SECTION:
                sectionToDelete = (Section) getArguments().getSerializable("sectionToDelete");
                break;
            case DELETE_ASSIGNMENT:
                assignmentToDelete = (Assignment) getArguments().getSerializable("assignmentToDelete");
        }

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle)
                        //set grade
                .setPositiveButton(R.string.dialog_action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //mListener will be null if the parent is a fragment
                        if (mListener == null) {
                            try {
                                // Instantiate the NoticeDialogListener so we can send events to the host
                                mListener = (ConfirmationDialogListener) getTargetFragment();
                            } catch (ClassCastException e) {
                                // The activity doesn't implement the interface, throw exception
                                throw new ClassCastException("The target fragment must implement ConfirmationDialogListener");
                            }
                        }

                        if (mListener != null) {
                            mListener.onConfirmationPositiveClick(ConfirmationDialogFragment.this);
                        }

                        dialog.dismiss();
                    }
                })
                        //cancel setting grade
                .setNegativeButton(R.string.dialog_action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //mListener will be null if the parent is a fragment
                        if (mListener == null) {
                            try {
                                // Instantiate the NoticeDialogListener so we can send events to the host
                                mListener = (ConfirmationDialogListener) getTargetFragment();
                            } catch (ClassCastException e) {
                                // The activity doesn't implement the interface, throw exception
                                throw new ClassCastException("The target fragment must implement ConfirmationDialogListener");
                            }
                        }
                        if (mListener != null) {
                            mListener.onConfirmationNegativeClick(ConfirmationDialogFragment.this);
                        }
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public Object getExtra(){
        switch(dialogType){
            case DELETE_CLASS:
                return classToDelete;
            case DELETE_SECTION:
                return sectionToDelete;
            case DELETE_ASSIGNMENT:
                return assignmentToDelete;
            default:
                return null;
        }
    }

    public interface ConfirmationDialogListener{
        public void onConfirmationPositiveClick(ConfirmationDialogFragment dialog);
        public void onConfirmationNegativeClick(ConfirmationDialogFragment dialog);
    }
}

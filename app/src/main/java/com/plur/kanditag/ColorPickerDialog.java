package com.plur.kanditag;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.larswerkman.holocolorpicker.ColorPicker;

/**
 * Created by Jim on 2/23/16.
 */
public class ColorPickerDialog extends DialogFragment {

    public interface ColorPickerListener {
        void onColorPicked(int colorCode);
    }

    private static final String COLOR_KEY = "color";

    private ColorPickerListener mListener;
    private ColorPicker mPicker;

    public static ColorPickerDialog newInstance(int oldColor) {
        ColorPickerDialog dialog = new ColorPickerDialog();

        Bundle args = new Bundle();
        args.putInt(COLOR_KEY, oldColor);
        dialog.setArguments(args);

        return dialog;
    }

    public ColorPickerDialog() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (ColorPickerListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ColorPickerListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.color_picker_dialog, null);
        int oldColor = getArguments().getInt(COLOR_KEY);

        mPicker = (ColorPicker) view.findViewById(R.id.picker);
        mPicker.setShowOldCenterColor(false);
        mPicker.setColor(oldColor);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle(R.string.chooseColor);
        builder.setNeutralButton(R.string.set, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onColorPicked(mPicker.getColor());
            }
        });
        return builder.create();
    }
}

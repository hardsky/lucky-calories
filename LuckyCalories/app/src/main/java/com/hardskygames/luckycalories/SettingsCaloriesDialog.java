package com.hardskygames.luckycalories;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.ButterKnife;

/**
 * Created by HardSkyGames on 18.05.15.
 */
public class SettingsCaloriesDialog extends DialogFragment {

    public interface OnSaveListener{
        void onSave(String calorie);
    }

    private OnSaveListener listener;
    private TextView txtCalories;
    private String calories;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity().getApplicationContext();
        View view = getActivity().getLayoutInflater().inflate(R.layout.settings_calories, null);
        txtCalories = ButterKnife.findById(view, R.id.txtCalories);
        txtCalories.setText(calories);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.fragment_settings_daily_calories)
                .setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onSave(txtCalories.getText().toString());
                    }
                })
                .create();
    }

    public void setListener(OnSaveListener listener){
        this.listener = listener;
    }

    public void setCalories(String calories){
        this.calories = calories;
    }
}

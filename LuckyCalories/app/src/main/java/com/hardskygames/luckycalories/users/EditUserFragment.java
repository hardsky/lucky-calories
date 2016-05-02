package com.hardskygames.luckycalories.users;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.hardskygames.luckycalories.BaseActivity;
import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.users.models.UserModel;
import com.hardskygames.luckycalories.users.events.EditUserEvent;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditUserFragment extends DialogFragment implements Toolbar.OnMenuItemClickListener {

    @Inject
    Bus bus;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.txtName)
    EditText txtName;
    @Bind(R.id.txtEmail)
    EditText txtEmail;
    @Bind(R.id.txtDailyCalories)
    EditText txtDailyCalories;

    UserModel model = new UserModel();
    private int position;

    public EditUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_edit_user, container, false);
        ButterKnife.bind(this, layout);

        toolbar.setTitle(model.getId() > 0 ? "Edit" : "New");
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.inflateMenu(R.menu.dialog_toolbar_menu);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if(model.getId() > 0) { //existed value; edit mode
            txtName.setText(model.getName());
            txtEmail.setText(model.getEmail());
            txtDailyCalories.setText(String.format("%d", model.getDailyCalories()));
        }

        return layout;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
        ((BaseActivity) getActivity()).inject(this);
        bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        try{

            model.setName(txtName.getText().toString());
            model.setEmail(txtEmail.getText().toString());
            model.setDailyCalories(Integer.parseInt(txtDailyCalories.getText().toString()));

            EditUserEvent ev = new EditUserEvent();
            ev.model = model;
            if(model.getId() > 0) {
                ev.position = position;
            }

            model = null;

            bus.post(ev);

            dismiss();
            return true;
        }
        catch (Exception ex){
            Timber.e(ex, "Error on input calorie's values.");
        }
        return false;
    }

    public void setModel(UserModel model) {
        this.model = model;
    }

    public void setIndex(int idx) {
        this.position = idx;
    }
}

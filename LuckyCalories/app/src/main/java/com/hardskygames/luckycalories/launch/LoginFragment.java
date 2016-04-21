package com.hardskygames.luckycalories.launch;


import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;

import com.hardskygames.luckycalories.ButterKnifeFragment;
import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.launch.models.Login;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnFocusChange;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends ButterKnifeFragment {

    @Bind(R.id.txtEmail)
    AppCompatEditText txtEmail;
    @Bind(R.id.txtPassword)
    AppCompatEditText txtPassword;

    @Inject
    Login model;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public void onResume() {
        super.onResume();

        //restore after orientation change
        txtEmail.setText(model.getEmail());
        txtPassword.setText(model.getPassword());
    }

    @OnFocusChange({R.id.txtEmail, R.id.txtPassword})
    public void onTextFocusChange(){

        model.setEmail(txtEmail.getText().toString());
        txtPassword.setText(txtPassword.getText().toString());
    }
}

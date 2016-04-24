package com.hardskygames.luckycalories.launch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hardskygames.luckycalories.BaseFragment;
import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.launch.events.ShowSignUpScreen;
import com.hardskygames.luckycalories.launch.models.Login;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment {

    @Bind(R.id.txtEmail)
    AppCompatEditText txtEmail;
    @Bind(R.id.txtPassword)
    AppCompatEditText txtPassword;

    @Inject
    Login model;
    @Inject
    Bus bus;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, layout);

        return layout;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();

        //restore after orientation change
        txtEmail.setText(model.getEmail());
        txtPassword.setText(model.getPassword());

        bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @OnFocusChange({R.id.txtEmail, R.id.txtPassword})
    public void onTextFocusChange(boolean focused){
        if(focused) //gain focus; we need only 'on lost'
            return;

        if(txtEmail == null || txtPassword == null)
            return;

        model.setEmail(txtEmail.getText().toString());
        model.setPassword(txtPassword.getText().toString());
    }

    @OnClick(R.id.btnSignUp)
    public void goToSignUp(){
        bus.post(new ShowSignUpScreen());
    }
}

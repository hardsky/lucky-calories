package com.hardskygames.luckycalories.launch;


import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;

import com.hardskygames.luckycalories.ButterKnifeFragment;
import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.launch.models.SignUp;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends ButterKnifeFragment {

    @Bind(R.id.txtName)
    AppCompatEditText txtName;
    @Bind(R.id.txtEmail)
    AppCompatEditText txtEmail;
    @Bind(R.id.txtPassword)
    AppCompatEditText txtPassword;
    @Bind(R.id.txtRepeatPassword)
    AppCompatEditText txtRepeatPassword;

    @Inject
    SignUp model;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sign_up;
    }
}

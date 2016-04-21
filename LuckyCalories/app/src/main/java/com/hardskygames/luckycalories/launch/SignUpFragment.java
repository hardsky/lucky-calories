package com.hardskygames.luckycalories.launch;


import android.support.v4.app.Fragment;

import com.hardskygames.luckycalories.ButterKnifeFragment;
import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.launch.models.SignUp;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends ButterKnifeFragment {

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

package com.hardskygames.luckycalories.launch;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hardskygames.luckycalories.BaseFragment;
import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.launch.models.SignUp;
import com.hardskygames.luckycalories.list.CaloriesActivity;
import com.hardskygames.luckycalories.models.User;
import com.mobandme.android.transformer.Transformer;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import io.swagger.client.api.LuckyCaloriesApi;
import io.swagger.client.model.SignUpInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends BaseFragment {

    @Bind(R.id.txtName)
    AppCompatEditText txtName;
    @Bind(R.id.txtEmail)
    AppCompatEditText txtEmail;
    @Bind(R.id.txtPassword)
    AppCompatEditText txtPassword;
    @Bind(R.id.txtRepeatPassword)
    AppCompatEditText txtRepeatPassword;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Inject
    SignUp model;
    @Inject
    LuckyCaloriesApi api;
    @Inject
    User user;

    private Call<String> signUpCall;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.bind(this, layout);

        return layout;
    }

    @Override
    public void onDestroyView() {
        if(signUpCall != null) {
            signUpCall.cancel();
            signUpCall = null;
        }
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @OnFocusChange({R.id.txtName, R.id.txtEmail, R.id.txtPassword, R.id.txtRepeatPassword})
    public void onTextFocusChange(boolean focused){
        if(focused) //gain focus; we need only 'on lost'
            return;

        if(txtEmail == null || txtPassword == null)
            return;

        model.setName(txtName.getText().toString());
        model.setEmail(txtEmail.getText().toString());
        model.setPassword(txtPassword.getText().toString());
        model.setRepeatedPassword(txtRepeatPassword.getText().toString());
    }

    @OnClick(R.id.btnSignUp)
    public void signUp(){
        Transformer homeModelTransformer = new Transformer
                .Builder()
                .build(SignUpInfo.class);
        final SignUpInfo signUpInfo = homeModelTransformer.transform(model, SignUpInfo.class);

        signUpCall = api.signup(signUpInfo);
        progressBar.setVisibility(View.VISIBLE);

        final Dialog overlayDialog = new Dialog(getContext(), android.R.style.Theme_Panel); //display an invisible overlay dialog to prevent user interaction and pressing back
        overlayDialog.setCancelable(false);
        overlayDialog.show();

        signUpCall.enqueue(new Callback<String>() {

            WeakReference<SignUpFragment> fragmentReference
                    = new WeakReference<>(SignUpFragment.this);

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                user.setName(signUpInfo.getName());
                user.setEmail(signUpInfo.getEmail());
                user.setAccessToken(response.body());

                overlayDialog.dismiss();

                SignUpFragment fragment = fragmentReference.get();
                if(fragment != null){
                    fragment.startActivity(new Intent(fragment.getActivity(), CaloriesActivity.class));
                    fragment.getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                overlayDialog.dismiss();
                progressBar.setVisibility(View.INVISIBLE);
                Timber.e(t, "Error on sign up.");
            }
        });
    }
}

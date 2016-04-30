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
import com.hardskygames.luckycalories.admin.AdminActivity;
import com.hardskygames.luckycalories.launch.events.ShowSignUpScreen;
import com.hardskygames.luckycalories.launch.models.Login;
import com.hardskygames.luckycalories.main.MainActivity;
import com.hardskygames.luckycalories.users.models.UserModel;
import com.mobandme.android.transformer.Transformer;
import com.squareup.otto.Bus;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import io.swagger.client.api.LuckyCaloriesApi;
import io.swagger.client.model.AuthInfo;
import io.swagger.client.model.LoginInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment {

    @Bind(R.id.txtEmail)
    AppCompatEditText txtEmail;
    @Bind(R.id.txtPassword)
    AppCompatEditText txtPassword;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Inject
    Login model;
    @Inject
    Bus bus;
    @Inject
    LuckyCaloriesApi api;
    @Inject
    UserModel user;

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

    @OnClick(R.id.btnLogin)
    public void logIn(){
        LoginInfo info = new LoginInfo();
        info.setEmail(txtEmail.getText().toString());
        info.setPassword(txtPassword.getText().toString());

        progressBar.setVisibility(View.VISIBLE);
        final Dialog overlayDialog = new Dialog(getContext(), android.R.style.Theme_Panel); //display an invisible overlay dialog to prevent user interaction and pressing back
        overlayDialog.setCancelable(false);
        overlayDialog.show();

        final WeakReference<LoginFragment> fragmentReference
                = new WeakReference<>(LoginFragment.this);

        api.login(info).enqueue(new Callback<AuthInfo>() {
            @Override
            public void onResponse(Call<AuthInfo> call, Response<AuthInfo> response) {
                if(response.isSuccessful()){
                    AuthInfo info = response.body();
                    user.setId(info.getUser().getId());
                    user.setName(info.getUser().getName());
                    user.setEmail(info.getUser().getEmail());
                    user.setUserType(info.getUser().getUserType());
                    user.setDailyCalories(info.getUser().getDailyCalories());
                    user.setAccessToken(info.getAccessToken());

                    LoginFragment fragment = fragmentReference.get();
                    if(fragment != null){
                        switch (user.getUserType()){
                            case UserModel.USER:
                                overlayDialog.cancel();
                                fragment.startActivity(new Intent(fragment.getActivity(), MainActivity.class));
                                fragment.getActivity().finish();
                                return;
                            case UserModel.MANGER:
                            case UserModel.ADMIN:
                                overlayDialog.cancel();
                                fragment.startActivity(new Intent(fragment.getActivity(), AdminActivity.class));
                                fragment.getActivity().finish();
                                return;
                            default:
                                Timber.e("Login error: unknown user type.");
                        }
                    }
                }

                progressBar.setVisibility(View.INVISIBLE);
                overlayDialog.cancel();

            }

            @Override
            public void onFailure(Call<AuthInfo> call, Throwable t) {
                Timber.e(t, "Error on login.");

                overlayDialog.dismiss();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}

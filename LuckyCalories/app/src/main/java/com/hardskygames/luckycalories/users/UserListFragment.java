package com.hardskygames.luckycalories.users;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hardskygames.luckycalories.BaseActivity;
import com.hardskygames.luckycalories.BaseFragment;
import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.admin.AdminActivity;
import com.hardskygames.luckycalories.common.EndlessRecyclerOnScrollListener;
import com.hardskygames.luckycalories.main.MainActivity;
import com.hardskygames.luckycalories.users.models.AdminContext;
import com.hardskygames.luckycalories.users.models.UserModel;
import com.hardskygames.luckycalories.users.events.EditUserEvent;
import com.mobandme.android.transformer.Transformer;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.swagger.client.api.LuckyCaloriesApi;
import io.swagger.client.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends BaseFragment {

    @Inject
    LuckyCaloriesApi api;
    @Inject
    Bus bus;
    @Inject
    UserModel user;
    @Inject
    AdminContext adminContext;

    @Bind(R.id.listUsers)
    RecyclerView listLayout;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private String lastName = null;
    private Call<List<User>> callList;
    private LinearLayoutManager layoutManager;

    private SortedList<UserModel> usersList;
    private UsersAdapter usersAdapter;
    private EndlessRecyclerOnScrollListener scrollListener;
    private Transformer usersTransformer;

    public UserListFragment() {
        // Required empty public constructor

        usersTransformer = new Transformer
                .Builder()
                .build(io.swagger.client.model.User.class);

        usersAdapter = new UsersAdapter();
        usersList = new SortedList<>(UserModel.class,
                new SortedListAdapterCallback<UserModel>(usersAdapter) {
                    @Override
                    public int compare(UserModel u1, UserModel u2) {
                        if(u1.getName() == null)
                            return -1;

                        if(u2.getName() == null)
                            return 1;

                        return u1.getName().compareTo(u2.getName());
                    }

                    @Override
                    public boolean areContentsTheSame(UserModel oldItem, UserModel newItem) {
                        return oldItem.getName().equals(newItem.getName());
                    }

                    @Override
                    public boolean areItemsTheSame(UserModel item1, UserModel item2) {
                        return item1.getId() == item2.getId();
                    }
                });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_user_list, container, false);
        ButterKnife.bind(this, layout);

        layoutManager = new LinearLayoutManager(getActivity());
        listLayout.setLayoutManager(layoutManager);

        listLayout.setAdapter(usersAdapter);

        scrollListener = new EndlessRecyclerOnScrollListener(layoutManager, 5) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadPage();
            }
        };

        loadPage();

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
        listLayout.addOnScrollListener(scrollListener);
        bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();

        listLayout.removeOnScrollListener(scrollListener);
        if(callList != null){
            callList.cancel();
            callList = null;
        }
    }

    private void loadPage() {
        progressBar.setVisibility(View.VISIBLE);
        callList = api.getUserList(lastName);

        listLayout.removeOnScrollListener(scrollListener);

        callList.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful()) {
                    List<io.swagger.client.model.User> list = response.body();

                    if (list != null && !list.isEmpty()) {

                        for (io.swagger.client.model.User respCl : list) {
                            usersList.add(usersTransformer.transform(respCl, UserModel.class));
                        }

                        lastName = usersList.get(usersList.size() - 1).getName();
                    }
                }
                else{
                    Timber.e("Error on request users list: %s", response.errorBody());
                }

                progressBar.setVisibility(View.INVISIBLE);
                listLayout.addOnScrollListener(scrollListener);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Timber.e(t, "Error on request users list.");
                progressBar.setVisibility(View.INVISIBLE);
                listLayout.addOnScrollListener(scrollListener);
            }
        });
    }

    private void openUserEditDlg(UserModel model){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        EditUserFragment editUserFragment = new EditUserFragment();
        ((BaseActivity)getActivity()).inject(editUserFragment);
        if(model != null){
            editUserFragment.setModel(model);
            editUserFragment.setIndex(usersList.indexOf(model));
        }
        editUserFragment.show(fm, "fragment_edit_user");
    }

    // create/edit
    @Subscribe
    public void onUserEdit(EditUserEvent ev){
        UserModel model = ev.model;
        ev.model = null;

        if(model.getId() == 0){//new
            progressBar.setVisibility(View.VISIBLE);
            listLayout.removeOnScrollListener(scrollListener);
            api.createUser(usersTransformer.transform(model, User.class)).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.isSuccessful()){
                        usersList.add(usersTransformer.transform(response.body(), UserModel.class));
                    }else{
                        Timber.e("Error on create user: %s", response.errorBody());
                    }

                    progressBar.setVisibility(View.INVISIBLE);
                    listLayout.addOnScrollListener(scrollListener);
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Timber.e(t, "Error on create user.");
                    progressBar.setVisibility(View.INVISIBLE);
                    listLayout.addOnScrollListener(scrollListener);
                }
            });
        }
        else{
            usersList.recalculatePositionOfItemAt(ev.position);
            usersList.updateItemAt(usersList.indexOf(model), model);
            api.updateUser(usersTransformer.transform(model, User.class)).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(!response.isSuccessful()){
                        Timber.e("Error on update user: %s", response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Timber.e(t, "Error on update user.");
                }
            });
        }
    }

    @OnClick(R.id.btnAdd)
    public void addUser(){
        openUserEditDlg(null);
    }

    class UsersAdapter extends RecyclerView.Adapter<UserViewHolder>{

        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_user_item, parent, false);
            return new UserViewHolder(v);
        }

        @Override
        public void onBindViewHolder(UserViewHolder holder, int position) {
            holder.setData(usersList.get(position));
        }

        @Override
        public int getItemCount() {
            return usersList.size();
        }
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        TextView txtName;
        TextView txtEmail;
        TextView txtDailyCalorie;

        Button btnEdit;
        Button btnDelete;
        Button btnImpersonate;

        private UserModel userModel;

        public UserViewHolder(View itemView) {
            super(itemView);

            txtName = ButterKnife.findById(itemView, R.id.txtName);
            txtEmail = ButterKnife.findById(itemView, R.id.txtEmail);
            txtDailyCalorie = ButterKnife.findById(itemView, R.id.txtDailyCalorie);

            btnEdit = ButterKnife.findById(itemView, R.id.btnEdit);
            btnDelete = ButterKnife.findById(itemView, R.id.btnDelete);

            btnImpersonate = ButterKnife.findById(itemView, R.id.btnImpersonate);
        }

        public void setData(final UserModel data){
            this.userModel = data;

            txtName.setText(data.getName());
            txtEmail.setText(data.getEmail());
            txtDailyCalorie.setText(String.format("%d", data.getDailyCalories()));

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openUserEditDlg(userModel);
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usersList.remove(userModel);
                    api.deleteUser(userModel.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(!response.isSuccessful()){
                                Timber.e("Error on delete user: %s", response.errorBody());
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Timber.e(t, "Error on delete user.");
                        }
                    });
                }
            });

            if(user.isAdmin() && this.userModel.isUser()){
                btnImpersonate.setVisibility(View.VISIBLE);
                btnImpersonate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        UserModel adminCopy = new UserModel();

                        adminCopy.setId(user.getId());
                        adminCopy.setName(user.getName());
                        adminCopy.setEmail(user.getEmail());
                        adminCopy.setUserType(user.getUserType());
                        adminCopy.setDailyCalories(user.getDailyCalories());

                        //access token is still from admin, that we can identify admin on server

                        adminContext.setAdminModel(adminCopy);

                        user.setId(userModel.getId());
                        user.setName(userModel.getName());
                        user.setEmail(userModel.getEmail());
                        user.setUserType(userModel.getUserType());
                        user.setDailyCalories(userModel.getDailyCalories());

                        startActivity(new Intent(getActivity(), MainActivity.class));
                    }
                });
            }
            else{
                btnImpersonate.setVisibility(View.INVISIBLE);
                btnImpersonate.setOnClickListener(null);
            }
        }
    }

}

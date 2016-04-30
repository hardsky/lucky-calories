package com.hardskygames.luckycalories.users;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hardskygames.luckycalories.BaseFragment;
import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.models.UserModel;
import com.squareup.otto.Bus;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.swagger.client.api.LuckyCaloriesApi;
import io.swagger.client.model.User;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends BaseFragment {

    @Inject
    LuckyCaloriesApi api;
    @Inject
    Bus bus;

    @Bind(R.id.listUsers)
    RecyclerView listLayout;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private Call<List<User>> callList;
    private LinearLayoutManager layoutManager;

    SortedList<UserModel> usersList;

    public UserListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_user_list, container, false);
        ButterKnife.bind(this, layout);

        layoutManager = new LinearLayoutManager(getActivity());
        listLayout.setLayoutManager(layoutManager);

        return layout;
    }

    @Override
    public void onPause() {
        super.onPause();

        if(callList != null){
            callList.cancel();
            callList = null;
        }
    }

    class UsersAdapter extends RecyclerView.Adapter<UserViewHolder>{

        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(UserViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        public UserViewHolder(View itemView) {
            super(itemView);
        }
    }

}

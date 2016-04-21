package com.hardskygames.luckycalories;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 21.04.16.
 */
public class BaseFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        inject(this);
    }

    public void inject(Object obj) {
        ((BaseActivity) getActivity()).inject(obj);
    }
}

package com.guyaning.media.mediaplayer01.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guyaning.media.mediaplayer01.base.BasePager;


/**
 * Created by Administrator on 2017/2/28.
 */

public class ReplaceFragment extends Fragment {

    private BasePager currPager;

    public ReplaceFragment(BasePager pager) {
        this.currPager = pager;
    }

    public ReplaceFragment(){

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return currPager.rootView;
    }

}

/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.mmc.view;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.sessionm.mmc.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BaseScrollAndRefreshFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ObservableScrollViewCallbacks {

    //Image position
    private int position = 0;
    //Scroll list methods to show/hide tool bar
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            if (scrollState == ScrollState.UP) {
                if (ab.isShowing()) {
                    ab.hide();
                }
            } else if (scrollState == ScrollState.DOWN) {
                if (!ab.isShowing()) {
                    ab.show();
                }
            }
        }
    }

    @Override
    public void onRefresh() {
    }

    protected void popUpImageDialog(final List<String> urls) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                position = 0;
            }
        });
        AlertDialog dialog = builder.create();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_image, null);
        final ImageView imageView = (ImageView) dialogLayout.findViewById(R.id.dialog_imageview);
        Picasso.with(getActivity())
                .load(urls.get(0))
                .resize(1280, 800)
                .onlyScaleDown()
                .centerInside()
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (urls.size() > 1) {
                    if (position < urls.size() - 1)
                        position += 1;
                    else
                        position = 0;
                    Picasso.with(getActivity())
                            .load(urls.get(position))
                            .resize(1280, 800)
                            .onlyScaleDown()
                            .centerInside()
                            .into(imageView);
                }
            }
        });

        dialog.setView(dialogLayout);
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                position = 0;
            }
        });

        dialog.show();
    }
}

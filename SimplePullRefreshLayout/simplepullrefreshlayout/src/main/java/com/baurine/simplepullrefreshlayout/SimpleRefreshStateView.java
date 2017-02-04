package com.baurine.simplepullrefreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by baurine on 2/4/17.
 */

public class SimpleRefreshStateView
        extends RelativeLayout
        implements SimplePullRefreshLayout.RefreshStateView {

    private boolean isRefreshing = false;
    private static final float PERCENT_THRESHOLD = 1.0f;

    @Override
    public void setPercent(float percent) {
        if (!isRefreshing) {
            int curState = lastState;
            if (percent >= PERCENT_THRESHOLD) {
                curState = STATE_TO_RELEASE;
            } else if (lastState == STATE_TO_RELEASE) {
                curState = STATE_BACK_TO_PULL;
            }
            if (curState != lastState) {
                changeState(curState);
            }
        } else {
            if (percent == 0.0f) {
                setRefreshing(false);
            }
        }
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        isRefreshing = refreshing;
        changeState(refreshing ? STATE_LOADING : STATE_PULL_INIT);
    }

    ////////////////////////////////////////////////////////////
    private final static int STATE_PULL_INIT = 0;
    private final static int STATE_TO_RELEASE = 1;
    private final static int STATE_BACK_TO_PULL = 2;
    private final static int STATE_LOADING = 3;
    private int lastState = STATE_PULL_INIT;

    private void changeState(int state) {
        lastState = state;
        switch (state) {
            case STATE_PULL_INIT:
                tvState.setText(R.string.pull_refresh);
                ivArrow.setRotation(0f);
                ivArrow.setVisibility(View.VISIBLE);
                break;
            case STATE_TO_RELEASE:
                tvState.setText(R.string.release_refresh);
                ivArrow.setRotation(0f);
                ivArrow.animate().rotation(180f).setDuration(150);
                break;
            case STATE_BACK_TO_PULL:
                tvState.setText(R.string.pull_refresh);
                ivArrow.setRotation(180f);
                ivArrow.animate().rotation(360f).setDuration(150);
                break;
            case STATE_LOADING:
                tvState.setText(R.string.loading);
                ivArrow.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    ////////////////////////////////////////////////////////////
    private TextView tvState;
    private ImageView ivArrow;

    public SimpleRefreshStateView(Context context) {
        this(context, null);
    }

    public SimpleRefreshStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_refresh_state, this);
        tvState = (TextView) findViewById(R.id.tv_state);
        ivArrow = (ImageView) findViewById(R.id.iv_arrow);
    }
}

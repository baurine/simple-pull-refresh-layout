package com.baurine.simplepullrefreshlayoutsample;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import com.baurine.multitypeadapter.MultiTypeAdapter;

/**
 * Created by baurine on 2/1/17.
 */

public class ImageItem implements MultiTypeAdapter.IItem {
    @Override
    public int getType() {
        return R.layout.item_image;
    }

    @Override
    public int getVariableId() {
        return BR.item;
    }

    /////////////////////////////////////////
    public final int imageResId;
    private final int colorResId;
    private ColorDrawable colorDrawable;

    public ImageItem(int colorResId, int imageResId) {
        this.colorResId = colorResId;
        this.imageResId = imageResId;
    }

    public ColorDrawable getColorDrawable(Context context) {
        if (colorDrawable == null) {
            colorDrawable = new ColorDrawable(context.getResources().getColor(colorResId));
        }
        return colorDrawable;
    }
}

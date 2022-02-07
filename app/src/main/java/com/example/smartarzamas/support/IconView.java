package com.example.smartarzamas.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class IconView extends androidx.appcompat.widget.AppCompatImageView {
    public IconView(@NonNull Context context) {
        super(context);
    }

    public IconView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IconView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        bm = Utils.getRoundBitmap(bm);
        super.setImageBitmap(bm);
    }
}

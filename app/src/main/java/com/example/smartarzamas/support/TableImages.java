package com.example.smartarzamas.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.BitSet;

public class TableImages extends GridLayout {

    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private LayoutParams imageParams;
    private final int COLUMNS_COUNT = 2;


    public TableImages(Context context) {
        super(context);
        setColumnCount(COLUMNS_COUNT);
    }

    public TableImages(Context context, AttributeSet attrs) {
        super(context, attrs);
        setColumnCount(COLUMNS_COUNT);
    }

    public void setBitmaps(ArrayList<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
        if (bitmaps.size() > 0){

            if (bitmaps.size() == 9)
                Log.e("pre", String.valueOf(getWidth()));

            fillTable();

            if (bitmaps.size() == 9)
                Log.e("post", String.valueOf(getWidth()));

        }
    }
    private void fillTable(){
        ArrayList<ImageView> imageViews = new ArrayList<>();
        for (Bitmap b : bitmaps) {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageBitmap(b);
            imageViews.add(imageView);
        }
        for (ImageView i : imageViews){
            addView(i);
        }

    }

    public void removeBitmaps(){
        removeAllViews();
    }


}

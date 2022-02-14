package com.example.smartarzamas.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.contentcapture.ContentCaptureCondition;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.smartarzamas.R;

import java.util.ArrayList;
import java.util.BitSet;

public class TableImages extends GridLayout {

    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private int width;
    private final int COLUMNS_COUNT = 2;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;
    public static final int LEFT = 3;



    public TableImages(Context context) {
        super(context);
        setColumnCount(COLUMNS_COUNT);
    }

    public TableImages(Context context, AttributeSet attrs) {
        super(context, attrs);
        setColumnCount(COLUMNS_COUNT);
    }

    public void setBitmaps(ArrayList<Bitmap> bitmaps, int type) {
        if (type == CENTER){
            width = (int) (getRootView().getWidth() - (getResources().getDimension(R.dimen.margin_system_message) * 2));
        }
        else if (type == RIGHT){
            width = (int) (getRootView().getWidth() - getResources().getDimension(R.dimen.margin_user_message));
        }
        else if (type == LEFT){
            width = (int) (getRootView().getWidth() - getResources().getDimension(R.dimen.margin_user_message) - getResources().getDimension(R.dimen.icon_message));
        }
        getLayoutParams().width = width;

        this.bitmaps = bitmaps;
        if (bitmaps.size() > 0){
            if (bitmaps.size() == 9)
                Log.e("pre", String.valueOf(getWidth()));


            ArrayList<ImageView> imageViews = new ArrayList<>();

            for (Bitmap b : bitmaps) {
                ImageView imageView = new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(width/COLUMNS_COUNT, width/COLUMNS_COUNT));
                imageView.setImageBitmap(b);
                imageViews.add(imageView);
            }
            for (ImageView i : imageViews){
                addView(i);
            }



            if (bitmaps.size() == 9)
                Log.e("post", String.valueOf(getWidth()));
        }
    }

    public void removeBitmaps(){
        getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        removeAllViews();
    }


}

package com.example.smartarzamas.support;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.smartarzamas.R;

import java.util.ArrayList;

public class TableMessageImages extends GridLayout {

    public static final int SYSTEM_MESSAGE = 1;
    public static final int MY_MESSAGE = 2;
    public static final int NOT_MY_MESSAGE = 3;

    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private int width;
    private int type;
    private int columnCount = 2;



    public TableMessageImages(Context context) {
        super(context);
        setColumnCount(columnCount);
    }

    public TableMessageImages(Context context, AttributeSet attrs) {
        super(context, attrs);
        setColumnCount(columnCount);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TableMessageImages);

        type = a.getInt(0, 1);
        a.recycle();


    }

    public void setBitmaps(ArrayList<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
        if (bitmaps.size() > 0){

            // hard set image width
            // overwrite when message layout will be exists
            /*
            if (type == SYSTEM_MESSAGE)
                width = (int) (getRootView().getWidth() - (getResources().getDimension(R.dimen.margin_system_message) * 2));
            else if (type == MY_MESSAGE)
                width = (int) (getRootView().getWidth() - getResources().getDimension(R.dimen.margin_user_message));
            else if (type == NOT_MY_MESSAGE)
                width = (int) (getRootView().getWidth() - getResources().getDimension(R.dimen.margin_user_message) - getResources().getDimension(R.dimen.icon_message));


             */
            getLayoutParams().width = 100;// width;

            ArrayList<ImageView> imageViews = new ArrayList<>();

            for (Bitmap b : bitmaps) {
                ImageView imageView = new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(width/columnCount, width/columnCount));
                imageView.setImageBitmap(b);
                imageViews.add(imageView);
            }
            for (ImageView i : imageViews){
                addView(i);
            }


        }
    }

    public void removeBitmaps(){
        getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        removeAllViews();
    }


}

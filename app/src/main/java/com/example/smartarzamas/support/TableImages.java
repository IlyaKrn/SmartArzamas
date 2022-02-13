package com.example.smartarzamas.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.BitSet;

public class TableImages extends TableLayout {

    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private LayoutParams imageParams;


    public TableImages(Context context) {
        super(context);
    }

    public TableImages(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setBitmaps(ArrayList<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
        if (bitmaps.size() > 0){
            fillTable();
            setVisibility(VISIBLE);
            setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

    }
    private void fillTable(){
        ArrayList<TableRow> tableRows = new ArrayList<>();
        for (int i = 0; i < bitmaps.size(); i+=2) {
            TableRow row = new TableRow(getContext());
            row.setLayoutParams(new TableRow.LayoutParams(getWidth(), getWidth()/2));
            tableRows.add(row);
        }

        ArrayList<ImageView> images = new ArrayList<>();
        for (TableRow r : tableRows) {
            for (int j = 0; j < 2; j++) {
                ImageView img = new ImageView(getContext());
                img.setLayoutParams(new TableRow.LayoutParams(getWidth()/2, getWidth()/2));
                images.add(img);
                r.addView(img);
            }
        }
        for (int i = 0; i < bitmaps.size(); i++) {
            images.get(i).setImageBitmap(bitmaps.get(i));
        }
        for (TableRow r : tableRows){
            addView(r);
        }


    }


}

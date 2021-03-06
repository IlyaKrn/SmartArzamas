package com.example.smartarzamas.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smartarzamas.R;
import com.example.smartarzamas.firebaseobjects.Locate;
import com.example.smartarzamas.firebaseobjects.OnGetIcon;
import com.example.smartarzamas.firebaseobjects.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final User user; // пользователь
    private final Context context; // контекст
    private final ArrayList<Locate> locates; // список меток
    private Map<Locate, Bitmap> cache; // кэш картинок
    // разметка
    private final View rootView;
    private TextView title;
    private TextView snippet;
    private ImageView icon;
    private ProgressBar progress;
    private FrameLayout ivContainer;


    //конструктор
    public MapInfoWindowAdapter(Context context, User user, ArrayList<Locate> locates) {
        this.user = user;
        this.context = context;
        this.rootView = LayoutInflater.from(context).inflate(R.layout.map_info_window, null);
        this.locates = locates;
    }

    // загрузка кэша
    public void loadCache(Map<Locate, Bitmap> c){
        this.cache = c;
    }

    // установка данных
    private void setData(Marker marker){
        title = rootView.findViewById(R.id.tv_title);
        snippet = rootView.findViewById(R.id.tv_snippet);
        icon = rootView.findViewById(R.id.icon);
        progress = rootView.findViewById(R.id.progress);
        ivContainer = rootView.findViewById(R.id.ll_iv_container);
        ivContainer.getLayoutParams().width = ivContainer.getHeight();

        for(Locate l : locates){
            if (marker.getPosition().equals(l.locate())){
                title.setText(l.name);
                snippet.setText(l.description);
                if (cache != null && cache.get(l) != null)
                    icon.setImageBitmap(cache.get(l));
            }
        }
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        setData(marker);
        return rootView;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        setData(marker);
        return rootView;
    }
}

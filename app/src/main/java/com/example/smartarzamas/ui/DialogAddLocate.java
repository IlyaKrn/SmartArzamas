package com.example.smartarzamas.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smartarzamas.R;
import com.example.smartarzamas.firebaseobjects.FirebaseObject;
import com.example.smartarzamas.firebaseobjects.OnGetDataListener;
import com.example.smartarzamas.firebaseobjects.OnSetIcon;
import com.example.smartarzamas.support.Utils;
import com.example.smartarzamas.support.Category;
import com.example.smartarzamas.firebaseobjects.Locate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class DialogAddLocate extends Dialog {

    private Button addLocate, cancel, setIcon;
    private EditText etName, etDescription;
    private TextView tvCategory, tvNameErr, tvCategoryErr, tvDescriptionErr;
    private double longitude, latitude;
    private ImageView icon;
    private Bitmap bitmap;

    public DialogAddLocate(AppCompatActivity activity, double longitude, double latitude) {
        super(activity);
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public DialogAddLocate(Fragment fragment, double longitude, double latitude) {
        super(fragment);
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_add_locate, container, false);
        addLocate = rootView.findViewById(R.id.bt_create);
        cancel = rootView.findViewById(R.id.bt_cancel);
        etName = rootView.findViewById(R.id.et_locate_name);
        etDescription = rootView.findViewById(R.id.et_locate_description);
        tvNameErr = rootView.findViewById(R.id.tv_locate_name_err);
        tvCategoryErr = rootView.findViewById(R.id.tv_category_err);
        tvDescriptionErr = rootView.findViewById(R.id.tv_description_err);
        tvNameErr = rootView.findViewById(R.id.tv_locate_name_err);
        tvCategory = rootView.findViewById(R.id.tv_locate_category);
        setIcon = rootView.findViewById(R.id.bt_set_icon);
        icon = rootView.findViewById(R.id.icon);

        setIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        addLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                String description = etDescription.getText().toString();
                String cat = tvCategory.getText().toString();

                if (!name.equals("")){
                    if (!cat.equals("")){
                        freeze();
                        String id = Locate.getDatabase().push().getKey();
                        if (description.equals("")) {
                            description = "Описание";
                        }

                        Locate.getDatabase().child(id).setValue(new Locate(name, id, longitude, latitude, description, cat)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (bitmap != null) {
                                    Locate.getLocateById(id, new OnGetDataListener<Locate>() {
                                        @Override
                                        public void onGetData(Locate data) {
                                            data.setIconAsync(getActivity().getApplicationContext(), bitmap, new OnSetIcon() {
                                                @Override
                                                public void onSet(String ref, Bitmap bitmap) {
                                                    destroy();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onVoidData() {

                                        }

                                        @Override
                                        public void onNoConnection() {

                                        }

                                        @Override
                                        public void onCanceled() {

                                        }
                                    });
                                }
                                else {
                                    destroy();
                                }
                            }
                        });
                    }
                    else {
                        Utils.showWarning(tvCategoryErr, R.string.enter_category);
                    }
                }
                else {
                    Utils.showWarning(tvNameErr, R.string.enter_locate_name);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destroy();
            }
        });
        tvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, view);
                popup.inflate(R.menu.popup_menu_map);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            // ямы на дорогах
                            case R.id.pits_on_roads:
                                tvCategory.setText(Category.PITS_ON_ROADS);
                                break;
                            // лужи
                            case R.id.puddles:
                                tvCategory.setText(Category.PUDDLES);
                                break;
                            // достопримечатльности
                            case R.id.sights:
                                tvCategory.setText(Category.SIGHTS);
                                break;
                            // другое
                            case R.id.other:
                                tvCategory.setText(Category.OTHER);
                                break;
                            // другое
                            case R.id.snow:
                                tvCategory.setText(Category.SNOW);
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(String.valueOf(rootView.getHeight()), String.valueOf(rootView.getWidth()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getData() != null && requestCode == 1){
            rootView.findViewById(R.id.iv).setVisibility(View.VISIBLE);
            icon.setImageURI(data.getData());
            bitmap = ((BitmapDrawable) icon.getDrawable()).getBitmap();
            icon.setImageBitmap(Utils.compressBitmapToIcon(bitmap, FirebaseObject.ICON_QUALITY));
        }
    }
}

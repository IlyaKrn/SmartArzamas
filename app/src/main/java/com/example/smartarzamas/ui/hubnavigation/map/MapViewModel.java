package com.example.smartarzamas.ui.hubnavigation.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartarzamas.support.Tag;

import java.util.ArrayList;

public class MapViewModel extends ViewModel {

    private MutableLiveData<String> search;
    private MutableLiveData<ArrayList<String>> category;

    public MapViewModel() {
        category = new MutableLiveData<>();
        search = new MutableLiveData<>();
        search.setValue("");
        category.setValue(Tag.getAllTags());

    }

    public LiveData<String> getSearch() {
        return search;
    }
    public LiveData<ArrayList<String>> getCategory() {
        return category;
    }

    public void setSearch(String search) {
        this.search.setValue(search);
    }

    public void setCategory(ArrayList<String> category) {
        this.category.setValue(category);
    }
}
package com.example.smartarzamas.ui.hubnavigation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartarzamas.support.Category;

import java.util.ArrayList;

public abstract class CommonNavigationViewModel extends ViewModel {

    protected MutableLiveData<String> search;
    protected MutableLiveData<ArrayList<String>> category;


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

    public CommonNavigationViewModel() {
        category = new MutableLiveData<>();
        search = new MutableLiveData<>();
        search.setValue("");
        category.setValue(Category.getAllTags());

    }

}

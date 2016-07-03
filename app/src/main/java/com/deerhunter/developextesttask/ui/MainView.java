package com.deerhunter.developextesttask.ui;

import com.deerhunter.developextesttask.model.Page;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

public interface MainView extends MvpView {
    void showToast(String text);
    void setStartedState();
    void setStoppedState();
    void setListData(List<Page> pages);
    void notifyListChanged();
}

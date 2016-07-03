package com.deerhunter.developextesttask;

import com.deerhunter.developextesttask.model.Page;

import java.util.List;

public interface PagesProcessListener {
    void onPagesProcessed(List<Page> pages);
    void onPageStatusChanged(Page page);
}
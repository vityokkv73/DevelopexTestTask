package com.deerhunter.developextesttask.ui;

import android.text.TextUtils;

import com.deerhunter.developextesttask.PagesProcessListener;
import com.deerhunter.developextesttask.network.TextUtil;
import com.deerhunter.developextesttask.model.Page;
import com.deerhunter.developextesttask.network.NetworkCommunicator;
import com.deerhunter.developextesttask.network.WebPagesScanner;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import rx.android.schedulers.AndroidSchedulers;

public class MainViewPresenter extends MvpBasePresenter<MainView> implements PagesProcessListener {
    private static final String TAG = MainViewPresenter.class.getSimpleName();

    private int loadedPageSize;
    private int maxPagesToScan;
    private int maxThreadsCount;
    private String phrase;
    private List<Page> pages = new ArrayList<>();
    private WebPagesScanner webPagesScanner;

    public void startButtonClicked(String startUrl, String phrase, String maxThreadCountText, String maxPagesToScanText) {
        if (!isAllParametersValid(startUrl, phrase, maxThreadCountText, maxPagesToScanText)) return;

        loadedPageSize = 0;
        pages.clear();
        pages.add(new Page(startUrl));

        initializeViewState();
        startScanning(phrase);
    }

    private boolean isAllParametersValid(String startUrl, String phrase, String maxThreadCountText, String maxPagesToScanText) {
        if (!TextUtil.isHtmlUrl(startUrl)) {
            getView().showToast("url should start from http://");
            return false;
        }

        if (TextUtils.isEmpty(phrase)) {
            getView().showToast("phrase is empty");
            return false;
        } else {
            this.phrase = phrase;
        }

        try {
            maxThreadsCount = Integer.parseInt(maxThreadCountText);
            if (maxThreadsCount > 20) {
                getView().showToast("Max threads count is 20");
                return false;
            }
        } catch (NumberFormatException ex) {
            getView().showToast("Invalid threads count");
            return false;
        }

        try {
            maxPagesToScan = Integer.parseInt(maxPagesToScanText);
        } catch (NumberFormatException ex) {
            getView().showToast("Invalid max pages count");
            return false;
        }
        return true;
    }

    private void initializeViewState() {
        getView().setListData(pages);
        getView().notifyListChanged();
        getView().setStartedState();
    }

    private void startScanning(String phrase) {
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreadsCount);
        webPagesScanner = new WebPagesScanner(new NetworkCommunicator(), threadPool, AndroidSchedulers.mainThread(), this);
        webPagesScanner.processPages(pages, phrase);
    }

    private void pagesProcessed(List<Page> processedPages) {
        loadedPageSize = pages.size();
        if (loadedPageSize == maxPagesToScan) {
            if (isViewAttached()) {
                getView().showToast("Max Scanned Pages Count Reached");
                getView().setStoppedState();
            }
        } else {
            List<Page> newPagesToProcess = generateStubPagesFromLinks(processedPages);
            if (!newPagesToProcess.isEmpty()) {
                addNewPages(pages, newPagesToProcess, maxPagesToScan);
                int lastPageToScan = (pages.size() > maxPagesToScan) ? maxPagesToScan : pages.size();
                webPagesScanner.processPages(pages.subList(loadedPageSize, lastPageToScan), phrase);
            } else {
                if (isViewAttached()) {
                    getView().showToast("No pages to process");
                    getView().setStoppedState();
                }
            }
        }
    }

    private void addNewPages(List<Page> pages, List<Page> pagesToAdd, int maxListSize) {
        if (pages.size() >= maxListSize)
            return;

        for (Page page : pagesToAdd) {
            if (!pages.contains(page)) {
                pages.add(page);
                if (pages.size() == maxListSize) {
                    break;
                }
            }
        }
    }

    private List<Page> generateStubPagesFromLinks(List<Page> processedPages) {
        List<Page> stubPages = new ArrayList<>();
        for (Page page : processedPages) {
            for (String url : page.links) {
                stubPages.add(new Page(url));
            }
        }
        return stubPages;
    }

    @Override
    public void onPagesProcessed(List<Page> pages) {
        pagesProcessed(pages);
        if (isViewAttached()) {
            getView().notifyListChanged();
        }
    }

    @Override
    public void onPageStatusChanged(Page page) {
        if (isViewAttached()) {
            getView().notifyListChanged();
        }
    }

    public void stopButtonClicked() {
        getView().setStoppedState();
        webPagesScanner.stopPageProcessing();
    }
}

package com.deerhunter.developextesttask.network;

import android.util.Log;

import com.deerhunter.developextesttask.PagesProcessListener;
import com.deerhunter.developextesttask.model.Page;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WebPagesScanner {
    private static final String TAG = WebPagesScanner.class.getSimpleName();

    private PagesProcessListener listener;
    private NetworkCommunicator networkCommunicator;
    private Scheduler observeOnScheduler;
    private Scheduler scheduler;
    private Subscription subscription;
    private ThreadPoolExecutor threadPool;

    public WebPagesScanner(NetworkCommunicator networkCommunicator, ThreadPoolExecutor threadPool, Scheduler observeOnScheduler, PagesProcessListener listener) {
        this.networkCommunicator = networkCommunicator;
        this.threadPool = threadPool;
        this.scheduler = Schedulers.from(threadPool);
        this.observeOnScheduler = observeOnScheduler;
        this.listener = listener;
    }

    public void processPages(List<Page> pages, final String phrase) {
        subscription = Observable.from(pages).flatMap(new Func1<Page, Observable<Page>>() {
            @Override
            public Observable<Page> call(Page page) {
                return Observable.just(page)
                        .subscribeOn(scheduler)
                        .map(new Func1<Page, Page>() {
                            @Override
                            public Page call(Page page) {
                                return downloadAndProcessPage(page, phrase, listener);
                            }
                        });
            }
        }).toList().observeOn(observeOnScheduler)
                .subscribe(new Action1<List<Page>>() {
                    @Override
                    public void call(List<Page> pages) {
                        listener.onPagesProcessed(pages);
                    }
                });
    }

    private Page downloadAndProcessPage(Page page, String phrase, PagesProcessListener listener) {
        Log.d(TAG, "page to process " + page + ". Thread = " + Thread.currentThread().getName());
        page.status = new Page.Status(Page.Status.State.LOADING, "");
        listener.onPageStatusChanged(page);
        if (networkCommunicator.isHtmlPage(page.url)) {
            try {
                Response response = networkCommunicator.loadWebPage(page.url);
                if (!response.isActual()) {
                    page.status = new Page.Status(Page.Status.State.ERROR, "can't load a page");
                } else if (response.getStatus() != 200) {
                    page.status = new Page.Status(Page.Status.State.ERROR, "responce code = " + response.getStatus());
                } else {
                    String content = new String(response.getResponse(), response.getContentEncoding());
                    page.status = new Page.Status(Page.Status.State.PARSING, "");
                    listener.onPageStatusChanged(page);
                    List<String> links = TextUtil.findLinks(content);
                    int phrasePos = content.indexOf(phrase);
                    page.contentSize = content.length();
                    page.links = links;
                    if ((phrasePos != -1)) {
                        page.status = new Page.Status(Page.Status.State.FOUND, "Found at pos " + phrasePos);
                    } else {
                        page.status = new Page.Status(Page.Status.State.NOT_FOUND, "");
                    }
                }
            } catch (IOException e) {
                page.status = new Page.Status(Page.Status.State.ERROR, e.toString());
            }
        } else {
            page.status = new Page.Status(Page.Status.State.ERROR, "Not an html page");
        }
        Log.d(TAG, page.toString());
        listener.onPageStatusChanged(page);
        return page;
    }

    public void stopPageProcessing() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        if (threadPool != null) {
            threadPool.shutdownNow();
        }
    }
}

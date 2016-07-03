package com.deerhunter.developextesttask.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.Toast;

import com.deerhunter.developextesttask.R;
import com.deerhunter.developextesttask.model.Page;
import com.deerhunter.developextesttask.ui.adapter.PageAdapter;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends MvpActivity<MainView, MainViewPresenter> implements MainView {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.start_url)
    AppCompatEditText startUrlEV;
    @BindView(R.id.max_threads_count)
    AppCompatEditText maxThreadCountEV;
    @BindView(R.id.max_pages_count)
    AppCompatEditText maxPagesCountEV;
    @BindView(R.id.phrase)
    AppCompatEditText phraseEV;
    @BindView(R.id.pages_list)
    RecyclerView pagesList;
    @BindView(R.id.start_button)
    Button startButton;
    @BindView(R.id.stop_button)
    Button stopButton;

    PageAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        pagesList.setLayoutManager(new LinearLayoutManager(this));
        pagesList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, R.drawable.list_divider));
        pageAdapter = new PageAdapter();
        pagesList.setAdapter(pageAdapter);
        setStoppedState();
    }

    @NonNull
    @Override
    public MainViewPresenter createPresenter() {
        return new MainViewPresenter();
    }

    @OnClick(R.id.start_button)
    public void startButtonClicked() {
        String startUrl = startUrlEV.getText().toString();
        String phrase = phraseEV.getText().toString();
        String maxThreadCountText = maxThreadCountEV.getText().toString();
        String maxPagesToScanText = maxPagesCountEV.getText().toString();
        getPresenter().startButtonClicked(startUrl, phrase, maxThreadCountText, maxPagesToScanText);
    }

    @OnClick(R.id.stop_button)
    public void stopButtonClicked() {
        getPresenter().stopButtonClicked();
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setStartedState() {
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    @Override
    public void setStoppedState() {
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    @Override
    public void setListData(List<Page> pages) {
        pageAdapter.setData(pages);
    }

    @Override
    public void notifyListChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pageAdapter.notifyDataSetChanged();

            }
        });
    }
}

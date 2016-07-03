package com.deerhunter.developextesttask.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deerhunter.developextesttask.R;
import com.deerhunter.developextesttask.model.Page;

import java.util.ArrayList;
import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageViewHolder> {
    private List<Page> pages = new ArrayList<>();

    public void setData(List<Page> pages) {
        this.pages = pages;
    }

    @Override
    public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_layout, parent, false);
        return new PageViewHolder(v, (TextView) v.findViewById(R.id.url), (TextView) v.findViewById(R.id.status));
    }

    @Override
    public void onBindViewHolder(PageViewHolder holder, int position) {
        Page page = pages.get(position);
        holder.pageUrlTV.setText(page.url);
        holder.pageStatusTV.setText("" + page.status);
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        TextView pageUrlTV;
        TextView pageStatusTV;

        PageViewHolder(View view, TextView pageUrlTV, TextView pageStatusTV) {
            super(view);
            this.pageUrlTV = pageUrlTV;
            this.pageStatusTV = pageStatusTV;
        }
    }
}

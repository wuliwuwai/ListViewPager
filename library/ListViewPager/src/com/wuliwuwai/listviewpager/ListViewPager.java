package com.wuliwuwai.listviewpager;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class ListViewPager {
    public interface PagerService {
        /**
         * 
         * @param page
         *            页码，通常情况下page++
         * @param finished
         *            当回调接口执行完成后，调用finished里的回调借口onFinished
         */
        public void getNext(int page, OnServiceFinished finished);

    }

    public interface OnServiceFinished {
        /**
         * 客户端获得数据后必须调用的方法
         */
        public void onFinished();
    }

    /**
     * 加载分页
     */
    private View             loadingView;
    /**
     * 当前页
     */
    private Integer          page            = 1;
    /**
     * 有更多
     */
    private Boolean          hasMore         = true;

    /**
     * 滑动事件
     */
    private OnScrollListener onScrollListener;

    /**
     * 可否继续加载标记，true为可以加载，false，不能加载
     */
    private AtomicBoolean    keepOnAppending = new AtomicBoolean(true);
    /**
     * 加载的ListView控件
     */
    private ListView         mListView;
    /**
     * 回调接口
     */
    private PagerService     pagerService;
    /**
     * 上下文对象
     */
    private Context          mContext;

    public ListViewPager(Context context, ListView listView,
            PagerService pagerService) {
        this(context, listView);
        this.setPagerService(pagerService);
    }

    public ListViewPager(Context context, ListView listView) {
        super();
        reSet();
        this.mContext = context;
        this.mListView = listView;
        loadingView = LayoutInflater.from(this.mContext).inflate(
                R.layout.view_loading_layout, null);
        this.mListView.addFooterView(loadingView);
        onScrollListener = new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {

                if (hasMore && keepOnAppending.get()) {
                    if (firstVisibleItem + visibleItemCount == totalItemCount) {
                        if (pagerService != null) {
                            final int pCount = mListView.getCount();
                            keepOnAppending.set(false);
                            pagerService.getNext(page++,
                                    new OnServiceFinished() {
                                        @Override
                                        public void onFinished() {
                                            // 获取下一页数据后，数据的个数没有变化表示没有心的数据，隐藏加载控件
                                            if (mListView.getCount() == pCount) {
                                                hasMore = false;
                                                // listView.removeFooterView(loadingView);
                                                loadingView
                                                        .setVisibility(View.GONE);
                                            }
                                            keepOnAppending.set(true);
                                        }
                                    });
                        }
                    }
                }
            }
        };
        this.mListView.setOnScrollListener(onScrollListener);
    }

    /**
     * 移除底部控件
     */
    public void removeFootView() {
        this.mListView.removeFooterView(loadingView);
    }

    /**
     * 调用这个方法可以完成刷新，客户端代码需要同时清空掉ListView适配器中的数据
     * 
     * 
     */
    public void reSet() {
        if (loadingView != null) {
            this.loadingView.setVisibility(View.VISIBLE);
        }
        this.page = 1;
        this.hasMore = true;
        keepOnAppending.set(true);
    }

    public Integer getPage() {
        return page;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public View getLoadingView() {
        return loadingView;
    }

    public void setLoadingView(View loadingView) {
        this.loadingView = loadingView;
    }

    public void setPagerService(PagerService pagerService) {
        this.pagerService = pagerService;
    }

}

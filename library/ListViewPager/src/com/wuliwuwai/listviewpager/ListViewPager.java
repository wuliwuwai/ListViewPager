package com.wuliwuwai.listviewpager;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ListViewPager {

    /**
     * 加载分页
     */
    private LinearLayout     loadingLayout;
    /**
     * 当前页
     */
    private Integer          page            = 1;
    /**
     * 有更多
     */
    private Boolean          hasMore         = true;
    /**
     * 当前现实最后一个item
     */
    private Integer          lastItem        = 0;
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
    private ListView         listView;

    private PagerService     pagerService;

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
         * service执行完成之后需要调用
         */
        public void onFinished();
    }

    public void removeFootView() {
        this.listView.removeFooterView(loadingLayout);
    }

    public void reset() {
        // removeFootView();
        this.page = 1;
        this.lastItem = 0;
        this.hasMore = true;
        keepOnAppending.set(true);
    }

    public void requestData() {

    }

    /**
     * pull to refresh
     * 
     * @param context
     * @param mListView
     * @param isPullToRefresh
     * @param service
     */
    public ListViewPager(Context context, ListView mListView,
            boolean isPullToRefresh, final PagerService service) {
        super();
        if (isPullToRefresh) {
            reset();
            this.listView = mListView;
            loadingLayout = (LinearLayout) LayoutInflater.from(context)
                    .inflate(R.layout.view_loading_layout, null);

            this.listView.addHeaderView(loadingLayout);
            onScrollListener = new OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view,
                        int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                        int visibleItemCount, int totalItemCount) {

                    if (hasMore && keepOnAppending.get()) {

                        // lastItem = firstVisibleItem + visibleItemCount - 1;
                        if (firstVisibleItem == 0) {
                            getPullData(service);
                        }
                    }
                }
            };
            this.listView.setOnScrollListener(onScrollListener);
        }
    }

    public ListViewPager(Context context, ListView mListView,
            final PagerService service) {
        reset();

        this.listView = mListView;
        loadingLayout = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.view_loading_layout, null);
        this.listView.addFooterView(loadingLayout);
        onScrollListener = new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {

                if (hasMore && keepOnAppending.get()) {
                    lastItem = firstVisibleItem + visibleItemCount - 1;
                    if (firstVisibleItem + visibleItemCount == totalItemCount) {
                        getData(service);
                    }
                }
            }
        };
        this.listView.setOnScrollListener(onScrollListener);

    }

    private void getPullData(PagerService service) {
        final int pCount = listView.getCount();
        keepOnAppending.set(false);
        service.getNext(page++, new OnServiceFinished() {
            @Override
            public void onFinished() {

                Log.i("ListViewPager", pCount + "   " + listView.getCount()
                        + " footViewCount:" + listView.getFooterViewsCount()
                        + " childCount:" + listView.getCount());
                if (listView.getCount() == pCount) {
                    hasMore = false;

                    listView.removeHeaderView(loadingLayout);

                }
                keepOnAppending.set(true);
            }
        });
    }

    private void getData(PagerService service) {
        final int pCount = listView.getCount();
        keepOnAppending.set(false);
        service.getNext(page++, new OnServiceFinished() {
            @Override
            public void onFinished() {

                Log.i("ListViewPager", pCount + "   " + listView.getCount()
                        + " footViewCount:" + listView.getFooterViewsCount()
                        + " childCount:" + listView.getCount());
                if (listView.getCount() == pCount) {
                    hasMore = false;
                    // if (listView.getFooterViewsCount() > 0)
                    // listView.getChildCount();
                    // loadingLayout.setVisibility(View.GONE);
                    listView.removeFooterView(loadingLayout);
                }
                keepOnAppending.set(true);
            }
        });
    }

}

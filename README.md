ListViewPager
=============

ListView分页,就一个类，轻量级。可以自动检测是否还有更多页，支持普通的ListView,不需要使用任何继承。

代码示例
=============
``` java
ListViewPager listPager=new ListPager(this,mListView);
        listPager.setPagerService(new PagerService() {
                    @Override
                    public void getNext(int page,OnServiceFinished finished) {
        //从网络获取分页数据 page是页码
                      //加载完代码后必须调用 finished.onFinished();
                    }
                });
``` 
                

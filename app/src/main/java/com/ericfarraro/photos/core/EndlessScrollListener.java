package com.ericfarraro.photos.core;

import android.util.Log;
import android.widget.AbsListView;

/**
 * Created by Eric on 10/8/2014.
 */
public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {

    protected boolean mIsLoading;
    protected int mPreviousTotalItemCount;
    protected int mCurrentPage = 1;

    // number of items below current scroll position before we need to load more
    protected int mVisibleThreshold = 12;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        // if we have less items than before, we must have reset
        if(totalItemCount < mPreviousTotalItemCount) {

            mPreviousTotalItemCount = totalItemCount;
            mCurrentPage = 1;

            // zero items would indicate that we must be loading more (eg: initial load)
            if(totalItemCount == 0) {
                mIsLoading = true;
            }
        }

        // previously, data was loading.  if the total items has increased, we necessarily must
        // have finished loading
        if(mIsLoading && totalItemCount > mPreviousTotalItemCount) {
            mIsLoading = false;
            mPreviousTotalItemCount = totalItemCount;
            mCurrentPage++;
        }

        // do we need to load more data?
        if(!mIsLoading &&
                (totalItemCount - firstVisibleItem - visibleItemCount) <= mVisibleThreshold) {
            mIsLoading = true;
            loadMore(mCurrentPage + 1);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // nothing to do here
    }

    public abstract void loadMore(int page);
}

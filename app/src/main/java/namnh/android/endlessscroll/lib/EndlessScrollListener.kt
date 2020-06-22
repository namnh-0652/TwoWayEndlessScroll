package namnh.android.endlessscroll.lib

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val direction: EndlessScrollDirection
) : RecyclerView.OnScrollListener() {

    // The minimum amount of items to have below your current scroll position before loading more.
    var visibleThreshold = DEFAULT_THRESHOLD
    // True if we are still waiting for the last set of data to load.
    private var loading = true
    // The total number of items in the dataSet after the last load
    private var previousTotalItemCount = 0

    private var topReachEnd = false
    private var bottomReachEnd = false

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        val lastVisibleItemPosition: Int = layoutManager.findLastVisibleItemPosition()
        val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()
        val totalItemCount = layoutManager.itemCount
        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                this.loading = true
            }
        }
        // If it’s still loading, we check to see if the dataSet count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }
        val topCondition = !loading && firstVisibleItemPosition < visibleThreshold && !topReachEnd
        val bottomCondition =
            !loading && lastVisibleItemPosition + visibleThreshold >= totalItemCount && !bottomReachEnd
        when (direction) {
            EndlessScrollDirection.BOTTOM -> {
                // If it isn’t currently loading, we check to see if we have breached
                // the visibleThreshold and need to reload more data.
                // If we do need to reload some more data, we execute onLoadMore to fetch the data.
                // threshold should reflect how many total columns there are too
                if (bottomCondition) {
                    onLoadMoreBottom()
                    loading = true
                }
            }
            EndlessScrollDirection.TOP -> {
                if (topCondition) {
                    onLoadMoreTop()
                    loading = true
                }
            }
            EndlessScrollDirection.TWO_WAY -> {
                if (topCondition && bottomCondition) {
                    onLoadMoreTop()
                    onLoadMoreBottom()
                    loading = true
                } else {
                    if (topCondition) {
                        onLoadMoreTop()
                        if (!topReachEnd) {
                            loading = true
                        }
                    }
                    if (bottomCondition) {
                        onLoadMoreBottom()
                        if (!bottomReachEnd) {
                            loading = true
                        }
                    }
                }
            }
        }
    }

    fun reset() {
        previousTotalItemCount = 0
        topReachEnd = false
        bottomReachEnd = false
    }

    fun notifyTopReachEnd() {
        topReachEnd = true
        loading = false
    }

    fun notifyBottomReachEnd() {
        bottomReachEnd = true
        loading = false
    }

    abstract fun onLoadMoreTop()
    abstract fun onLoadMoreBottom()

    companion object {
        const val DEFAULT_THRESHOLD = 1
    }
}

package namnh.android.endlessscroll.lib

import android.content.Context
import android.os.Parcelable

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyLayoutManager(context: Context) : LinearLayoutManager(context) {

    private var pendingTargetPos = -1
    private var pendingPosOffset = -1

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        if (pendingTargetPos != -1 && state.itemCount > 0) {
            /**
             * Data is present now, we can set the real scroll position
            */
            scrollToPositionWithOffset(pendingTargetPos, pendingPosOffset)
            pendingTargetPos = -1
            pendingPosOffset = -1
        }
        super.onLayoutChildren(recycler, state)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        /**
         * May be needed depending on your implementation.
         * Ignore target start position if InstanceState is available (page existed before already, keep position that user scrolled to)
         */
        pendingTargetPos = -1
        pendingPosOffset = -1
        super.onRestoreInstanceState(state)
    }

    /**
     * Sets a start position that will be used **as soon as data is available**.
     * May be used if your Adapter starts with itemCount=0 (async data loading) but you need to
     * set the start position already at this time. As soon as itemCount > 0,
     * it will set the scrollPosition, so that given itemPosition is visible.
     * @param position
     * @param offset
     */
    fun setTargetStartPos(position: Int, offset: Int) {
        pendingTargetPos = position
        pendingPosOffset = offset
    }
}
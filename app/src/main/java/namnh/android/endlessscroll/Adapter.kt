package namnh.android.endlessscroll

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import namnh.android.endlessscroll.lib.EndlessScrollDirection

class Adapter(private var endlessScrollDirection: EndlessScrollDirection) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var currentList = mutableListOf<String>()
        private set

    var isReachedTop = false
    var isReachedBottom = false

    fun set(items: List<String>) {
        currentList.apply {
            clear()
            addAll(items)
            notifyDataSetChanged()
        }
    }

    fun reset(endlessScrollDirection: EndlessScrollDirection) {
        this.endlessScrollDirection = endlessScrollDirection
        clear()
    }

    fun clear() {
        currentList.apply {
            clear()
            notifyDataSetChanged()
        }
        isReachedBottom = false
        isReachedTop = false
    }

    fun add(items: List<String>) {
        val currentSize = currentList.size
        currentList.addAll(items)
        notifyItemRangeInserted(currentSize + 1, items.size)
    }

    fun addFirst(items: List<String>) {
        currentList.addAll(0, items)
        notifyItemRangeInserted(0, items.size)
    }

    fun notifyTopReachEnd() {
        isReachedTop = true
        notifyItemChanged(0)
    }

    fun notifyBottomReachEnd() {
        isReachedBottom = true
        notifyItemChanged(itemCount - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        if (viewType == ITEM) {
            val contactView = inflater.inflate(R.layout.item_test, parent, false)
            return ViewHolder(contactView)
        }
        return ProgressHolder(inflater.inflate(R.layout.item_progress, parent, false))
    }


    override fun getItemCount(): Int {
        return if (endlessScrollDirection == EndlessScrollDirection.TWO_WAY) {
            currentList.size + 2
        } else {
            currentList.size + 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (endlessScrollDirection) {
            EndlessScrollDirection.TWO_WAY -> {
                if (position == currentList.size + 1 || position == 0) LOADING else ITEM
            }
            EndlessScrollDirection.TOP -> {
                if (position == 0) LOADING else ITEM
            }
            EndlessScrollDirection.BOTTOM -> {
                if (position == currentList.size) LOADING else ITEM
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM) {
            val validPosition = if (endlessScrollDirection == EndlessScrollDirection.TWO_WAY
                || endlessScrollDirection == EndlessScrollDirection.TOP
            ) position - 1 else position

            val str = currentList[validPosition]
            (holder as ViewHolder).tv.text = str
        } else {
            if ((isReachedBottom && position == itemCount - 1)
                || (isReachedTop && position == 0)
            ) {
                (holder as ProgressHolder).apply {
                    tvNoMore.visibility = View.VISIBLE
                    progress.visibility = View.GONE
                }
            } else {
                (holder as ProgressHolder).apply {
                    tvNoMore.visibility = View.GONE
                    progress.visibility = View.VISIBLE
                }
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv: TextView = view.findViewById(R.id.tv)
    }

    inner class ProgressHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNoMore: TextView = view.findViewById(R.id.tv_no_more)
        val progress: ProgressBar = view.findViewById(R.id.pb)
    }

    companion object {
        const val LOADING = 0
        const val ITEM = 1
    }
}
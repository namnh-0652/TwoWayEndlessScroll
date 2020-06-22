package namnh.android.endlessscroll

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import namnh.android.endlessscroll.lib.EndlessScrollDirection
import namnh.android.endlessscroll.lib.EndlessScrollListener
import namnh.android.endlessscroll.lib.MyLayoutManager

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by lazy {
        MainViewModel()
    }
    private lateinit var adapter: Adapter

    private val layoutManager = MyLayoutManager(this)
    private val listener = getListener(EndlessScrollDirection.TWO_WAY)

    private fun getListener(endlessScrollDirection: EndlessScrollDirection) = object :
        EndlessScrollListener(layoutManager, endlessScrollDirection) {
        override fun onLoadMoreTop() {
            if (viewModel.isPastReachEnd()) {
                this@MainActivity.notifyTopReachEnd()
            } else {
                viewModel.getPreviousData()
            }
        }

        override fun onLoadMoreBottom() {
            if (viewModel.isFutureReachEnd()) {
                this@MainActivity.notifyBottomReachEnd()
            } else {
                viewModel.getFutureData()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        observe()
        getData()
    }

    private fun initView() {
        rv.layoutManager = layoutManager
        layoutManager.setTargetStartPos(0, 0)
        rv.addOnScrollListener(listener)
        adapter = Adapter(EndlessScrollDirection.TWO_WAY)
        rv.adapter = adapter
    }

    private fun observe() {
        viewModel.todayData.observe(this, Observer {
            adapter.set(it)
        })

        viewModel.pastData.observe(this, Observer {
            adapter.addFirst(it)
        })

        viewModel.futureData.observe(this, Observer {
            adapter.add(it)
        })

        viewModel.pastReachEnd.observe(this, Observer {
            notifyTopReachEnd()
        })

        viewModel.futureReachEnd.observe(this, Observer {
            notifyBottomReachEnd()
        })
    }

    private fun notifyTopReachEnd() {
        listener.notifyTopReachEnd()
        adapter.notifyTopReachEnd()
    }

    private fun notifyBottomReachEnd() {
        listener.notifyBottomReachEnd()
        adapter.notifyBottomReachEnd()
    }

    private fun getData() {
        viewModel.getTodayData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.endless_bottom -> {
                resetEndless(EndlessScrollDirection.BOTTOM)
                true
            }
            R.id.endless_top -> {
                resetEndless(EndlessScrollDirection.TOP)
                true
            }
            R.id.endless_two_way -> {
                resetEndless(EndlessScrollDirection.TWO_WAY)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun resetEndless(endlessScrollDirection: EndlessScrollDirection) {
        viewModel.reset()
        adapter.reset(endlessScrollDirection)
        rv.clearOnScrollListeners()
        rv.addOnScrollListener(getListener(endlessScrollDirection))
        getData()
    }
}

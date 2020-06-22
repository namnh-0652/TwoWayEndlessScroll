package namnh.android.endlessscroll

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val pastReachEnd = MutableLiveData<Boolean>()
    val futureReachEnd = MutableLiveData<Boolean>()
    val todayData = MutableLiveData<MutableList<String>>()
    val pastData = MutableLiveData<MutableList<String>>()
    val futureData = MutableLiveData<MutableList<String>>()
    private var pastPage = 1
    private var futurePage = 1

    fun getTodayData() {
        viewModelScope.launch {
            val response = mutableListOf<String>()
            for (i in 0..10) {
                response.add("Item $i")
            }
            todayData.value = response
        }

    }

    fun getPreviousData() {
        viewModelScope.launch {
            delay(250)
            if (pastPage > 5) {
                pastReachEnd.value = true
                return@launch
            }
            val response = mutableListOf<String>()
            for (i in 10 downTo 1) {
                response.add("Item past ${10 * (pastPage - 1) + i}")
            }
            pastData.value = response
            pastPage++
        }
    }

    fun getFutureData() {
        viewModelScope.launch {
            delay(250)
            if (futurePage > 5) {
                futureReachEnd.value = true
                return@launch
            }
            val response = mutableListOf<String>()
            for (i in 1..10) {
                response.add("Item future ${10 * (futurePage - 1) + i}")
            }
            futureData.value = response
            futurePage++
        }
    }

    fun isPastReachEnd() = pastReachEnd.value == true
    fun isFutureReachEnd() = futureReachEnd.value == true
    fun reset() {
        pastPage = 1
        futurePage = 1
        pastReachEnd.value = false
        futureReachEnd.value = false
    }
}

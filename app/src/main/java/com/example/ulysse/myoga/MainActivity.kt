package com.example.ulysse.myoga

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.DisplayMetrics
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ulysse.myoga.Model.ApiNetworkResponse
import com.example.ulysse.myoga.Model.Pose
import com.example.ulysse.myoga.Network.NetworkService
import com.example.ulysse.myoga.Network.RetrofitClient
import com.example.ulysse.myoga.Utils.SingleToast
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity()
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var presenterInteractor: PresenterInteractor
    private lateinit var dataProgressBar: ProgressBar
    private var queryEditText: EditText? = null
    private var searchProgressBar: ProgressBar? = null
    private var textViewDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        queryEditText = findViewById(R.id.search_edit_text)
        searchProgressBar = findViewById(R.id.search_progress_bar)
        dataProgressBar = findViewById(R.id.data_progress_bar)
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = GridLayoutManager(this@MainActivity, calculateNoOfColumns())
        recyclerView.adapter = YogaPoseAdapter(emptyList()) //set empty adapter in case of search before request returns
        subscribeTextViewObservable()
        if (savedInstanceState == null)
        {
            presenterInteractor = PresenterLayer(this, NetworkService(RetrofitClient.createNetworkService()), emptyList())
            presenterInteractor.loadYogaPoseList()
        } else
        {
            dataProgressBar.visibility = View.GONE
            val yogaPoseListDB: ApiNetworkResponse? = savedInstanceState.getParcelable(LIST_PARCEL_KEY)
            presenterInteractor = PresenterLayer(this, NetworkService(RetrofitClient.createNetworkService()), yogaPoseListDB?.yogaPoseList)
            val savedRecyclerViewGridItems: ApiNetworkResponse? = savedInstanceState.getParcelable(SEARCH_LIST_PARCEL_KEY)
            (recyclerView.adapter as YogaPoseAdapter).yogaPoseList = savedRecyclerViewGridItems?.yogaPoseList
        }
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        val recyclerViewGridItems = ApiNetworkResponse(
                (recyclerView.adapter as YogaPoseAdapter?)!!.yogaPoseList)
        //TODO: issue if triggered without request result: empty list, no later request
        val yogaPoseListDB = ApiNetworkResponse(presenterInteractor.yogaPoseListDB)
        outState.putParcelable(SEARCH_LIST_PARCEL_KEY, recyclerViewGridItems)
        outState.putParcelable(LIST_PARCEL_KEY, yogaPoseListDB)
        outState.putParcelable(LIST_STATE_KEY, recyclerView.layoutManager!!.onSaveInstanceState())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle)
    {
        super.onRestoreInstanceState(savedInstanceState)
        val recycleViewLayoutState = savedInstanceState.getParcelable<Parcelable>(LIST_STATE_KEY)
        recyclerView.layoutManager!!.onRestoreInstanceState(recycleViewLayoutState)
    }

    override fun onDestroy()
    {
        super.onDestroy()
        if (textViewDisposable != null)
        {
            textViewDisposable!!.dispose()
        }
    }

    fun calculateNoOfColumns(): Int
    {
        var metrics = DisplayMetrics();

        windowManager.defaultDisplay.getMetrics(metrics)
        var dpWidth = metrics.widthPixels / metrics.density;
        var noOfColumns = dpWidth / 180;
        return noOfColumns.roundToInt();
    }

    /**
     * @param requestResponse
     */
    fun updateViewForRequestSuccess(requestResponse: List<Pose?>?)
    {
        dataProgressBar.visibility = View.GONE
        (recyclerView.adapter as YogaPoseAdapter?)!!.yogaPoseList = requestResponse
    }

    /**
     *
     */
    fun updateViewForRequestError()
    {
        dataProgressBar.visibility = View.GONE
        SingleToast.show(this, R.string.request_failed, Toast.LENGTH_SHORT)
    }

    /**
     *
     * @param searchResultList
     */
    protected fun updateViewForSearchResult(searchResultList: List<Pose?>)
    {
        if (searchResultList.isEmpty())
        {
            SingleToast.show(this, R.string.search_nothing_found, Toast.LENGTH_SHORT)
        }
        (recyclerView.adapter as YogaPoseAdapter?)!!.yogaPoseList = searchResultList
    }

    /**
     *
     */
    private fun subscribeTextViewObservable()
    {
        textViewDisposable = RxTextView.textChanges(queryEditText!!)
                .skipInitialValue()
                .debounce(USER_INPUT_TIME_DELAY.toLong(), TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { searchProgressBar!!.visibility = View.VISIBLE }
                .observeOn(Schedulers.io())
                .map { searchQuery -> presenterInteractor.searchYogaPoseList(searchQuery.toString()) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { searchResultList ->
                    searchProgressBar!!.visibility = View.GONE
                    updateViewForSearchResult(searchResultList)
                }
    }

    companion object
    {
        private const val LIST_STATE_KEY = "recycler_list_state"
        private const val SEARCH_LIST_PARCEL_KEY = "recycler_search_list_parcel"
        private const val LIST_PARCEL_KEY = "recycler_list_parcel"
        private const val GRID_COLUMN_NUMBER = 3
        private const val USER_INPUT_TIME_DELAY = 1000
    }
}
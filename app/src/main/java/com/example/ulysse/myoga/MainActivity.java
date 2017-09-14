package com.example.ulysse.myoga;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ulysse.myoga.Model.ApiNetworkResponse;
import com.example.ulysse.myoga.Model.Pose;
import com.example.ulysse.myoga.Network.NetworkService;
import com.example.ulysse.myoga.Network.RetrofitClient;
import com.example.ulysse.myoga.Utils.SingleToast;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity
{
    private final static String LIST_STATE_KEY = "recycler_list_state";
    private final static String SEARCH_LIST_PARCEL_KEY = "recycler_search_list_parcel";
    private final static String LIST_PARCEL_KEY = "recycler_list_parcel";
    private final static String WEBSITE_BASE_URL = "http://www.yogajournal.com/pose/";
    private final static int GRID_COLUMN_NUMBER = 3;
    private final static int USER_INPUT_TIME_DELAY = 1000;

    private RecyclerView recyclerView;
    private EditText queryEditText;
    private ProgressBar searchProgressBar;
    private ProgressBar dataProgressBar;

    private PresenterInteractor presenterInteractor;

    private Disposable textViewDisposable;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryEditText = (EditText) findViewById(R.id.search_edit_text);
        searchProgressBar = (ProgressBar) findViewById(R.id.search_progress_bar);
        dataProgressBar = (ProgressBar) findViewById(R.id.data_progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, GRID_COLUMN_NUMBER));
        recyclerView.setAdapter(new YogaPoseAdapter(Collections.EMPTY_LIST)); //set empty adapter in case of search before request returns


        subscribeTextViewObservable();

        if (savedInstanceState == null)
        {
            presenterInteractor = new PresenterLayer(this, new NetworkService(RetrofitClient.createNetworkService()), Collections.EMPTY_LIST);
            presenterInteractor.loadYogaPoseList();
        }
        else
        {
            dataProgressBar.setVisibility(View.GONE);
            ApiNetworkResponse yogaPoseListDB = savedInstanceState.getParcelable(LIST_PARCEL_KEY);
            presenterInteractor = new PresenterLayer(this, new NetworkService(RetrofitClient.createNetworkService()), yogaPoseListDB.getYogaPoseList());

            ApiNetworkResponse savedRecyclerViewGridItems = savedInstanceState.getParcelable(SEARCH_LIST_PARCEL_KEY);
            ((YogaPoseAdapter) recyclerView.getAdapter()).setYogaPoseList(savedRecyclerViewGridItems.getYogaPoseList());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        ApiNetworkResponse recyclerViewGridItems = new ApiNetworkResponse(
                ((YogaPoseAdapter) recyclerView.getAdapter()).getYogaPoseList());

        //TODO: issue if triggered without request result: empty list, no later request
        ApiNetworkResponse yogaPoseListDB = new ApiNetworkResponse(presenterInteractor.getYogaPoseListDB());

        outState.putParcelable(SEARCH_LIST_PARCEL_KEY, recyclerViewGridItems);
        outState.putParcelable(LIST_PARCEL_KEY, yogaPoseListDB);
        outState.putParcelable(LIST_STATE_KEY, recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        Parcelable recycleViewLayoutState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        recyclerView.getLayoutManager().onRestoreInstanceState(recycleViewLayoutState);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (textViewDisposable != null)
        {
            textViewDisposable.dispose();
        }
    }

    public void onClickLoadUrl(View view)
    {
        TextView textView = (TextView) view;
        String poseName = textView.getText().toString().toLowerCase(); //English Name
        poseName = poseName.replaceAll("[^a-z]", "-");                 //Format url
        String poseUrl = WEBSITE_BASE_URL + poseName;

        Intent loadUrlIntent = new Intent(Intent.ACTION_VIEW);
        loadUrlIntent.setData(Uri.parse(poseUrl));
        startActivity(loadUrlIntent);
    }

    /**
     * @param requestResponse
     */
    protected void updateViewForRequestSuccess(List<Pose> requestResponse)
    {
        dataProgressBar.setVisibility(View.GONE);
        ((YogaPoseAdapter) recyclerView.getAdapter()).setYogaPoseList(requestResponse);
    }

    /**
     *
     */
    protected void updateViewForRequestError()
    {
        dataProgressBar.setVisibility(View.GONE);
        SingleToast.show(this, R.string.request_failed, Toast.LENGTH_SHORT);
    }

    /**
     *
     * @param searchResultList
     */
    protected void updateViewForSearchResult(List<Pose> searchResultList)
    {
        if (searchResultList.isEmpty())
        {
            SingleToast.show(this, R.string.search_nothing_found, Toast.LENGTH_SHORT);
        }
        ((YogaPoseAdapter) recyclerView.getAdapter()).setYogaPoseList(searchResultList);
    }

    /**
     *
     */
    private void subscribeTextViewObservable()
    {
        textViewDisposable = RxTextView.textChanges(queryEditText)
                .skipInitialValue()
                .debounce(USER_INPUT_TIME_DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<CharSequence>()
                {
                    @Override
                    public void accept(CharSequence s) throws Exception
                    {
                        searchProgressBar.setVisibility(View.VISIBLE);
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<CharSequence, List<Pose>>()
                {
                    @Override
                    public List<Pose> apply(CharSequence searchQuery) throws Exception
                    {
                        return presenterInteractor.searchYogaPoseList(searchQuery.toString());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Pose>>()
                {
                    @Override
                    public void accept(List<Pose> searchResultList) throws Exception
                    {
                        searchProgressBar.setVisibility(View.GONE);
                        updateViewForSearchResult(searchResultList);
                    }
                });
    }
}

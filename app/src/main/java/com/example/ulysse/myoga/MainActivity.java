package com.example.ulysse.myoga;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ulysse.myoga.Model.ApiNetworkResponse;
import com.example.ulysse.myoga.Model.Pose;
import com.example.ulysse.myoga.Network.NetworkService;
import com.example.ulysse.myoga.Utils.ApiUtils;
import com.example.ulysse.myoga.Utils.SingleToast;
import com.jakewharton.rxbinding2.widget.RxTextView;

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
    private final static String LIST_PARCEL_KEY = "recycler_list_parcel";
    private final static int GRID_COLUMN_NUMBER = 3;
    private final static int USER_INPUT_TIME_DELAY = 500;

    private RecyclerView recyclerView;
    private EditText queryEditText;
    private ProgressBar searchProgressBar;

    private NetworkService networkService;
    private ApiNetworkResponse apiNetworkResponse;

    private Disposable textViewDisposable;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryEditText = (EditText) findViewById(R.id.search_edit_text);
        searchProgressBar = (ProgressBar) findViewById(R.id.search_progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, GRID_COLUMN_NUMBER));

        if(savedInstanceState == null)
        {
            networkService = new NetworkService(ApiUtils.createNetworkService());
            getYogaPoseList();
        }
        else
        {
            ApiNetworkResponse savedRecyclerListState = savedInstanceState.getParcelable(LIST_PARCEL_KEY);
            recyclerView.setAdapter(new YogaPoseAdapter(savedRecyclerListState.getYogaPoseList()));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        ApiNetworkResponse currentListItems = new ApiNetworkResponse(
                ((YogaPoseAdapter)recyclerView.getAdapter()).getYogaPoseList());

        outState.putParcelable(LIST_PARCEL_KEY, currentListItems);
        outState.putParcelable(LIST_STATE_KEY, recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
    }

    //TODO: Display progressbar while loading
    public void getYogaPoseList()
    {
        networkService.getYogaPoseList(new NetworkService.GetYogaPoseListCallback()
        {
            @Override
            public void onSuccess(ApiNetworkResponse yogaPoseListResponse)
            {
                apiNetworkResponse = yogaPoseListResponse; //Keep a reference for search
                recyclerView.setAdapter(new YogaPoseAdapter(yogaPoseListResponse.getYogaPoseList()));
                subscribeTextViewObservable();
                Log.d("MainActivity", "posts loaded from API");
            }

            @Override
            public void onError(Throwable networkError)
            {
                SingleToast.show(getApplicationContext(), R.string.request_failed, Toast.LENGTH_SHORT);
                Log.d("MainActivity", "error loading posts from API");
            }

        });
    }


    protected void showResult(List<Pose> result)
    {
        if (result.isEmpty())
        {
            SingleToast.show(this, R.string.search_nothing_found, Toast.LENGTH_SHORT);
        }
        ((YogaPoseAdapter) recyclerView.getAdapter()).setYogaPoseList(result);
    }

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
                    public List<Pose> apply(CharSequence query) throws Exception
                    {
                        return apiNetworkResponse.searchPose(query.toString());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Pose>>()
                {
                    @Override
                    public void accept(List<Pose> result) throws Exception
                    {
                        searchProgressBar.setVisibility(View.GONE);
                        showResult(result);
                    }
                });
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
}

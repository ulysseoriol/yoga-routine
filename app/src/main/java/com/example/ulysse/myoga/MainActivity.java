package com.example.ulysse.myoga;

import android.os.Bundle;
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

import java.util.Collections;
import java.util.List;
import java.util.Observable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity
{
    private final int GRID_COLUMN_NUMBER = 3;

    private RecyclerView recyclerView;
    private EditText queryEditText;
    private ProgressBar progressBar;

    private NetworkService networkService;
    private ApiNetworkResponse apiNetworkResponse;

    private Disposable textViewDisposable;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkService = new NetworkService(ApiUtils.createNetworkService());

        queryEditText = (EditText) findViewById(R.id.query_edit_text);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, GRID_COLUMN_NUMBER));
        getYogaPoseList();
    }

    public void getYogaPoseList()
    {
        networkService.getYogaPoseList(new NetworkService.GetYogaPoseListCallback()
        {
            @Override
            public void onSuccess(ApiNetworkResponse yogaPoseListResponse)
            {
//                try
//                {
//                    Thread.sleep(4000);
//                }
//                catch (InterruptedException e)
//                {
//                    e.printStackTrace();
//                }
                apiNetworkResponse = yogaPoseListResponse;
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
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<CharSequence>()
                {
                    @Override
                    public void accept(CharSequence s) throws Exception
                    {
                        progressBar.setVisibility(View.VISIBLE);
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
                        progressBar.setVisibility(View.GONE);
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

//    //Save recyclerview's state
//    public final static String LIST_STATE_KEY = "recycler_list_state";
//    Parcelable listState;
//
//    protected void onSaveInstanceState(Bundle state) {
//        super.onSaveInstanceState(state);
//        // Save list state
//        listState = mLayoutManager.onSaveInstanceState();
//        state.putParcelable(LIST_STATE_KEY, listState);
//    }
//
//    protected void onRestoreInstanceState(Bundle state) {
//        super.onRestoreInstanceState(state);
//        // Retrieve list state and list/item positions
//        if(state != null)
//            listState = state.getParcelable(LIST_STATE_KEY);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (listState != null) {
//            mLayoutManager.onRestoreInstanceState(listState);
//        }
//    }
}

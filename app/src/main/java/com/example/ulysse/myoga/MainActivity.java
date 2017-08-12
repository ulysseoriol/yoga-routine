package com.example.ulysse.myoga;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ulysse.myoga.Model.ApiNetworkResponse;
import com.example.ulysse.myoga.Model.Pose;
import com.example.ulysse.myoga.Network.IApiNetworkService;
import com.example.ulysse.myoga.Utils.ApiUtils;
import com.example.ulysse.myoga.Utils.SingleToast;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
{

    private RecyclerView recyclerView;
    private EditText queryEditText;
    private ProgressBar progressBar;

    private ApiNetworkResponse apiNetworkResponse;
    private IApiNetworkService apiNetworkService;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiNetworkService = ApiUtils.createNetworkService();
        getYogaPoseList();

        queryEditText = (EditText) findViewById(R.id.query_edit_text);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
        recyclerView.setAdapter(new YogaPoseAdapter(Collections.EMPTY_LIST));

        subscribeSearchObservable(createTextChangeObservable());
        getYogaPoseList();
    }

    private void subscribeSearchObservable(Observable<String> searchObservable)
    {
        searchObservable
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>()
                {
                    @Override
                    public void accept(String s) throws Exception
                    {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<String, List<Pose>>()
                {
                    @Override
                    public List<Pose> apply(String query) throws Exception
                    {
                        return searchPose(query);
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

    public void getYogaPoseList()
    {
        apiNetworkService.getYogaPoseList().enqueue(new Callback<ApiNetworkResponse>()
        {
            @Override
            public void onResponse(Call<ApiNetworkResponse> call, Response<ApiNetworkResponse> response)
            {
                if (response.isSuccessful())
                {
                    apiNetworkResponse = response.body();
                    ((YogaPoseAdapter) recyclerView.getAdapter()).setYogaPoseList(apiNetworkResponse.getYogaPoseList());
                    Log.d("MainActivity", "posts loaded from API");
                }
                else
                {
                    int statusCode = response.code();
                    // handle request errors depending on status code
                }
            }

            @Override
            public void onFailure(Call<ApiNetworkResponse> call, Throwable t)
            {
                SingleToast.show(getApplicationContext(), R.string.request_failed, Toast.LENGTH_SHORT);
                Log.d("MainActivity", "error loading from API");

            }
        });
    }

    protected List<Pose> searchPose(String query)
    {
        query = query.toLowerCase();

        List<Pose> result = new LinkedList<>();
        List<Pose> baseList = apiNetworkResponse.getYogaPoseList();

        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        for (int i = 0; i < baseList.size(); i++)
        {
            if (baseList.get(i).englishName.toLowerCase().contains(query))
            {
                result.add(baseList.get(i));
            }
        }

        return result;
    }

    protected void showResult(List<Pose> result)
    {
        if (result.isEmpty())
        {
             SingleToast.show(this, R.string.search_nothing_found, Toast.LENGTH_SHORT);
            ((YogaPoseAdapter) recyclerView.getAdapter()).setYogaPoseList(Collections.EMPTY_LIST);
        }
        else
        {
            ((YogaPoseAdapter) recyclerView.getAdapter()).setYogaPoseList(result);
        }
    }

    private Observable<String> createTextChangeObservable()
    {
        return Observable.create(new ObservableOnSubscribe<String>()
        {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception
            {
                final TextWatcher watcher = new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after)
                    {
                    }

                    @Override
                    public void afterTextChanged(Editable s)
                    {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count)
                    {
                        emitter.onNext(s.toString());
                    }
                };
                queryEditText.addTextChangedListener(watcher);
                emitter.setCancellable(new Cancellable()
                {
                    @Override
                    public void cancel() throws Exception
                    {
                        queryEditText.removeTextChangedListener(watcher);
                    }
                });
            }
        });
    }
}

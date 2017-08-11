package com.example.ulysse.myoga;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ulysse.myoga.Model.Pose;
import com.example.ulysse.myoga.Network.YogaPoseList;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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

public class MainActivity extends AppCompatActivity
{

    private RecyclerView recyclerView;
    private EditText queryEditText;
    private ProgressBar progressBar;
    private YogaPoseList yogaPoseList;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InputStream inputStream = getResources().openRawResource(R.raw.poses);
        Reader reader = new BufferedReader(new InputStreamReader(inputStream));
        Gson gson = new Gson();
        yogaPoseList = gson.fromJson(reader, YogaPoseList.class);

        queryEditText = (EditText) findViewById(R.id.query_edit_text);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
        recyclerView.setAdapter(new YogaPoseAdapter(yogaPoseList.getYogaPoseList()));

        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.activity_main);
        viewGroup.setLayoutTransition(layoutTransition);


        Observable<String> searchTextObservable = createTextChangeObservable();

        searchTextObservable
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

    protected List<Pose> searchPose(String query)
    {
        query = query.toLowerCase();

        List<Pose> result = new LinkedList<>();
        List<Pose> baseList = yogaPoseList.getYogaPoseList();

        try
        {
            Thread.sleep(2000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        for (int i = 0; i < baseList.size(); i++)
        {
            if (baseList.get(i).getEnglishName().toLowerCase().contains(query))
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
            Toast.makeText(this, R.string.search_nothing_found, Toast.LENGTH_SHORT).show();
            ((YogaPoseAdapter) recyclerView.getAdapter()).setYogaPoseList(Collections.EMPTY_LIST);
        } else
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

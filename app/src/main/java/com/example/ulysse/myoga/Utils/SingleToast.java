package com.example.ulysse.myoga.Utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * Created by ulysse on 8/12/17.
 */

public class SingleToast
{

    private static Toast mToast;

    public static void show(Context context, @StringRes int resId, int duration)
    {
        if (mToast != null)
        {
            mToast.cancel();
        }

        mToast = Toast.makeText(context, resId, duration);
        mToast.show();
    }
}
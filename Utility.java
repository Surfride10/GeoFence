package com.jddldesign.GeoFence;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Toast;


public class Utility {

    public static void ExceptionHandler(Context context, Exception E) {

        String sCallingMethod=E.getStackTrace()[0].getMethodName();
        final Toast toast;
        toast=Toast.makeText(context, E.getClass().getSimpleName()+"!\nIn "+context.getClass().getSimpleName()+"\\"+sCallingMethod+"\n"+E.getMessage(),Toast.LENGTH_LONG);
        toast.show();
        new CountDownTimer(9000, 1000)
        {
            public void onTick(long millisUntilFinished) {toast.show();}
            public void onFinish() {toast.show();}

        }.start();
    }
}

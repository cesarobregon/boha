package com.example.sistema_boha;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class volleys {
    private static volleys mVolleyS = null;
    //Este objeto es la cola que usará la aplicación
    private RequestQueue mRequestQueue;

    private volleys(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public static volleys getInstance(Context context) {
        if (mVolleyS == null) {
            mVolleyS = new volleys(context);
        }
        return mVolleyS;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}

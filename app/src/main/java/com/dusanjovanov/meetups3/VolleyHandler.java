package com.dusanjovanov.meetups3;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by duca on 21/12/2016.
 */

public class VolleyHandler {

    private static VolleyHandler instance = null;
    private static Context mCtx;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    private VolleyHandler(Context context) {
        mCtx = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });

    }

    public static synchronized VolleyHandler getInstance(Context context){
        if(instance==null){
            return new VolleyHandler(context);
        }
        return null;
    }

    public RequestQueue getRequestQueue() {
        if(requestQueue==null){
            return Volley.newRequestQueue(mCtx.getApplicationContext());
        }

        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }


    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}

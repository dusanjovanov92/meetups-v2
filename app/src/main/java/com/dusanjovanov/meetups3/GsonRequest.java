package com.dusanjovanov.meetups3;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by duca on 26/12/2016.
 */

public class GsonRequest<T> extends Request<T> {

    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Map<String, String> headers = new HashMap<>();
    private final ResponseListener<T> listener;

    {
        headers.put("Content-Type","application/x-www-form-urlencoded");
        headers.put("Authorization","801bb1e7ebfc127b17fb1330335701c2");
    }

    public interface ResponseListener<T> {
        void onResponse(T response);
    }

    public GsonRequest(int method,
                       String url,
                       ResponseListener<T> listener,
                       Response.ErrorListener errorListener,
                       Class<T> clazz) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.listener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    //TODO
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return super.getParams();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        }
        catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
        catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }
}

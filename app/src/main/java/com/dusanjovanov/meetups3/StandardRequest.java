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

public class StandardRequest<T> extends Request<T> {

    private static final String BASE_URL = "http://192.168.1.7/api/v1";
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final ResponseListener<T> listener;
    private Map<String, String> headers;
    private Map<String,String> params;

    {
        headers = new HashMap<>();
        headers.put("authorization","801bb1e7ebfc127b17fb1330335701c2");
        headers.put("Content-Type","application/x-www-form-urlencoded");
    }

    public interface ResponseListener<T> {
        void onResponse(T response);
    }

    public StandardRequest(int method,
                       String url,
                       Map<String,String> params,
                       Class<T> clazz,
                       ResponseListener<T> listener,
                       Response.ErrorListener errorListener){
        super(method,BASE_URL+url,errorListener);
        this.params = params;
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
        return params;
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

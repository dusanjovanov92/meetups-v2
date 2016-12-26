package com.dusanjovanov.meetups3.models;

/**
 * Created by duca on 26/12/2016.
 */

public class DataResponse<T> extends Response {
    private int count;
    private T data;
}

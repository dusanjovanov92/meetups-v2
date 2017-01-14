package com.dusanjovanov.meetups3.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.dusanjovanov.meetups3.R;

import java.io.Serializable;

/**
 * Created by duca on 28/12/2016.
 */

public class InterfaceUtil {
    public static final ColorGenerator COLOR_GENERATOR = ColorGenerator.MATERIAL;

    public static TextDrawable getTextDrawable(String text){
        int color = COLOR_GENERATOR.getColor(text);

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(text.substring(0,1),color);

        return drawable;

    }

    public interface RowClickListener{
        void onRowClick(Serializable serializable);
    }

    public static Drawable getMeetingResponseIcon(int response, Context context){
        int icon = 0;
        switch (response){
            case 0:
                icon = R.drawable.ic_help_outline_amber_900_36dp;
                break;
            case 1:
                icon = R.drawable.ic_check_green_500_36dp;
                break;
            case 2:
                icon = R.drawable.ic_close_red_500_36dp;
                break;
        }
        return ResourcesCompat.getDrawable(context.getResources(),icon,null);
    }

}

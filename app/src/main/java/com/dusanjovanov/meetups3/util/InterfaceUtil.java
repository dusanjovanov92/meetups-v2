package com.dusanjovanov.meetups3.util;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

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
}

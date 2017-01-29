package com.dusanjovanov.meetups3.util;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

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

    public interface OnRowClickListener {
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

    public static class HeaderHolder extends RecyclerView.ViewHolder{
        private TextView txtHeader;

        public HeaderHolder(View itemView) {
            super(itemView);
            txtHeader = (TextView) itemView.findViewById(R.id.txt_header);
        }

        public void bindHeader(String header) {
            txtHeader.setText(header);
        }
    }

    public static class NoResultsHolder extends RecyclerView.ViewHolder{
        private TextView txtNoResults;

        public NoResultsHolder(View itemView) {
            super(itemView);
            txtNoResults = (TextView) itemView.findViewById(R.id.txt_no_results);
        }

        public void bind(String text) {
            txtNoResults.setText(text);
        }
    }

    public static void showYesNoDialog(Context context,
                                       String message,
                                       String positive,
                                       String negative,
                                       DialogInterface.OnClickListener positiveListener){
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(positive,positiveListener)
                .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    public static void showInfoDialog(Context context,
                                      String title,
                                      String message){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_info_outline_blue_36dp)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

}

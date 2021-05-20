package com.example.manofsteel.optimalmessenger;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Win7 on 12-09-2016.
 */
public class RecyclerHolder extends RecyclerView.ViewHolder{
    TextView textView;
    ImageView imageView;
    public RecyclerHolder(View itemView) {
        super(itemView);
        textView=(TextView) itemView.findViewById(R.id.bot_name);
        imageView=(ImageView) itemView.findViewById(R.id.bot_image);


    }
}

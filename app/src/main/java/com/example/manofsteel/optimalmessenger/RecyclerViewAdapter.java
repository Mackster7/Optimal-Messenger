package com.example.manofsteel.optimalmessenger;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.recyclerview.widget.RecyclerView;

import org.kobjects.base64.Base64;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Win7 on 12-09-2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerHolder>{

    ArrayList<Institution> botlist;
    Context context;

    public RecyclerViewAdapter(ArrayList<Institution> botlist, Context context) {
        this.botlist = botlist;
        this.context = context;
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.bots_template,parent,false);
        RecyclerHolder recyclerHolder=new RecyclerHolder(view);
        return recyclerHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {
        final int p = position;
        holder.imageView.setImageBitmap(getImage(botlist.get(position).getImage()));
        holder.textView.setText(botlist.get(position).getName());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ChatActivity.class);
                intent.putExtra("id",botlist.get(p).getId());
                intent.putExtra("botName",botlist.get(p).getName());
                context.startActivity(intent);
            }
        });

    }

    public Bitmap getImage(String imgString)
    {
        byte[] productPreviewBytes = Base64.decode(imgString);

        Bitmap bitmap = BitmapFactory.decodeByteArray(productPreviewBytes, 0,
                productPreviewBytes.length);
        return bitmap;
    }

    @Override
    public int getItemCount() {
        return botlist.size();
    }
    public ArrayList<Institution> getList() {
        return botlist;
    }


}

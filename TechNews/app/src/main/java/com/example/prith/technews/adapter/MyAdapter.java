package com.example.prith.technews.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prith.technews.Model.NewsModel;
import com.example.prith.technews.R;
import com.example.prith.technews.ReadNews;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prith on 1/28/2018.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.AdapterHolder> {
    private Context context;
    private List<NewsModel> newsList;
    public MyAdapter(Context context, List<NewsModel> newsList) {
        this.context = context;
        this.newsList = newsList;
    }


    @NonNull
    @Override
    public AdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.card_view, null);
        return new AdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterHolder holder, int position) {
        final NewsModel model = newsList.get(position);
        // saving file to string
        final String title = model.title;
        final String desc = model.description;
        final String image = model.image;
        final String author = model.author;
        final String site = model.website;

        // setting the string variable to the textView of the holder
        holder.title.setText(title);
        holder.author.setText(author);
        Picasso.with(context)
            .load(model.image)
            .resize(150,150)
            .onlyScaleDown()
            .placeholder(R.drawable.img_placeholder)
            .error(R.drawable.error_placeholder)
            .into(holder.imageView, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    // TODO Auto-generated method stub
                    Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
                }
            });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent intent = new Intent(context, ReadNews.class);
                Bundle bundle = new Bundle();
                bundle.putString("title",title);
                bundle.putString("description",desc);
                bundle.putString("image",image);
                bundle.putString("author",author);
                bundle.putString("site",site);
                intent.putExtras(bundle);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context,
                                holder.imageView, ViewCompat.getTransitionName(holder.imageView));
                context.startActivity(intent, options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    class AdapterHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView title, author;
        CardView cardView;

        AdapterHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            imageView = itemView.findViewById(R.id.newsImgArea);
            title = itemView.findViewById(R.id.titleArea);
            author = itemView.findViewById(R.id.postedBy);
        }
    }

    public void searchFilter(ArrayList<NewsModel> newList){

        newsList.clear();
        newsList.addAll(newList);
        notifyDataSetChanged();



        for(NewsModel news : newList){

            Log.v("checkingdata",news.title);


        }



    }
}


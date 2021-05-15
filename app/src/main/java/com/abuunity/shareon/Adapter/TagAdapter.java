package com.abuunity.shareon.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abuunity.shareon.R;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder>{

    private Context context;
    private List<String> tagList;
    private List<String> countList;

    public TagAdapter(Context context, List<String> tagList, List<String> countList) {
        this.context = context;
        this.tagList = tagList;
        this.countList = countList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tags, parent, false);
        return new TagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tag.setText("#"+tagList.get(position));
        holder.noOfPost.setText(countList.get(position) + " posts");
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tag;
        public TextView noOfPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tag = itemView.findViewById(R.id.hash_tag);
            noOfPost = itemView.findViewById(R.id.no_of_posts);
        }
    }

    public  void filter(List<String> tagLists, List<String> countLists) {
        this.tagList = tagLists;
        this.countList = countLists;

        notifyDataSetChanged();
    }
}

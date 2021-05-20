package com.abuunity.shareon.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.abuunity.shareon.CommentActivity;
import com.abuunity.shareon.Model.Steps;
import com.abuunity.shareon.R;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder>{

    private Context context;
    private List<Steps> stepsList;
    private Steps[] getImageUrl;
    HashMap<Integer, String> map = new HashMap<>();

    public StepsAdapter(Context context, List<Steps> stepsList) {
        this.context = context;
        this.stepsList = stepsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_steps, parent, false);
        return new StepsAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return stepsList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Steps steps = stepsList.get(position);
        holder.setData(steps, position);
        holder.setListeners();

        if(stepsList.size()==1){
            holder.imgDelete.setVisibility(View.GONE);
        }else {
            holder.imgDelete.setVisibility(View.VISIBLE);
        }

        holder.inputSteps.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(v.hasFocus()){
                    holder.inputCounter.setVisibility(View.VISIBLE);
                    holder.inputSteps.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if(s.length() != 0)
                                holder.inputCounter.setText(s.length()+"/64");
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                                map.put(position, holder.inputSteps.getText().toString());
                                System.out.println(map);

                        }
                    });
                } else {
                    holder.inputCounter.setVisibility(View.GONE);
                }

            }
        });



    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;
        public ImageView imgDelete, imgCopy;
        public int position;
        public Steps currentObject;
        public SocialAutoCompleteTextView inputSteps;
        public TextView inputCounter;
        public ImageView imageSteps;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.index_no);
            imgDelete = itemView.findViewById(R.id.image_delete);
            imgCopy = itemView.findViewById(R.id.image_add);
            inputSteps = itemView.findViewById(R.id.input_steps);
            inputCounter = itemView.findViewById(R.id.input_counter);
            imageSteps = itemView.findViewById(R.id.image_steps);
        }

        public void setData(Steps currentObject, int position) {
            this.title.setText(String.valueOf(position+1));
            this.inputSteps.setHint(currentObject.getTools());
            this.imageSteps.setImageResource(currentObject.getImageId());
            this.position = position;
            this.currentObject = currentObject;
        }

        public void setListeners() {
            imageSteps.setOnClickListener(ViewHolder.this);
            imgDelete.setOnClickListener(ViewHolder.this);
            imgCopy.setOnClickListener(ViewHolder.this);
            title.setOnClickListener(ViewHolder.this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.image_steps:
                    getImageItem(position);
                    break;

                case R.id.image_delete:
                    removeItem(position);
                    break;

                case R.id.image_add:
                    addItem(position, currentObject);
                    break;
            }
        }
    }


    public void getImageItem(int position) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra("positionsId", position);
        ((Activity) context).startActivityForResult(intent, 26);
    }

    public void removeItem(int position) {
        stepsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, stepsList.size());
		notifyDataSetChanged();
    }

    public void addItem(int position, Steps currentObject) {
        stepsList.add(position, currentObject);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, stepsList.size());
		notifyDataSetChanged();
    }
}
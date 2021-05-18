package com.abuunity.shareon.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.abuunity.shareon.Model.Tools;
import com.abuunity.shareon.R;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.List;

public class ToolsAdapter extends RecyclerView.Adapter<ToolsAdapter.ViewHolder>{

    private List<Tools> toolsList;
    private LayoutInflater inflater;


    public ToolsAdapter(Context context, List<Tools> toolsList) {
        inflater = LayoutInflater.from(context);
        this.toolsList = toolsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_tools, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return toolsList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tools tools = toolsList.get(position);
        holder.setData(tools, position);
        holder.setListeners();

        holder.inputTools.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(v.hasFocus()){
                    holder.inputCounter.setVisibility(View.VISIBLE);
                    holder.inputTools.addTextChangedListener(new TextWatcher() {
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

                        }
                    });
                } else {
                    holder.inputCounter.setVisibility(View.GONE);
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title;
        private ImageView imgDelete, imgCopy;
        private int position;
        private Tools currentObject;
        public SocialAutoCompleteTextView inputTools;
        public TextView inputCounter;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.index_no);
            imgDelete = itemView.findViewById(R.id.image_delete);
            imgCopy = itemView.findViewById(R.id.image_add);
            inputTools = itemView.findViewById(R.id.input_tools);
            inputCounter = itemView.findViewById(R.id.input_counter);
        }

        public void setData(Tools currentObject, int position) {
            this.title.setText(String.valueOf(position+1));
            this.inputTools.setHint(currentObject.getTools());
            this.position = position;
            this.currentObject = currentObject;
        }

        public void setListeners() {
            imgDelete.setOnClickListener(ViewHolder.this);
            imgCopy.setOnClickListener(ViewHolder.this);
            title.setOnClickListener(ViewHolder.this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.image_delete:
                    if(toolsList.size()>1){
                        removeItem(position);
                    }

                    break;
                case R.id.image_add:
                    addItem(position, currentObject);
                    break;
            }
        }
    }

    public void removeItem(int position) {
        toolsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, toolsList.size());
		notifyDataSetChanged();
    }

    public void addItem(int position, Tools currentObject) {
        toolsList.add(position, currentObject);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, toolsList.size());
		notifyDataSetChanged();
    }
}
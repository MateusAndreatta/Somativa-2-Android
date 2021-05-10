package com.mateusandreatta.somativa2;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private static ClickListener clickListener;

    public void setClickListener(ClickListener clickListener){
        ProductAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View view);
        boolean onItemLongClick(int position,View view);
    }


    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.card_item,
                        parent,false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        Product c = DataModel.getInstance().getProducts().get(position);
        Log.i("[PRODUCT ADAPTER]", c.getPhotoPath());
        holder.textView.setText(StringUtils.capitalize( c.getName() + " - R$ " + c.getPrice() + ",00"));
        holder.imageView.setImageURI(Uri.parse(c.getPhotoPath()));
    }

    @Override
    public int getItemCount() {
        return DataModel.getInstance().getProducts().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener == null)
                        return;
                    clickListener.onItemClick(getAdapterPosition(),view);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(clickListener == null)
                        return false;

                    return clickListener.onItemLongClick(getAdapterPosition(),view);
                }
            });
        }
    }
}
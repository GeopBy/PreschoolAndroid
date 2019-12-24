package com.example.preschool.PhotoAlbum;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.preschool.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterGripViewPhoto extends RecyclerView.Adapter<AdapterGripViewPhoto.viewHolder> {

    private Context context;
    private ArrayList<String> imageUrls;

    AdapterGripViewPhoto(Context context, ArrayList<String> imageUrls){
        this.imageUrls=imageUrls;
        this.context=context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.photo_grid_view, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        Picasso.get().load(imageUrls.get(position))
//                .networkPolicy(NetworkPolicy.NO_CACHE)
//                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .placeholder(R.drawable.ic_photo_black_50dp)
                .resize(400,0)
                .into(holder.imageView);
//        Picasso.get().load(imageUrls.get(position)).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ViewPhotoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("IMAGE_LINK",imageUrls);
                                intent.putExtra("POSITION",position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.img_photo_item);
        }
    }
}

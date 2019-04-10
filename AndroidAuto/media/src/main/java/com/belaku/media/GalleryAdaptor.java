package com.belaku.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by naveenprakash on 10/04/19.
 */

class GalleryAdaptor extends RecyclerView.Adapter<GalleryAdaptor.MyViewHolder> {

    private ArrayList<String> imgPaths;
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imgView;
        public MyViewHolder(ImageView v) {
            super(v);
            imgView = v;
        }
    }

    public GalleryAdaptor(MainActivity mainActivity, ArrayList<String> listOfAllImages) {
        this.imgPaths = listOfAllImages;
    }

    @Override
    public GalleryAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        ImageView v = (ImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_gallery_item_layout, parent, false);

        GalleryAdaptor.MyViewHolder vh = new GalleryAdaptor.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(GalleryAdaptor.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (imgPaths.get(position) != null) {
            Bitmap bmImg = BitmapFactory.decodeFile(imgPaths.get(position));
            holder.imgView.setImageBitmap(bmImg);
        }

    }


    @Override
    public int getItemCount() {
        return imgPaths.size();
    }
}

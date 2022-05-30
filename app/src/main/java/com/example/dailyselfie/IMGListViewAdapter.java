package com.example.dailyselfie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class IMGListViewAdapter extends BaseAdapter {
    final ArrayList<IMG> listIMG;

    public IMGListViewAdapter(ArrayList<IMG> listIMG) {
        this.listIMG = listIMG;
    }

    @Override
    public int getCount() {
        return listIMG.size();
    }

    @Override
    public Object getItem(int position) {
        return listIMG.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewImg;
        if (convertView == null) {
            viewImg = View.inflate(parent.getContext(), R.layout.list_view, null);
        } else viewImg = convertView;
        IMG img = listIMG.get(position);
        ((TextView) viewImg.findViewById(R.id.txtFileName)).setText(img.name);
        ImageView imgView = viewImg.findViewById(R.id.imgV_img);
        byte[] tp = img.img;
        Bitmap bitmap = BitmapFactory.decodeByteArray(tp,0,tp.length);
        imgView.setImageBitmap(bitmap);
        return viewImg;
    }
}

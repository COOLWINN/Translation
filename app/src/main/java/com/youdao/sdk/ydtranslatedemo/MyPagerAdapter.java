package com.youdao.sdk.ydtranslatedemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyPagerAdapter extends PagerAdapter {

    private List<String> mData;
    private Context mContext;

    MyPagerAdapter(List<String> data, Context context) {
        mData = data;
        mContext = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View inflate = LayoutInflater.from(container.getContext()).inflate(R.layout.cardviewpager_item, container, false);
        ImageView strokeImageView = inflate.findViewById(R.id.img_item);
        TextView indexTextView = inflate.findViewById(R.id.tv_index);
        Glide.with(mContext).load(mData.get(position)).into(strokeImageView);
        indexTextView.setText(String.format("%s / %s", position + 1, getCount()));
        container.addView(inflate);
        return inflate;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(((View) object));
    }
}

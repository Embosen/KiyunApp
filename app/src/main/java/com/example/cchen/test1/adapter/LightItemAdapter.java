package com.example.cchen.test1.adapter;

import android.app.Activity;
import android.content.res.Resources;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchen.test1.R;
import com.example.cchen.test1.model.ToggleItem;


/**
 * Created by cchen on 2016/6/18.
 */
public class LightItemAdapter extends RecyclerView.Adapter<LightItemAdapter.ViewHolder> {

    private final Activity mActivity;
    private final int mIdi;
    private int itemW;
    private ToggleItem[] items;

    public LightItemAdapter(Activity mainActivity, ToggleItem[] list , int imageDrawableId) {
        mActivity = mainActivity;
        items = list;
        mIdi = imageDrawableId;

        Resources resources = mActivity.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int screenWidth = dm.widthPixels;

        if (items == null || items.length == 0) return;
        itemW = (screenWidth - mActivity.getResources().getDimensionPixelSize(R.dimen.light_list_margin) * 2) / items.length;
//        Log.d("cchen", screenWidth + " wwwww " + itemW);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        TextView mTxt;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }


    @Override
    public LightItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.light_item,
                parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        if (itemW != 0) view.getLayoutParams().width = itemW;

        viewHolder.mImg = (ImageView) view.findViewById(R.id.img_light);
        viewHolder.mTxt = (TextView) view.findViewById(R.id.txt_light);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LightItemAdapter.ViewHolder holder, int position) {
        holder.mImg.setImageResource(mIdi);
        holder.mTxt.setText(items[position].name);

        holder.mImg.setSelected(items[position].isOn);//test
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.length;
    }
}

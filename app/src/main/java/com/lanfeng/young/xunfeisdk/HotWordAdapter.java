package com.lanfeng.young.xunfeisdk;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yf on 2018/8/27.
 */
class HotWordAdapter extends RecyclerView.Adapter<HotWordAdapter.BaseViewHolder> {
    private List<String> list;
    public HotWordAdapter(List<String> list) {
        this.list =list;
        Log.e("TAG", "HotWordAdapter: "+list.size() );
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_auto_poll, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.tv.setText(list.get(position));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder{
        TextView tv;
        public BaseViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_content);
        }
    }
}

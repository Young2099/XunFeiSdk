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
class VoiceAdapter extends RecyclerView.Adapter<VoiceAdapter.BaseViewHolder> {
    private List<RawMessage> list;
    public VoiceAdapter(List<RawMessage> list) {
        this.list =list;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voice, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.voice.setText(list.get(position).getVoice());
        holder.message.setText(list.get(position).getMessage());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder{
        TextView message;
        TextView voice;
        public BaseViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            voice = itemView.findViewById(R.id.voice);
        }
    }
}

package com.lanfeng.young.xunfeisdk;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yf on 2018/8/27.
 */
class VoiceAdapter extends RecyclerView.Adapter<VoiceAdapter.BaseViewHolder> {
    private List<RawMessage> list;
    private Context mContext;
    private List<WeatherEntity> weatherEntities;

    public VoiceAdapter(List<RawMessage> list, MainActivity mainActivity) {
        this.list = list;
        mContext = mainActivity;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voice, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (!TextUtils.isEmpty(list.get(position).getVoice())) {
            holder.voice.setVisibility(View.VISIBLE);
            holder.voice.setText(list.get(position).getVoice());
        } else {
            holder.voice.setVisibility(View.GONE);
        }
        holder.message.setText(list.get(position).getMessage());
        if (list.get(position).getIntent() != null) {
            if ("weather".equals(list.get(position).getIntent())) {
                List<WeatherEntity> weatherEntities = getData(list.get(position).getJsonObject());
                holder.today_weather.setVisibility(View.VISIBLE);
                holder.mWeatherRecycler.setLayoutManager(new LinearLayoutManager(mContext));
                WeatherAdapter adapter = new WeatherAdapter(weatherEntities);
                holder.mWeatherRecycler.setAdapter(adapter);
                if (weatherEntities != null && weatherEntities.size() != 0) {
                    holder.city.setText(weatherEntities.get(0).getCity());
                    holder.weather_detail.setText(weatherEntities.get(0).getWeather());
                    holder.airQuality.setText(weatherEntities.get(0).getAir());
                    holder.tempRange.setText(weatherEntities.get(0).getTempRange());
                    holder.temp.setText(weatherEntities.get(0).getTemp());
                    holder.wind.setText(weatherEntities.get(0).getWind());
                    GlideApp.with(mContext).load(weatherEntities.get(0).getImg()).into(holder.weather_icon);
                }
            }
        }

    }

    private List<WeatherEntity> getData(JsonObject jsonObject) {
        weatherEntities = new ArrayList<>();
        WeatherEntity weatherEntity;
        JsonArray data = jsonObject.getAsJsonArray("result");
        for (int i = 0; i < data.size(); i++) {
            Gson objectMapper = new Gson();
            weatherEntity = objectMapper.fromJson(data.get(i).getAsJsonObject(), WeatherEntity.class);
            Log.e("TAG", "getData: " + weatherEntity.toString());
            weatherEntities.add(weatherEntity);
        }
        if (weatherEntities == null) {
            return null;
        }
        return weatherEntities;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView voice;
        TextView weather;
        RelativeLayout today_weather;
        RecyclerView mWeatherRecycler;
        TextView time;
        TextView city;
        TextView temp;
        TextView airQuality;
        TextView wind;
        TextView weather_detail;
        TextView tempRange;
        ImageView weather_icon;

        public BaseViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            voice = itemView.findViewById(R.id.voice);
            weather = itemView.findViewById(R.id.weather);
            today_weather = itemView.findViewById(R.id.today_weather);
            mWeatherRecycler = itemView.findViewById(R.id.weather_recyclerview);
            time = itemView.findViewById(R.id.time);
            city = itemView.findViewById(R.id.city);
            temp = itemView.findViewById(R.id.temp);
            airQuality = itemView.findViewById(R.id.air);
            wind = itemView.findViewById(R.id.wind);
            weather_detail = itemView.findViewById(R.id.weather_detail);
            tempRange = itemView.findViewById(R.id.tem_range);
            weather_icon = itemView.findViewById(R.id.weather_icon);
        }
    }
}

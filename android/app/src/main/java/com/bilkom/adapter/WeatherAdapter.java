package com.bilkom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bilkom.R;
import com.bilkom.model.WeatherForecast;
import com.bilkom.utils.WeatherIconUtils;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private WeatherForecast weatherForecast;
    private Context context;

    public WeatherAdapter(Context context) {
        this.context = context;
    }

    public void setWeatherForecast(WeatherForecast weatherForecast) {
        this.weatherForecast = weatherForecast;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        if (weatherForecast != null) {
            // Set weather description
            holder.weatherDescription.setText(weatherForecast.getDescription());
            
            // Set temperature
            holder.temperatureText.setText(String.format("%.1f°C", weatherForecast.getTemperature()));
            
            // Set weather icon
            int iconResourceId = WeatherIconUtils.getWeatherIconResourceId(context, weatherForecast.getIcon());
            holder.weatherIcon.setImageResource(iconResourceId);
        }
    }

    @Override
    public int getItemCount() {
        return weatherForecast != null ? 1 : 0;
    }

    static class WeatherViewHolder extends RecyclerView.ViewHolder {
        ImageView weatherIcon;
        TextView weatherDescription;
        TextView temperatureText;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
            weatherDescription = itemView.findViewById(R.id.weatherDescription);
            temperatureText = itemView.findViewById(R.id.temperatureText);
        }
    }
} 
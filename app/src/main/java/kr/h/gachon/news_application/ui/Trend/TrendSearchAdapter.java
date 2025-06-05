package kr.h.gachon.news_application.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.h.gachon.news_application.R;
import kr.h.gachon.news_application.network.model.TrendSearchDTO;

public class TrendSearchAdapter extends RecyclerView.Adapter<TrendSearchAdapter.ViewHolder> {

    private List<TrendSearchDTO> items = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trend_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrendSearchDTO item = items.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvUrl.setText(item.getUrl());
        holder.tvScore.setText(String.format("유사도: %.3f", item.getCosineScore()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setData(List<TrendSearchDTO> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvUrl, tvScore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.trendTitle);
            tvUrl   = itemView.findViewById(R.id.trendUrl);
            tvScore = itemView.findViewById(R.id.trendScore);
        }
    }
}

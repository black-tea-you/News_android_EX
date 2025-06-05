// package kr.h.gachon.news_application.adapter;

package kr.h.gachon.news_application.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    public TrendSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trend_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendSearchAdapter.ViewHolder holder, int position) {
        final TrendSearchDTO item = items.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvScore.setText(String.format("유사도: %.3f", item.getScore()));

        // 링크를 클릭하면 외부 브라우저에서 열도록 Intent 설정
        holder.tvLink.setText(item.getLink());
        holder.tvLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = item.getLink();
                if (url != null && !url.isEmpty()) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    view.getContext().startActivity(i);
                } else {
                    Toast.makeText(view.getContext(), "유효하지 않은 링크입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        // items가 null이면 0, 아니면 크기 반환
        return (items == null) ? 0 : items.size();
    }

    /** 외부에서 데이터 갱신 시 호출 **/
    public void setData(List<TrendSearchDTO> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLink, tvScore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvLink  = itemView.findViewById(R.id.tvItemLink);
            tvScore = itemView.findViewById(R.id.tvItemScore);
        }
    }
}


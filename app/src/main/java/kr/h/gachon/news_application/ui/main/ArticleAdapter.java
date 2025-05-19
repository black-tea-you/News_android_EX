package kr.h.gachon.news_application.ui.main;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


// URL 이미지용
import com.bumptech.glide.Glide;
import kr.h.gachon.news_application.R;

import kr.h.gachon.news_application.databinding.ItemArticleBinding;
import kr.h.gachon.news_application.network.model.News;

import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.VH> {

    private final List<News> items = new ArrayList<>();

    /** 외부에서 리스트 갱신할 때 호출 */
    public void submitList(List<News> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemArticleBinding binding = ItemArticleBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        private final ItemArticleBinding binding;

        VH(ItemArticleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(News article) {
            binding.tvTitle.setText(article.getTitle());
            // Glide로 URL에서 이미지 로드
            Glide.with(binding.tvImage.getContext())
                    .load(article.getUrlimg())      // String URL
                    .placeholder(R.drawable.reload) // 로딩 중 대체 이미지
                    .error(R.drawable.error)           // 실패 시 대체 이미지
                    .into(binding.tvImage);

            binding.tvDesc.setText(article.getDescription());
        }
    }
}

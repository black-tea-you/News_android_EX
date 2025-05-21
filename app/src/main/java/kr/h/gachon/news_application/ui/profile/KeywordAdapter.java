package kr.h.gachon.news_application.ui.profile;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.h.gachon.news_application.databinding.ItemKeywordBinding;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.VH> {
    private final List<String> items = new ArrayList<>();

    /** 외부에서 키워드 리스트 갱신 시 호출 */
    public void submitList(List<String> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemKeywordBinding binding = ItemKeywordBinding.inflate(
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
        private final ItemKeywordBinding binding;

        public VH(ItemKeywordBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String keyword) {
            binding.tvKeyword.setText(keyword);
        }
    }
}

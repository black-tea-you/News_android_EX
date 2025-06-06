// package kr.h.gachon.news_application.ui.main;

package kr.h.gachon.news_application.ui.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.h.gachon.news_application.R;
import kr.h.gachon.news_application.databinding.ItemArticleBinding;
import kr.h.gachon.news_application.network.model.News;
import kr.h.gachon.news_application.viewmodel.ScrapViewModel;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.VH> {

    private final List<News> items = new ArrayList<>();
    // ① 현재 사용자가 스크랩한 뉴스 ID를 담는 Set
    private final Set<Long> scrapedIds = new HashSet<>();

    /** 스크랩/취소 버튼 클릭 시 호출할 콜백 인터페이스 **/
    public interface OnScrapToggleListener {
        void onToggle(News news, boolean isScraped);
    }

    private final ScrapViewModel scrapViewModel;
    private final OnScrapToggleListener scrapeToggleListener;

    /**
     * @param scrapViewModel         : 스크랩/취소 API 호출을 담당하는 ViewModel
     * @param scrapeToggleListener   : 버튼 클릭 시 액티비티/프래그먼트 쪽에서 처리할 콜백
     */
    public ArticleAdapter(ScrapViewModel scrapViewModel, OnScrapToggleListener scrapeToggleListener) {
        this.scrapViewModel = scrapViewModel;
        this.scrapeToggleListener = scrapeToggleListener;
    }

    /** 외부에서 News 리스트를 전달할 때 호출 **/
    public void submitList(List<News> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        notifyDataSetChanged();
    }

    /**
     * ② 외부(액티비티)에서 스크랩된 ID 집합이 바뀔 때 호출
     */
    public void setScrapedIds(Set<Long> ids) {
        scrapedIds.clear();
        if (ids != null) {
            scrapedIds.addAll(ids);
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

    class VH extends RecyclerView.ViewHolder {
        private final ItemArticleBinding binding;

        VH(ItemArticleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(News article) {
            // 1) 제목 세팅
            binding.tvTitle.setText(article.getTitle());

            // 2) 이미지 로드 (Glide)
            Glide.with(binding.tvImage.getContext())
                    .load(article.getUrlimg())
                    .placeholder(R.drawable.reload)
                    .error(R.drawable.error)
                    .into(binding.tvImage);

            // 3) 요약 텍스트 세팅
            binding.tvDesc.setText(article.getSummary());

            // 4) 현재 이 뉴스(article.id)가 스크랩된 상태인지 확인
            boolean isScraped = scrapedIds.contains(article.getId());
            // 버튼 텍스트를 “스크랩” or “취소” 로 설정
            binding.btnScrap.setText(isScraped ? "취소" : "스크랩");
            // 버튼을 항상 활성화 상태로 두되, 클릭 시 로직 동작
            binding.btnScrap.setEnabled(true);

            // 5) 스크랩/취소 버튼 클릭 리스너
            binding.btnScrap.setOnClickListener(v -> {
                // 버튼 클릭 직후 UI 피드백: 비활성화, 텍스트 변경(요청 중) 등
                binding.btnScrap.setEnabled(false);
                binding.btnScrap.setText(isScraped ? "취소 중..." : "스크랩 중...");

                // 실제 “스크랩 or 취소” 동작을 액티비티 쪽에 위임
                scrapeToggleListener.onToggle(article, isScraped);
            });
        }
    }
}


package kr.h.gachon.news_application.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import kr.h.gachon.news_application.databinding.SearchBinding;
import kr.h.gachon.news_application.network.model.News;
import kr.h.gachon.news_application.network.model.SearchResult;
import kr.h.gachon.news_application.ui.main.ArticleAdapter;
import kr.h.gachon.news_application.viewmodel.SearchViewModel;

public class Search extends ComponentActivity {
    private SearchBinding binding;
    private SearchViewModel viewModel;
    private ArticleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1) View Binding 초기화
        binding = SearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2) RecyclerView + ArticleAdapter(기존 News 전용) 설정
        adapter = new ArticleAdapter();
        binding.rvResults.setLayoutManager(new LinearLayoutManager(this));
        binding.rvResults.setAdapter(adapter);

        // 3) ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        // 4) LiveData 관찰: SearchResult 리스트 → 내부의 News만 추출 → adapter.submitList(onlyNews)
        viewModel.getSearchResults().observe(this, new Observer<List<SearchResult>>() {
            @Override
            public void onChanged(List<SearchResult> results) {
                if (results == null) {
                    // 서버 호출 전이거나 널 상태인 경우, 그냥 리턴
                    return;
                }

                // SearchResult → News 로 변환
                List<News> onlyNews = new ArrayList<>();
                for (SearchResult sr : results) {
                    onlyNews.add(sr.getNews());
                }

                if (onlyNews.isEmpty()) {
                    Toast.makeText(
                            Search.this,
                            "검색 결과가 없습니다.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
                // 기존 ArticleAdapter (List<News> 전용)에 News 리스트만 넘김
                adapter.submitList(onlyNews);
            }
        });

        // 5) 에러 메시지 관찰: 네트워크/API 에러 시 토스트로 알림
        viewModel.getErrorMessage().observe(this, err -> {
            if (err != null && !err.isEmpty()) {
                Toast.makeText(
                        Search.this,
                        "오류 발생: " + err,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // 6) 검색 버튼 클릭 리스너: 입력값 검증 후 ViewModel.search() 호출
        binding.btnSearch.setOnClickListener(v -> {
            String category = binding.etCategory.getText().toString().trim();
            String keyword  = binding.etKeyword.getText().toString().trim();
            int topK        = 15; // 기본값

            if (TextUtils.isEmpty(category) || TextUtils.isEmpty(keyword)) {
                Toast.makeText(
                        Search.this,
                        "카테고리와 키워드를 모두 입력하세요.",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }
            viewModel.search(category, keyword, topK);
        });
    }
}

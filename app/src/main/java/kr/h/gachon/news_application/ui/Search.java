package kr.h.gachon.news_application.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.h.gachon.news_application.databinding.SearchBinding;
import kr.h.gachon.news_application.network.model.News;
import kr.h.gachon.news_application.network.model.SearchResult;
import kr.h.gachon.news_application.ui.main.ArticleAdapter;
import kr.h.gachon.news_application.viewmodel.ScrapViewModel;
import kr.h.gachon.news_application.viewmodel.SearchViewModel;
import kr.h.gachon.news_application.util.TokenManager;

public class Search extends ComponentActivity {
    private SearchBinding binding;
    private SearchViewModel viewModel;
    private ScrapViewModel scrapViewModel;
    private ArticleAdapter adapter;

    // Search Activity 내에서도 로컬 스크랩 ID 집합을 관리
    private final Set<Long> localScrapedIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1) ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        scrapViewModel = new ViewModelProvider(this).get(ScrapViewModel.class);

        // 2) RecyclerView + LayoutManager 설정
        binding.rvResults.setLayoutManager(new LinearLayoutManager(this));

        // 3) ArticleAdapter 생성: 로컬 scrapedIds와 onToggle 콜백 전달
        adapter = new ArticleAdapter(
                scrapViewModel,
                new ArticleAdapter.OnScrapToggleListener() {
                    @Override
                    public void onToggle(News news, boolean isScraped) {
                        long newsId = news.getId();

                        if (!isScraped) {
                            // ▶ 즉시 UI 토글: 로컬에 추가 후 갱신
                            localScrapedIds.add(newsId);
                            adapter.setScrapedIds(localScrapedIds);

                            // ▶ 서버에 스크랩 요청
                            scrapViewModel.addScrap(newsId, success -> {
                                runOnUiThread(() -> {
                                    if (!success) {
                                        // 실패 시 롤백
                                        localScrapedIds.remove(newsId);
                                        adapter.setScrapedIds(localScrapedIds);
                                        Toast.makeText(Search.this, "스크랩 실패", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Search.this, "스크랩 완료", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });
                        } else {
                            // ▶ 즉시 UI 토글: 로컬에서 제거 후 갱신
                            localScrapedIds.remove(newsId);
                            adapter.setScrapedIds(localScrapedIds);

                            // ▶ 서버에 스크랩 취소 요청
                            scrapViewModel.deleteScrap(newsId, success -> {
                                runOnUiThread(() -> {
                                    if (!success) {
                                        // 실패 시 롤백
                                        localScrapedIds.add(newsId);
                                        adapter.setScrapedIds(localScrapedIds);
                                        Toast.makeText(Search.this, "스크랩 취소 실패", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Search.this, "스크랩 취소 완료", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });
                        }
                    }
                }
        );
        binding.rvResults.setAdapter(adapter);

        // 4) LiveData 옵저빙: 검색 결과 → News 리스트 → adapter.submitList(...)
        viewModel.getSearchResults().observe(this, new Observer<List<SearchResult>>() {
            @Override
            public void onChanged(List<SearchResult> results) {
                if (results == null) return;

                List<News> onlyNews = new ArrayList<>();
                for (SearchResult sr : results) {
                    onlyNews.add(sr.getNews());
                }
                if (onlyNews.isEmpty()) {
                    Toast.makeText(Search.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                }
                adapter.submitList(onlyNews);
            }
        });

        // 5) LiveData 옵저빙: 서버에서 가져온 스크랩된 뉴스 목록 → 로컬 scrapedIds 초기화 및 adapter 갱신
        scrapViewModel.getScrapList().observe(this, new Observer<List<News>>() {
            @Override
            public void onChanged(List<News> scrapList) {
                localScrapedIds.clear();
                if (scrapList != null) {
                    for (News n : scrapList) {
                        localScrapedIds.add(n.getId());
                    }
                }
                adapter.setScrapedIds(localScrapedIds);
            }
        });
        scrapViewModel.getErrorMsg().observe(this, err -> {
            if (err != null) {
                Toast.makeText(Search.this, "스크랩 목록 로드 오류: " + err, Toast.LENGTH_SHORT).show();
            }
        });

        // 6) 검색 버튼 클릭 리스너
        binding.btnSearch.setOnClickListener(v -> {
            String category = binding.etCategory.getText().toString().trim();
            String keyword = binding.etKeyword.getText().toString().trim();
            int topK = 15;

            if (TextUtils.isEmpty(category) || TextUtils.isEmpty(keyword)) {
                Toast.makeText(Search.this, "카테고리와 키워드를 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.search(category, keyword, topK);
        });

        // 7) 초기 진입 시(로그인되어 있으면) 서버에서 스크랩 목록 불러오기
        if (TokenManager.getInstance(this).getToken() != null) {
            scrapViewModel.fetchScraps();
        }
    }
}

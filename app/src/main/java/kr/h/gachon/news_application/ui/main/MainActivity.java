package kr.h.gachon.news_application.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.h.gachon.news_application.R;
import kr.h.gachon.news_application.databinding.ActivityMainBinding;
import kr.h.gachon.news_application.network.model.News;
import kr.h.gachon.news_application.ui.Search;
import kr.h.gachon.news_application.ui.trend.TrendSearchActivity;
import kr.h.gachon.news_application.ui.login.LoginActivity;
import kr.h.gachon.news_application.ui.profile.ProfileActivity;
import kr.h.gachon.news_application.util.TokenManager;
import kr.h.gachon.news_application.viewmodel.NewsViewModel;
import kr.h.gachon.news_application.viewmodel.ScrapViewModel;

public class MainActivity extends ComponentActivity {
    private ActivityMainBinding binding;
    private NewsViewModel newsViewModel;
    private ScrapViewModel scrapViewModel;
    private ArticleAdapter adapter;
    private Animation loadingAnim;

    // 로그인 결과 처리용 런처
    private ActivityResultLauncher<Intent> loginLauncher;

    // ① Activity 내에서 관리할 "로컬 스크랩 ID 집합"
    private final Set<Long> localScrapedIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1) 로그인 런처 초기화
        loginLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String userName = result.getData().getStringExtra("username");
                        binding.tvUsername.setText("안녕하세요, " + userName + "님");
                        setAuthButtons(true);
                        // 로그인 직후, 서버에서 스크랩된 ID를 가져와서 localScrapedIds 초기화
                        scrapViewModel.fetchScraps();
                    }
                }
        );

        // 2) 로그인 여부에 따른 UI 세팅
        boolean loggedIn = TokenManager.getInstance(this).getToken() != null;
        setAuthButtons(loggedIn);

        // 3) 버튼 리스너 등록 (로그인/로그아웃/프로필/검색/트렌드)
        binding.btnGoLogin.setOnClickListener(v ->
                loginLauncher.launch(new Intent(this, LoginActivity.class))
        );
        binding.btnLogout.setOnClickListener(v -> {
            TokenManager.getInstance(this).clearToken();
            binding.tvUsername.setText("");
            setAuthButtons(false);
            // 로그아웃 시 로컬 세트도 비우고 UI 갱신
            localScrapedIds.clear();
            adapter.setScrapedIds(localScrapedIds);
        });
        binding.btnProfile.setOnClickListener(v -> {
            if (TokenManager.getInstance(this).getToken() == null) {
                loginLauncher.launch(new Intent(this, LoginActivity.class));
            } else {
                startActivity(new Intent(this, ProfileActivity.class));
            }
        });
        binding.button3.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, Search.class))
        );
        binding.button4.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, TrendSearchActivity.class))
        );

        // 4) 로딩 애니메이션 준비
        loadingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);

        // 5) ViewModel 초기화
        newsViewModel  = new ViewModelProvider(this).get(NewsViewModel.class);
        scrapViewModel = new ViewModelProvider(this).get(ScrapViewModel.class);

        // 6) RecyclerView에 LayoutManager 설정
        binding.rvArticles.setLayoutManager(new LinearLayoutManager(this));

        // 7) ArticleAdapter 생성: 로컬 scrapedIds와 onToggle 콜백 전달
        adapter = new ArticleAdapter(
                scrapViewModel,     // 실제 서버 호출용 ViewModel
                new ArticleAdapter.OnScrapToggleListener() {
                    @Override
                    public void onToggle(News news, boolean isScraped) {
                        long newsId = news.getId();

                        if (!isScraped) {
                            // ▶ 1) 버튼 토글: 로컬에서 바로 "스크랩 상태"로 표시
                            localScrapedIds.add(newsId);
                            adapter.setScrapedIds(localScrapedIds);

                            // ▶ 2) 서버에 스크랩 요청 (비동기로 진행)
                            scrapViewModel.addScrap(newsId, success -> {
                                runOnUiThread(() -> {
                                    if (!success) {
                                        // 실패하면 로컬 집합에서 롤백하고 UI 복원
                                        localScrapedIds.remove(newsId);
                                        adapter.setScrapedIds(localScrapedIds);
                                        Toast.makeText(MainActivity.this, "스크랩 실패", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // 성공 시에는 서버에서 받아오는 최신 목록을 다시 로컬에 덮어써도 좋지만,
                                        // 이미 로컬에 반영했으므로 굳이 fetchScraps()는 선택 사항입니다.
                                        Toast.makeText(MainActivity.this, "스크랩 완료", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });
                        } else {
                            // ▶ 1) 버튼 토글: 로컬에서 바로 "스크랩 취소 상태"로 표시
                            localScrapedIds.remove(newsId);
                            adapter.setScrapedIds(localScrapedIds);

                            // ▶ 2) 서버에 스크랩 취소 요청 (비동기로 진행)
                            scrapViewModel.deleteScrap(newsId, success -> {
                                runOnUiThread(() -> {
                                    if (!success) {
                                        // 실패하면 로컬 집합에 다시 추가 후 UI 복원
                                        localScrapedIds.add(newsId);
                                        adapter.setScrapedIds(localScrapedIds);
                                        Toast.makeText(MainActivity.this, "스크랩 취소 실패", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "스크랩 취소 완료", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });
                        }
                    }
                }
        );
        binding.rvArticles.setAdapter(adapter);

        // 8) 전체 뉴스 옵저빙
        newsViewModel.getHeadlines().observe(this, newsList -> {
            hideLoading();
            if (newsList != null) {
                adapter.submitList(newsList);
                binding.textBar.setText("총 " + newsList.size() + "개의 기사를 가져왔습니다.");
            }
        });
        newsViewModel.getError().observe(this, err -> {
            hideLoading();
            binding.textBar.setText("Error: " + err);
        });

        // 9) 서버에서 실제 스크랩된 리스트를 받아 로컬 Set에 동기화
        scrapViewModel.getScrapList().observe(this, scrapList -> {
            // scrapList는 서버에서 내려준 “내가 스크랩한 뉴스 전체” List<News>
            localScrapedIds.clear();
            if (scrapList != null) {
                for (News n : scrapList) {
                    localScrapedIds.add(n.getId());
                }
            }
            adapter.setScrapedIds(localScrapedIds);
        });
        scrapViewModel.getErrorMsg().observe(this, err -> {
            if (err != null) {
                Toast.makeText(MainActivity.this, "스크랩 목록 로드 오류: " + err, Toast.LENGTH_SHORT).show();
            }
        });

        // 10) 새로고침 버튼
        binding.btnRefresh.setOnClickListener(v -> {
            showLoading();
            newsViewModel.loadHeadlines();
            if (TokenManager.getInstance(this).getToken() != null) {
                scrapViewModel.fetchScraps();
            }
        });

        // 11) 화면 처음 진입 시: 뉴스와 스크랩 목록을 동시에 가져오기
        showLoading();
        newsViewModel.loadHeadlines();
        if (loggedIn) {
            scrapViewModel.fetchScraps();
        }
    }

    private void setAuthButtons(boolean loggedIn) {
        binding.btnGoLogin.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
        binding.btnLogout.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
    }

    private void showLoading() {
        binding.ivLoading.setVisibility(View.VISIBLE);
        binding.ivLoading.startAnimation(loadingAnim);
    }

    private void hideLoading() {
        binding.ivLoading.clearAnimation();
        binding.ivLoading.setVisibility(View.GONE);
    }
}

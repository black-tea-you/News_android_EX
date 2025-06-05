package kr.h.gachon.news_application.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import kr.h.gachon.news_application.R;
import kr.h.gachon.news_application.databinding.ActivityMainBinding;
import kr.h.gachon.news_application.network.model.News;
import kr.h.gachon.news_application.ui.Search;
import kr.h.gachon.news_application.ui.trend.TrendSearchActivity;
import kr.h.gachon.news_application.ui.login.LoginActivity;
import kr.h.gachon.news_application.ui.profile.ProfileActivity;
import kr.h.gachon.news_application.util.TokenManager;
import kr.h.gachon.news_application.viewmodel.NewsViewModel;

public class MainActivity extends ComponentActivity {
    private ActivityMainBinding binding;
    private NewsViewModel vm;
    private ArticleAdapter adapter;
    private Animation loadingAnim;

    // 1) 런처는 한 번만 선언 & 초기화
    private ActivityResultLauncher<Intent> loginLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2) 런처 초기화 (onCreate 맨 위에서 한 번만)
        loginLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String userName = result.getData().getStringExtra("username");
                        binding.tvUsername.setText("안녕하세요, " + userName + "님");
                        setAuthButtons(true);
                    }
                }
        );

        // 3) 초기 UI 세팅: 토큰 여부에 따라 로그인/로그아웃 버튼 토글
        boolean loggedIn = TokenManager.getInstance(this).getToken() != null;
        setAuthButtons(loggedIn);

        // 4) 버튼 리스너 등록
        binding.btnGoLogin.setOnClickListener(v ->
                loginLauncher.launch(new Intent(this, LoginActivity.class))
        );

        binding.btnLogout.setOnClickListener(v -> {
            TokenManager.getInstance(this).clearToken();
            binding.tvUsername.setText("");
            setAuthButtons(false);
        });

        binding.btnProfile.setOnClickListener(v -> {
            if (TokenManager.getInstance(this).getToken() == null) {
                // 로그인 안 된 상태 → 로그인 화면으로
                loginLauncher.launch(new Intent(this, LoginActivity.class));
            } else {
                // 로그인 되어 있으면 프로필 화면으로
                startActivity(new Intent(this, ProfileActivity.class));
            }
        });

        //search 확인 버튼
        binding.button3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Search.class);
            startActivity(intent);
        });

        //트렌드 확인 버튼
        binding.button4.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,TrendSearchActivity.class);
            startActivity(intent);
        });

        // 5) 나머지 기존 로직(애니, RecyclerView, ViewModel, 뉴스 로딩)
        loadingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);

        adapter = new ArticleAdapter();
        binding.rvArticles.setLayoutManager(new LinearLayoutManager(this));
        binding.rvArticles.setAdapter(adapter);

        vm = new ViewModelProvider(this).get(NewsViewModel.class);
        binding.btnRefresh.setOnClickListener(v -> {
            showLoading();
            vm.loadHeadlines();
        });
        vm.getHeadlines().observe(this, this::onNewsReceived);
        vm.getError().observe(this, err -> {
            hideLoading();
            binding.textBar.setText("Error: " + err);
        });
        vm.loadHeadlines();
    }

    private void setAuthButtons(boolean loggedIn) {
        binding.btnGoLogin.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
        binding.btnLogout .setVisibility(loggedIn ? View.VISIBLE : View.GONE);
    }

    private void onNewsReceived(List<News> newsList) {
        hideLoading();
        adapter.submitList(newsList);
        binding.textBar.setText("총 " + newsList.size() + "개의 기사를 가져왔습니다.");
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

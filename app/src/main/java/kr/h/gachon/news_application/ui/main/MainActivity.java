package kr.h.gachon.news_application.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import kr.h.gachon.news_application.R;
import kr.h.gachon.news_application.databinding.ActivityMainBinding;
import kr.h.gachon.news_application.network.model.News;
import kr.h.gachon.news_application.ui.login.LoginActivity;
import kr.h.gachon.news_application.viewmodel.NewsViewModel;

public class MainActivity extends ComponentActivity {
    private ActivityMainBinding binding;
    private NewsViewModel vm;
    private ArticleAdapter adapter;

    private Animation loadingAnim;
    private TextView textBar, tvUsername;
    private Button btnGoLogin, btn1, btn2, btn3, btn4, btn5, btn_textBar;
    private Spinner spinner;

    // 1) 로그인 액티비티 결과 받기
    private final ActivityResultLauncher<Intent> loginLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            String userName = result.getData().getStringExtra("username");
                            binding.tvUsername.setText("안녕하세요, " + userName + "님");
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // **로그인 버튼 리스너**
        binding.btnGoLogin.setOnClickListener(v ->
                loginLauncher.launch(new Intent(this, LoginActivity.class))
        );

        // loading 애니메이션 로드
        loadingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);

        // 뷰 바인딩 + findViewById 혼용
        textBar    = binding.textBar;
        tvUsername = binding.tvUsername;     // 새로 추가한 아이디
        btnGoLogin = binding.btnGoLogin;     // 새로 추가한 아이디

        btn1       = findViewById(R.id.button1);
        btn2       = findViewById(R.id.button2);
        btn3       = findViewById(R.id.button3);
        btn4       = findViewById(R.id.button4);
        btn5       = findViewById(R.id.button5);
        btn_textBar= findViewById(R.id.btn_textBar);
        spinner    = findViewById(R.id.spinner);

        // RecyclerView 세팅
        adapter = new ArticleAdapter();
        binding.rvArticles.setLayoutManager(new LinearLayoutManager(this));
        binding.rvArticles.setAdapter(adapter);

        // ViewModel 초기화
        vm = new ViewModelProvider(this).get(NewsViewModel.class);

        // 새로고침 버튼 클릭
        binding.btnRefresh.setOnClickListener(v -> {
            showLoading();
            vm.loadHeadlines();
        });

        // 뉴스 데이터 수신
        vm.getHeadlines().observe(this, this::onNewsReceived);

        // 에러 처리
        vm.getError().observe(this, err -> {
            hideLoading();
            textBar.setText("Error: " + err);
        });

        // 요약 토글
        btn_textBar.setOnClickListener(v ->
                textBar.setVisibility(
                        textBar.getVisibility() == View.VISIBLE
                                ? View.GONE : View.VISIBLE
                )
        );

        // 앱 시작 시 데이터 로드
        vm.loadHeadlines();

        // 2) 로그인 버튼 클릭 → LoginActivity 실행
        btnGoLogin.setOnClickListener(v -> {
            loginLauncher.launch(new Intent(this, LoginActivity.class));
        });
    }

    private void onNewsReceived(List<News> newsList) {
        hideLoading();
        adapter.submitList(newsList);
        textBar.setText("총 " + newsList.size() + "개의 기사를 가져왔습니다.");
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
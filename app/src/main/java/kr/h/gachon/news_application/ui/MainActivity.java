package kr.h.gachon.news_application.ui;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import kr.h.gachon.news_application.R;
import kr.h.gachon.news_application.databinding.ActivityMainBinding;
import kr.h.gachon.news_application.network.model.News;
import kr.h.gachon.news_application.viewmodel.NewsViewModel;

public class MainActivity extends ComponentActivity {
    private ActivityMainBinding binding;
    private NewsViewModel vm;
    private ArticleAdapter adapter;

    private Animation loadingAnim;

    private TextView textBar;
    private Button btn1, btn2, btn3, btn4, btn5, btn_textBar;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);

        textBar = findViewById(R.id.textBar);
        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        btn4 = findViewById(R.id.button4);
        btn5 = findViewById(R.id.button5);
        btn_textBar = findViewById(R.id.btn_textBar);
        spinner = findViewById(R.id.spinner);

        // RecyclerView 세팅
        adapter = new ArticleAdapter();
        binding.rvArticles.setLayoutManager(new LinearLayoutManager(this));
        binding.rvArticles.setAdapter(adapter);

        // ViewModel 초기화
        vm = new ViewModelProvider(this).get(NewsViewModel.class);

        // 새로고침 버튼 클릭 시
        binding.btnRefresh.setOnClickListener(v -> {
            showLoading();
            vm.loadHeadlines();
        });

        // 뉴스 데이터 수신 시
        vm.getHeadlines().observe(this, this::onNewsReceived);

        // 에러 발생 시
        vm.getError().observe(this, err -> {
            hideLoading();
            binding.textBar.setText("Error: " + err);
        });

        // 앱 시작 시 로딩
        vm.loadHeadlines();

        // 요약 토글 버튼
        btn_textBar.setOnClickListener(v -> {
            if (textBar.getVisibility() == View.VISIBLE) {
                textBar.setVisibility(View.GONE);
            } else {
                textBar.setVisibility(View.VISIBLE);
            }
        });
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

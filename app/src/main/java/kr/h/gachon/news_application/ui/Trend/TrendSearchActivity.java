package kr.h.gachon.news_application.ui.Trend;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.h.gachon.news_application.R;
import kr.h.gachon.news_application.adapter.TrendSearchAdapter;
import kr.h.gachon.news_application.network.model.TrendSearchDTO;
import kr.h.gachon.news_application.viewmodel.TrendSearchViewModel;

public class TrendSearchActivity extends AppCompatActivity {

    private TrendSearchViewModel trendSearchViewModel;
    private RecyclerView recyclerView;
    private TrendSearchAdapter adapter;
    private ProgressBar progressBar;
    private EditText etStartDate, etEndDate;
    private Button btnSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend_search);

        // 뷰 초기화
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate   = findViewById(R.id.etEndDate);
        btnSearch   = findViewById(R.id.btnSearch);
        recyclerView= findViewById(R.id.trendRecyclerView);
        progressBar = findViewById(R.id.trendProgressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TrendSearchAdapter();
        recyclerView.setAdapter(adapter);

        trendSearchViewModel = new ViewModelProvider(this).get(TrendSearchViewModel.class);

        // 1) LiveData 관찰: trendingNews
        trendSearchViewModel.getTrendingNews().observe(this, new Observer<List<TrendSearchDTO>>() {
            @Override
            public void onChanged(List<TrendSearchDTO> trendList) {
                progressBar.setVisibility(View.GONE);
                if (trendList != null) {
                    adapter.setData(trendList);
                }
            }
        });

        // 2) LiveData 관찰: errorMsg
        trendSearchViewModel.getErrorMsg().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String err) {
                progressBar.setVisibility(View.GONE);
                if (err != null) {
                    Toast.makeText(TrendSearchActivity.this, err, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 3) 버튼 클릭 시 API 호출
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String start = etStartDate.getText().toString().trim();
                String end   = etEndDate.getText().toString().trim();
                int topK = 5; // 필요 시 EditText 등으로 변경 가능

                if (start.isEmpty() || end.isEmpty()) {
                    Toast.makeText(TrendSearchActivity.this, "날짜를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                trendSearchViewModel.fetchTrendSearch(start, end, topK);
            }
        });
    }
}


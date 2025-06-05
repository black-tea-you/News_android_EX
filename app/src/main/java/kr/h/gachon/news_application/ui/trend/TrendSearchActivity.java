package kr.h.gachon.news_application.ui.trend;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;                // Java 8 desugaring 필요
import java.time.format.DateTimeFormatter; // Java 8 desugaring 필요
import java.util.Calendar;
import java.util.List;

import kr.h.gachon.news_application.R;
import kr.h.gachon.news_application.adapter.TrendSearchAdapter;
import kr.h.gachon.news_application.network.model.TrendSearchDTO;
import kr.h.gachon.news_application.network.model.TrendSearchResult;
import kr.h.gachon.news_application.viewmodel.TrendSearchViewModel;

public class TrendSearchActivity extends AppCompatActivity {

    private TextView    tvStartDate;
    private TextView    tvEndDate;
    private TextView    tvKeywords;
    private Button      btnSearch;
    private ProgressBar progressBar;
    private RecyclerView rvNews;

    private TrendSearchViewModel viewModel;
    private TrendSearchAdapter   adapter;

    // 사용자가 선택한 날짜 (시간 없이)
    private LocalDate selectedStartDate = null;
    private LocalDate selectedEndDate   = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend_search);

        // 1) 뷰 바인딩
        tvStartDate  = findViewById(R.id.tvStartDate);
        tvEndDate    = findViewById(R.id.tvEndDate);
        tvKeywords   = findViewById(R.id.tvKeywords);
        btnSearch    = findViewById(R.id.btnSearch);
        progressBar  = findViewById(R.id.progressLoading);
        rvNews       = findViewById(R.id.rvNews);

        // 2) RecyclerView 초기화
        rvNews.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TrendSearchAdapter();
        rvNews.setAdapter(adapter);

        // 3) ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(TrendSearchViewModel.class);

        // 4) LiveData 관찰: 전체 JSON 래퍼(TrendSearchResult)
        viewModel.getFullResponse().observe(this, new Observer<TrendSearchResult>() {
            @Override
            public void onChanged(TrendSearchResult response) {
                progressBar.setVisibility(View.GONE);

                if (response != null) {
                    // 4-1) 키워드 한 줄로 표시
                    List<String> kwList = response.getKeywords();
                    if (kwList != null && !kwList.isEmpty()) {
                        String joined = String.join(", ", kwList);
                        tvKeywords.setText("키워드: " + joined);
                        tvKeywords.setVisibility(View.VISIBLE);
                    } else {
                        tvKeywords.setVisibility(View.GONE);
                    }

                    // 4-2) 뉴스 리스트 표시
                    List<TrendSearchDTO> newsList = response.getNews();
                    if (newsList != null && !newsList.isEmpty()) {
                        adapter.setData(newsList);
                    } else {
                        Toast.makeText(TrendSearchActivity.this, "조건에 맞는 뉴스가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // 5) LiveData 관찰: 에러 메시지
        viewModel.getErrorMsg().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String err) {
                progressBar.setVisibility(View.GONE);
                if (err != null) {
                    Toast.makeText(TrendSearchActivity.this, err, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 6) “시작 날짜” 클릭 시 DatePicker 띄우기
        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(true);
            }
        });

        // 7) “종료 날짜” 클릭 시 DatePicker 띄우기
        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(false);
            }
        });

        // 8) “검색” 버튼 클릭 리스너
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedStartDate == null || selectedEndDate == null) {
                    Toast.makeText(TrendSearchActivity.this, "시작/종료 날짜를 모두 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 날짜 비교: LocalDate의 isAfter 또는 isBefore 사용
                if (selectedEndDate.isBefore(selectedStartDate)) {
                    Toast.makeText(TrendSearchActivity.this, "종료 날짜는 시작 날짜 이후여야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // “YYYY-MM-DD” 문자열로 포맷
                String startDateStr = selectedStartDate.toString();
                String endDateStr   = selectedEndDate.toString();

                progressBar.setVisibility(View.VISIBLE);
                tvKeywords.setVisibility(View.GONE);
                adapter.setData(null);
                viewModel.fetchTrendSearch(startDateStr, endDateStr, 10);
            }
        });
    }

    /**
     * DatePickerDialog만 띄워서 사용자가 연-월-일을 선택하도록 함.
     * 선택된 날짜는 LocalDate로 변환하여 TextView에 표시하고,
     * selectedStartDate 또는 selectedEndDate에 저장.
     *
     * @param isStart true → 시작 날짜 선택, false → 종료 날짜 선택
     */
    private void showDatePicker(final boolean isStart) {
        final Calendar now = Calendar.getInstance();
        int year  = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day   = now.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                TrendSearchActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // month는 0~11이므로 +1
                        LocalDate picked = LocalDate.of(year, month + 1, dayOfMonth);

                        // TextView에 "YYYY-MM-DD" 형태로 표시
                        String formatted = picked.toString();

                        if (isStart) {
                            selectedStartDate = picked;
                            tvStartDate.setText(formatted);
                        } else {
                            selectedEndDate = picked;
                            tvEndDate.setText(formatted);
                        }
                    }
                },
                year, month, day
        );
        datePicker.show();
    }
}

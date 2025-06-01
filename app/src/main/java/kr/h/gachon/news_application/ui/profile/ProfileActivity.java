package kr.h.gachon.news_application.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import kr.h.gachon.news_application.databinding.ActivityProfileBinding;
import kr.h.gachon.news_application.viewmodel.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private ProfileViewModel vm;
    private KeywordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this).get(ProfileViewModel.class);

        adapter = new KeywordAdapter();
        binding.rvKeywords.setLayoutManager(new LinearLayoutManager(this));
        binding.rvKeywords.setAdapter(adapter);

        vm.getKeywords().observe(this, list -> {
            if (list != null) {
                Log.d("ProfileActivity", "서버에서 받은 키워드 [" + list.size() + "개]: " + list);
            }
            adapter.submitList(list);
        });

        vm.getResultMsg().observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        binding.btnAddKeyword.setOnClickListener(v -> {
            String kw = binding.etKeyword.getText().toString().trim();
            vm.addKeyword(kw);
        });

        binding.btnDeleteKeyword.setOnClickListener(v -> {
            String kw = binding.etKeyword.getText().toString().trim();
            vm.deleteKeyword(kw);
        });

        // 첫 로드
        vm.loadKeywords();
    }
}

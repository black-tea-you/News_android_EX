package kr.h.gachon.news_application.ui.login;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import kr.h.gachon.news_application.databinding.RegisterBinding;
import kr.h.gachon.news_application.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {
    private RegisterBinding binding;
    private AuthViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = RegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this).get(AuthViewModel.class);

        // 1) 버튼 클릭 시 register() 호출
        binding.btnRegister.setOnClickListener(v -> {
            String user  = binding.etUser.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String pass  = binding.etPass.getText().toString().trim();
            vm.register(user, pass, email);
        });

        // 2) 회원가입 성공 관찰
        vm.getRegisterSuccess().observe(this, msg -> {
            Toast.makeText(this, msg != null ? msg : "회원가입 성공", Toast.LENGTH_SHORT).show();
            finish(); // 가입 후 뒤로 (로그인 화면) 복귀
        });

        // 3) 회원가입 실패 관찰
        vm.getRegisterError().observe(this, err -> {
            Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
        });
    }
}
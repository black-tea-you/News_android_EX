package kr.h.gachon.news_application.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import kr.h.gachon.news_application.databinding.ActivityLoginBinding;
import kr.h.gachon.news_application.ui.main.MainActivity;
import kr.h.gachon.news_application.util.TokenManager;
import kr.h.gachon.news_application.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private AuthViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.btnLogin.setOnClickListener(v -> {
            Log.d("LoginActivity", "로그인 버튼 클릭, user="
                    + binding.etUser.getText().toString());
            vm.login(
                    binding.etUser.getText().toString(),
                    binding.etPass.getText().toString()
            );
        });

        vm.getLoginSuccess().observe(this, resp -> {
            //토큰 저장
            TokenManager.getInstance(this).saveToken(resp.getAccessToken());
            Intent result = new Intent();
            result.putExtra("username", binding.etUser.getText().toString().trim());
            setResult(RESULT_OK, result);
            
            //메인으로 이동
            finish();
        });

        vm.getLoginError().observe(this, errMsg ->
                Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
        );

        //회원가입 버튼
        binding.btnGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}

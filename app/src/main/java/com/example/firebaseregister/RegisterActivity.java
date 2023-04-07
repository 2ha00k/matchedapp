package com.example.firebaseregister;

import java.util.regex.Pattern;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.text.TextUtils;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;   //파이어베이스 인증
    private DatabaseReference mDatabaseRef;  //실시간 데이터 베이스
    private EditText mEtEmail, mEtPwd, mEtName, mEtDepartment, mEtGrade, mEtBirth;
    private Button mBtnRegister;
    private boolean isValidPassword(String password) {
        // 비밀번호 유효성 검사 로직 구현
        if (password.length() < 6) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasNumber = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            }
        }
        return hasLetter && hasNumber;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Spinner에 사용될 항목들을 정의한 배열 가져오기
        String[] departments = getResources().getStringArray(R.array.grades);
        // Spinner 어댑터 생성
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Spinner와 어댑터 연결
        Spinner spinner = findViewById(R.id.spinner_grade);
        spinner.setAdapter(adapter);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mEtEmail = findViewById(R.id.et_id);
        mEtEmail.setText("@mju.ac.kr");
        mEtPwd = findViewById(R.id.et_pass);
        mEtName = findViewById(R.id.et_name);
        mEtDepartment = findViewById(R.id.et_department);
        Spinner mEtGrade = findViewById(R.id.spinner_grade);
        mEtBirth = findViewById(R.id.et_birth);
        mBtnRegister = findViewById(R.id.btn_regi);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원가입 처리 시작 (버튼 누를 때)
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();
                String strName = mEtName.getText().toString();
                String strDepartment = mEtDepartment.getText().toString();
                String strGrade = mEtGrade.getSelectedItem().toString();
                String strBirth = mEtBirth.getText().toString();

                // XML 레이아웃에서 EditText 뷰를 가져옵니다
                EditText et_id = (EditText) findViewById(R.id.et_id);

                // EditText 뷰에 대한 이벤트 리스너를 등록합니다
                et_id.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        // EditText 뷰에 포커스가 있을 때
                        if (hasFocus) {
                            // "@mju.ac.kr" 문자열을 EditText 뷰에 설정합니다
                            et_id.setText("@mju.ac.kr");
                        }
                        // EditText 뷰에 포커스가 없을 때
                        else {
                            // EditText 뷰가 비어있을 경우에만 힌트를 설정합니다
                            if (et_id.getText().toString().equals("")) {
                                et_id.setHint("학교 이메일 아이디를 입력하세요");
                            }
                        }
                    }
                });



                // 이메일 아이디 입력 여부 확인
                if (TextUtils.isEmpty(strEmail)) {
                    Toast.makeText(RegisterActivity.this, "이메일 아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 비밀번호 유효성 검사
                if (!isValidPassword(strPwd)) {
                    Toast.makeText(RegisterActivity.this, "비밀번호는 영문, 숫자를 포함한 6자리 이상으로 설정해주세요. ", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firebase Auth 진행
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "이메일 인증을 위한 링크가 이메일로 전송되었습니다.", Toast.LENGTH_SHORT).show();
                                        UserAccount account = new UserAccount();
                                        account.setIdToken(firebaseUser.getUid());
                                        account.setEmailId(firebaseUser.getEmail());
                                        account.setPassword(strPwd);
                                        account.setName(strName);
                                        account.setDepartment(strDepartment);
                                        account.setGrade(strGrade);
                                        account.setBirth(strBirth);

                                        mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
                                        Toast.makeText(RegisterActivity.this, "이메일 인증 후 회원가입이 완료됩니다. 받은 메일함을 확인해주세요.", Toast.LENGTH_SHORT).show();
                                        // 회원가입 성공 시 로그인 화면으로 이동
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 이전 모든 액티비티 삭제
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "이메일 인증 링크 전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            String errorMsg = task.getException().getMessage(); // 에러 메시지를 가져옵니다.
                            Toast.makeText(RegisterActivity.this, "회원가입에 실패했습니다. 오류: " + errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        }
    }

package com.example.knu_matching.membermanage;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knu_matching.MainActivity;
import com.example.knu_matching.R;
import com.example.knu_matching.UserAccount;
import com.example.knu_matching.chatting.ChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseref;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText edt_StudentID, edt_Major, edt_Email, edt_password, edt_repassword, edt_Nickname, edt_PhoneNumber, edt_StudentName;
    private ImageButton btn_finish, btn_check_nick, btn_knuID;
    private String strEmail, strPassword, strNick, strMaojr, strStudentId, strPhoneNumber, strStudentName;
    private TextView tv_guide;
    private boolean nickname_state;
    private int rate = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseref = FirebaseDatabase.getInstance().getReference("Knu_Matching");
        DatabaseReference mProfieDatabaseReference = mDatabaseref.child("UserAccount");
        edt_StudentName = findViewById(R.id.edt_StudentName);
        edt_StudentID = findViewById(R.id.edt_StudentID);
        edt_Major = findViewById(R.id.edt_Major);
        edt_PhoneNumber = findViewById(R.id.edt_PhoneNumber);
        edt_Email = findViewById(R.id.edt_Email);
        tv_guide = findViewById(R.id.tv_guide);
        edt_password = findViewById(R.id.edt_Password);
        edt_repassword = findViewById(R.id.edt_RePassword);
        edt_Nickname = findViewById(R.id.edt_nickname);
        btn_knuID = findViewById(R.id.btn_knuID);
        btn_check_nick = findViewById(R.id.btn_check_nick);
        btn_finish = findViewById(R.id.btn_registerButton);

        edt_StudentName.setVisibility(View.GONE);
        edt_StudentID.setVisibility(View.GONE);
        edt_Major.setVisibility(View.GONE);
        edt_PhoneNumber.setVisibility(View.GONE);
        edt_Email.setVisibility(View.GONE);
        tv_guide.setVisibility(View.GONE);

        btn_check_nick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strNick = edt_Nickname.getText().toString();
                System.out.println("test2222   " + strNick + " " + strEmail + " " + strStudentId);

                db.collection("Account")
                        .whereEqualTo("nickName", strNick)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                System.out.println("회원가입 디버깅"+QuerySnapshot document:task.getResult());
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        UserAccount userAccount = document.toObject(UserAccount.class);
                                        Log.d("db디버깅", userAccount.getNickName());
                                        if (strNick.equals(userAccount.getNickName())) {
                                            Toast.makeText(RegisterActivity.this, "중복됨", Toast.LENGTH_SHORT).show();
                                            nickname_state = false;
                                            return;
                                        }
                                    }
                                    nickname_state = true;
                                    Toast.makeText(RegisterActivity.this, "중복안됨", Toast.LENGTH_SHORT).show();
                                } else {

                                }
                            }
                        });
            }
        });


        btn_knuID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveSubActivity();
            }
        });

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strStudentId = edt_StudentID.getText().toString();
                strEmail = edt_Email.getText().toString();
                strPassword = edt_password.getText().toString();
                strNick = edt_Nickname.getText().toString();

//                System.out.println("test" + strNick + " " + strEmail + " " + strStudentId);

                if (strStudentName.trim().isEmpty() || strEmail.trim().isEmpty() || strPassword.trim().isEmpty() || strNick.trim().isEmpty() || strStudentId.trim().isEmpty() || strMaojr.trim().isEmpty() || strPhoneNumber.trim().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "빈칸을 채워주세요:(", Toast.LENGTH_SHORT).show();
                } else {
                    if (nickname_state == false) {
                        Toast.makeText(RegisterActivity.this, "닉네임 중복여부를 확인해주세요:(", Toast.LENGTH_SHORT).show();
                    }
                    if (nickname_state == true) {


                        mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPassword)
                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                    UserAccount account = new UserAccount();
                                    account.setUid(firebaseUser.getUid());
                                    account.setEmailId(firebaseUser.getEmail());
                                    account.setPhoneNumber(strPhoneNumber);
                                    account.setStudentName(strStudentName);
                                    account.setStudentId(strStudentId);
                                    account.setMajor(strMaojr);
                                    account.setNickName(strNick);
                                    account.setPassword(strPassword);
                                    account.setRate(rate);
                                    UserAccount users = new UserAccount();
                                    users.setUid(account.uid);
                                    users.setNickName(account.getNickName());

                                    FirebaseDatabase.getInstance().getReference().child("users").child(account.uid)
                                            .setValue(users)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            });


                                    db.collection("Account")
                                            .document(firebaseUser.getEmail().replace(".", ">"))
                                            .set(account)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d(TAG, "DocumentSnapshot added with ID: " + task.isSuccessful());
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "DocumentSnapshot added with ID: " + task.isSuccessful());
                                                }
                                            });
                                    Toast.makeText(RegisterActivity.this, "성공", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(RegisterActivity.this, "실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void moveSubActivity() {
        Intent intent = new Intent(RegisterActivity.this, Student_Certificate.class);
        startActivityResult.launch(intent);
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        edt_StudentName.setVisibility(View.VISIBLE);
                        edt_StudentID.setVisibility(View.VISIBLE);
                        edt_Major.setVisibility(View.VISIBLE);
                        edt_PhoneNumber.setVisibility(View.VISIBLE);
                        edt_Email.setVisibility(View.VISIBLE);
                        tv_guide.setVisibility(View.VISIBLE);
                        Log.d(TAG, "RegisterActivity로 돌아왔다. ");
                        strStudentId = result.getData().getStringExtra("StudentId");
//                        System.out.println("회원가입 디버깅:" + strStudentId);
                        strMaojr = result.getData().getStringExtra("Major");
//                        System.out.println("회원가입 디버깅:" + strMaojr);
                        strEmail = result.getData().getStringExtra("Email");
                        strStudentName = result.getData().getStringExtra("StudentName");
                        strPhoneNumber = result.getData().getStringExtra("PhoneNumber");
                        edt_StudentName.setText(strStudentName);
                        edt_StudentID.setText(strStudentId);
                        edt_Major.setText(strMaojr);
                        edt_Email.setText(strEmail);
                        edt_PhoneNumber.setText(strPhoneNumber);
                    }
                }
            });
}
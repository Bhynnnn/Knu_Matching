package com.example.knu_matching.Post;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.knu_matching.GetSet.Post;
import com.example.knu_matching.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PostFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Spinner spinner_field;
    private String serch;
    private String mParam1;
    private String mParam2;
    private Button btn_Recent;
    private Button btn_next;
    private Button btn_back, btn_test;
    private FirebaseUser user;



    public PostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment4.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_post_fragment, container, false);
        ImageButton btn_Recent = v.findViewById(R.id.btn_Recent);
        Spinner spinner_field=v.findViewById(R.id.spinner_field);




        user = FirebaseAuth.getInstance().getCurrentUser();
        getData();
        SwipeRefreshLayout swipeRefreshLayout = v.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                getData();
                ft.detach(PostFragment.this).attach(PostFragment.this).commit();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        spinner_field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                String total="전체";
                String foreign="대외활동";
                String competition="공모전";
                String knuactivity="비교과";
                String study="스터디";

                if(item.equals(foreign)){
                    serch="대외활동";
                    getData();
                }
                else if(item.equals(competition)){
                    serch="공모전";
                    getData();
                }
                else if(item.equals(knuactivity)){
                    serch="비교과";
                    getData();
                }
                else if(item.equals(study)){
                    serch="스터디";
                    getData();
                }
                else if(item.equals(total)){
                    serch="전체";
                    getData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        btn_Recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveSubActivity();
            }
        });


        // Inflate the layout for this fragment
        return v;
    }

    private void myStartActivity(Class c){
        Intent intent =new Intent(getActivity(), c);
        startActivity(intent);
    }

    private void moveSubActivity() {
        Intent intent = new Intent(getContext(), postActivity.class);
        startActivityResult.launch(intent);
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(getContext(),"돌아옴",Toast.LENGTH_SHORT).show();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        getData();
                        ft.detach(PostFragment.this).attach(PostFragment.this).commit();
                        Log.d(TAG, "RegisterActivity로 돌아왔다. ");
                    }
                    if(result.getResultCode()==2){
                        Toast.makeText(getContext(),"삭제후 돌아옴",Toast.LENGTH_SHORT).show();
                    }
                }
            });
    public void getData(){
        db.collection("Post")
                .orderBy("str_time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Post> postList = new ArrayList<>();
                            String str;
                            String result="전체";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + "==>" + document.getData());
                                Post post = document.toObject(Post.class);
                                if(serch.equals(result)){
                                    post.getStr_Title();
                                    post.getStr_Title();
                                    post.getStr_Number();
                                    post.getUri();
                                    post.getStr_post();
                                    post.getStr_email();
                                    post.getStr_filename();
                                    post.getStr_EndDate();
                                    post.getStr_StartDate();
                                    post.getStr_Id();
                                    post.getStr_Nickname();
                                    post.getStr_time();
                                    post.getStr_link();
                                    post.getStr_uid();
                                    post.getStr_field();
                                    postList.add(post);
                                }
                                else if(serch.equals(post.getStr_field())){
                                    post.getStr_Title();
                                    post.getStr_Title();
                                    post.getStr_Number();
                                    post.getUri();
                                    post.getStr_post();
                                    post.getStr_email();
                                    post.getStr_filename();
                                    post.getStr_EndDate();
                                    post.getStr_StartDate();
                                    post.getStr_Id();
                                    post.getStr_Nickname();
                                    post.getStr_time();
                                    post.getStr_link();
                                    post.getStr_uid();
                                    post.getStr_field();
                                    postList.add(post);
                                }

//                                postList.add(new postInfo(
//                                        document.getData().get("str_Title").toString(),
//                                        document.getData().get("str_StartDate").toString(),
//                                        document.getData().get("str_EndDate").toString(),
//                                        document.getData().get("str_Number").toString(),
//                                        document.getData().get("str_post").toString(),
//                                        document.getData().get("str_time").toString(),
//                                        document.getData().get("str_Nickname").toString(),
//                                        document.getData().get("str_email").toString(),
//                                        document.getId(),
//                                        document.getData().get("str_filename").toString(),
//                                        (Uri) document.getData().get("uri")
//                                ));
                            }
                            RecyclerView recyclerView = getView().findViewById(R.id.recycleView);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            RecyclerView.Adapter mAdapter = new PostAdapter(getActivity(), postList);
                            mAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(mAdapter);

                        } else {
                            Log.d(TAG, "error", task.getException());
                        }
                    }
                });
    }



}
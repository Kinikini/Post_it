package com.example.postit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.DocumentType;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView user_list_view;
    private List<User> user_list;
    private List<String> id_list;

    private FirebaseFirestore firebaseFirestore;
    private UserRecyclerAdapter userRecyclerAdapter;



    public UserFragment() {
        // Required empty public constructor
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
        super.onCreateView(inflater,container,savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_user, container, false);// Inflate the layout for this fragment


        user_list = new ArrayList<>();
        id_list = new ArrayList<>();
        user_list_view = view.findViewById(R.id.user_list_view);
        userRecyclerAdapter = new UserRecyclerAdapter(user_list, id_list);
        user_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        user_list_view.setAdapter(userRecyclerAdapter);



        firebaseFirestore = FirebaseFirestore.getInstance();

        Query firstQuery = firebaseFirestore.collection("Users")
                .orderBy("name",Query.Direction.ASCENDING);

        firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges())
                {
                    if(doc.getType() == DocumentChange.Type.ADDED)
                    {
                        String userId = doc.getDocument().getId();
                        User user = doc.getDocument().toObject(User.class);
                        if(!userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            user_list.add(user);
                            id_list.add(userId);
                            userRecyclerAdapter.notifyDataSetChanged();
                        }


                    }
                }
            }
        });

        return view;
    }



}
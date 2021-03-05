package com.example.postit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.postit.Notification;
import com.example.postit.NotificationsRecyclerAdapter;
import com.example.postit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminNotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminNotificationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private RecyclerView notification_list_view;
    private List<Notification> notification_list;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private NotificationsRecyclerAdapter notificationsRecyclerAdapter;

    public AdminNotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminNotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminNotificationFragment newInstance(String param1, String param2) {
        AdminNotificationFragment fragment = new AdminNotificationFragment();
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
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_admin_notification, container, false);// Inflate the layout for this fragment


        notification_list = new ArrayList<>();
        notification_list_view = view.findViewById(R.id.notification_list_view);
        notificationsRecyclerAdapter = new NotificationsRecyclerAdapter(notification_list);
        notification_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        notification_list_view.setAdapter(notificationsRecyclerAdapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        Query firstQuery = firebaseFirestore.collection("AdminNotifs")
                .orderBy("timestamp",Query.Direction.DESCENDING);

        firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges())
                {
                    if(doc.getType() == DocumentChange.Type.ADDED)
                    {
                        String notificationId = doc.getDocument().getId();
                        Notification notification = doc.getDocument().toObject(Notification.class);
                        notification_list.add(notification);
                        notificationsRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });


        return view;
    }
}
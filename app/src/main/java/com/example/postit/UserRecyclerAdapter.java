package com.example.postit;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder> {

    public List<User> user_list;
    public List<String> id_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;




    public UserRecyclerAdapter(List<User> user_list, List<String> id_list){
        this.user_list = user_list;
        this.id_list = id_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item,parent,false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        String name = user_list.get(position).getName();
        String image = user_list.get(position).getImage();

        holder.setAuthorProfilView(image);
        holder.setUsername(name);

        holder.ban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Users").document(id_list.get(position)).delete();

                FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();

                fragmentTransaction.replace(R.id.admin_container,new UserFragment());
                fragmentTransaction.commit();
            }
        });


    }

    @Override
    public int getItemCount() {
        return user_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView userProfil;

        private TextView username;

        private Button ban;

        private View mView;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);



            mView = itemView;

            ban = mView.findViewById(R.id.ban);


        }

        public void setUsername(String s)
        {
            username = mView.findViewById(R.id.Username_user);
            username.setText(s);
        }



        public void setAuthorProfilView(String image)
        {
            userProfil = mView.findViewById(R.id.userProfile);
            RequestOptions placeholderOptions = new RequestOptions();
            placeholderOptions.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeholderOptions).load(image).into(userProfil);
        }


    }


}

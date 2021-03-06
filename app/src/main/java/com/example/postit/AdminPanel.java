package com.example.postit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class AdminPanel extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FloatingActionButton addPostBtn;
    private FirebaseFirestore firebaseFirestore;
    private String current_user_id;
    private BottomNavigationView mainBottomNav;

    private AdminHomeFragment adminHomeFragment;
    private AdminNotificationFragment notificationFragment;
    private AccountFragment accountFragment;

    private Toolbar toolbar;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private ImageView ProfilView;
    private TextView UserNameView;

    public boolean is_admin;

    private UserFragment userFragment;




    @Nullable
    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return super.onWindowStartingActionMode(callback);
    }

    @Override
    public void onRestart()
    {
        super.onRestart();

        finish();

        startActivity(getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);




        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.enableNetwork();
        if(mAuth.getCurrentUser() != null){
            //mainBottomNav = (BottomNavigationView) findViewById(R.id.mainBottomNav);





            //FRAGMENTS
            adminHomeFragment = new AdminHomeFragment();
            notificationFragment = new AdminNotificationFragment();
            accountFragment = new AccountFragment();
            userFragment = new UserFragment();


            replaceFragment(adminHomeFragment);


            toolbar = findViewById(R.id.main_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Post it");

            drawerLayout = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);

            /*if(is_admin)
            {
                Toast.makeText(AdminPanel.this,"is admin",Toast.LENGTH_LONG).show();
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.admin_panel);
            }*/

            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                    this,
                    drawerLayout,
                    toolbar,
                    R.string.openNavDrawer,
                    R.string.closeNavDrawer
            );

            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();

            navigationView.bringToFront();


            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {

                    switch (item.getItemId())
                    {
                        case R.id.see_as_user:
                            Intent settingsIntent = new Intent(AdminPanel.this, MainActivity.class);
                            startActivity(settingsIntent);

                            return true;
                        case R.id.action_admin_unpublished:
                            replaceFragment(adminHomeFragment);
                            return true;

                        case R.id.action_admin_notification:
                            replaceFragment(notificationFragment);
                            return true;

                        case R.id.action_create_admin:

                            Intent createAdminIntent = new Intent(AdminPanel.this, CreateAdminActivity.class);
                            startActivity(createAdminIntent);

                            return true;
                        case R.id.action_ban_user:

                            replaceFragment(userFragment);

                            return true;

                        case R.id.action_logout_button:

                            logOut();
                            return true;


                        case R.id.action_setting_button:

                            Intent adminIntent = new Intent(AdminPanel.this, SetupActivity.class);
                            startActivity(adminIntent);

                            return true;
                        default:
                            return false;

                    }



                }


            });


            String user_id = mAuth.getUid();
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String userName = task.getResult().getString("name");
                        String userImage = task.getResult().getString("image");

                        setUserNameView(userName);
                        setAuthorProfilView(userImage);


                    }
                }
            });
        }




        addPostBtn = findViewById(R.id.add_post_btn);

        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newPostIntent = new Intent(AdminPanel.this, NewPostActivity.class);
                startActivity(newPostIntent);
                finish();


            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            sendToLoginPage();

        } else {

            current_user_id = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    //replaceFragment(adminHomeFragment);
                    if (task.isSuccessful()){

                        if (!task.getResult().exists()){

                            Intent setupIntent = new Intent(AdminPanel.this, SetupActivity.class);
                            startActivity(setupIntent);

                            finish();

                        }

                    } else {

                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(AdminPanel.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();


                    }

                }
            });

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_logout_button:

                logOut();
                return true;


            case R.id.action_setting_button:

                Intent settingsIntent = new Intent(AdminPanel.this, SetupActivity.class);
                startActivity(settingsIntent);

                return true;

            default:
                return false;
        }

    }

    private void logOut() {
        firebaseFirestore.getInstance().disableNetwork();
        mAuth.signOut();

        sendToLoginPage();

    }


    private void sendToLoginPage() {

        Intent loginIntent = new Intent(AdminPanel.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.admin_container,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }



    public void setUserNameView(String name)
    {
        UserNameView = findViewById(R.id.username);
        UserNameView.setText(name);
    }

    public void setAuthorProfilView(String image)
    {
        ProfilView = findViewById(R.id.user_profil);
        RequestOptions placeholderOptions = new RequestOptions();
        placeholderOptions.placeholder(R.drawable.user_image);
        Glide.with(AdminPanel.this).applyDefaultRequestOptions(placeholderOptions).load(image).into(ProfilView);
    }



}
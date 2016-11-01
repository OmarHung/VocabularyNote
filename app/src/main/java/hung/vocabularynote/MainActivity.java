package hung.vocabularynote;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
    private ArrayList<Integer> mDataType = new ArrayList<Integer>();
    private SQLiteHelper sqlite;
    private ImageView imgProfile,imgLogout;
    private TextView txtName,txtEmail,txtLogin;
    private LinearLayout linearLayout_Login,linearLayout_NoLogin;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 0;
    //private static final int PROFILE_PIC_SIZE = 400;
    //private String personName = "";
    //private String personPhotoUrl  = "";
    //private String email  = "";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();

        // Configure sign-in to request the user's ID, email address, and basic profile. ID and
        // basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initWidget() {
        sqlite=new SQLiteHelper(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
        imgLogout = (ImageView) header.findViewById(R.id.imageView_logout);
        imgProfile = (ImageView) header.findViewById(R.id.imageView_profile);
        //imgProfile.setOnClickListener(this);
        txtEmail = (TextView) header.findViewById(R.id.textView_email);
        txtName = (TextView) header.findViewById(R.id.textView_name);
        txtLogin = (TextView) header.findViewById(R.id.textView_Login);
        linearLayout_Login = (LinearLayout) header.findViewById(R.id.linear_Login);
        linearLayout_NoLogin = (LinearLayout) header.findViewById(R.id.linear_NoLogin);

        imgLogout.setOnClickListener(this);
        linearLayout_NoLogin.setOnClickListener(this);
        //linearLayout_Login.setVisibility(View.INVISIBLE);

        setRecyclerView();
    }

    private void setRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_Vocabulary);
        recyclerViewAdapter = new RecyclerViewAdapter(this,mData,mDataType);//, mDataTypes);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
        readSQLite();
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(final View view , final String data){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("選擇動作").setNegativeButton("編輯", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] _id = {view.getTag().toString()};
                        showDialog("編輯",sqlite.getSingleData(_id));
                    }
                }).setPositiveButton("刪除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("delete",data);
                        sqlite.delete(Long.valueOf(data));
                        readSQLite();
                    }
                });
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                //Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readSQLite() {
        mData.clear();
        mData.addAll(sqlite.getData());
        mDataType.clear();
        for(int i=0;i<mData.size();i++) {
            if(i==mData.size()-1) mDataType.add(1);
            else mDataType.add(0);
        }
        recyclerViewAdapter.notifyDataSetChanged();
        //specialUpdate();
    }
    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d("MainActivity", "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    private void showDialog(final String title, final List<Map<String,Object>> item) {
        LayoutInflater layoutInflater = MainActivity.this.getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title).setView(layoutInflater.inflate(R.layout.vocabulary_dialog_layout, null));
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final EditText inputEnglish = (EditText) alertDialog.findViewById(R.id.input_english);
        final EditText inputChinses = (EditText) alertDialog.findViewById(R.id.input_chinese);
        final EditText inputExample = (EditText) alertDialog.findViewById(R.id.input_example);
        if(title.equals("編輯")) {
            inputEnglish.setText(item.get(0).get("English").toString());
            inputChinses.setText(item.get(0).get("Chinese").toString());
            inputExample.setText(item.get(0).get("Example").toString());
        }
        Button btnSave = (Button) alertDialog.findViewById(R.id.button_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEnglish,strChinese,strExample;
                strEnglish = inputEnglish.getText().toString();
                strChinese = inputChinses.getText().toString();
                strExample = inputExample.getText().toString();
                if(title.equals("編輯")) {
                    Log.d("editer", item.get(0).get("English").toString());
                    sqlite.update(Long.valueOf(item.get(0).get("_id").toString()),strEnglish,strChinese,strExample,item.get(0).get("Star").toString());
                }else {
                    sqlite.insert(strEnglish, strChinese, strExample, "0");
                }
                readSQLite();
                alertDialog.cancel();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("確定要登出嗎？").setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signOut();
                        Toast.makeText(MainActivity.this,"登出",Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("取消", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.linear_NoLogin:
                signIn();
                Toast.makeText(MainActivity.this,"登入",Toast.LENGTH_SHORT).show();
                break;
            case R.id.fab:
                showDialog("新增單字",null);
                break;
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("result", connectionResult.toString());
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("MainActivity", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String personName = acct.getDisplayName();
            String email = acct.getEmail();
            txtName.setText(personName);
            txtEmail.setText(email);
            if(acct.getPhotoUrl()!=null) {
                String personPhotoUrl = acct.getPhotoUrl().toString();
                //imgProfile.setImageURI(acct.getPhotoUrl());
                Glide.with(getApplicationContext()).load(personPhotoUrl)
                        .override(144,144)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgProfile);
            }else {
                imgProfile.setImageResource(R.mipmap.ic_launcher);
            }
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }
    private void updateUI(boolean signedIn) {
        if (signedIn) {
            linearLayout_NoLogin.setVisibility(View.GONE);
            linearLayout_Login.setVisibility(View.VISIBLE);
        } else {
            linearLayout_NoLogin.setVisibility(View.VISIBLE);
            linearLayout_Login.setVisibility(View.GONE);
        }
    }
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    private ProgressDialog mProgressDialog;
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("載入中...請稍後...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from
        //   GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
}

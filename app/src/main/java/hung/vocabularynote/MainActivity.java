package hung.vocabularynote;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//GoogleApiClient.OnConnectionFailedListener,
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
    private ArrayList<Integer> mDataType = new ArrayList<Integer>();
    private SQLiteHelper sqlite;
    //private ImageView imgProfile,imgLogout;
    //private TextView txtName,txtEmail,txtLogin;
    //private LinearLayout linearLayout_Login,linearLayout_NoLogin;
    private int lastSelectedItem=0;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private static final int REQUEST = 0;

    //private GoogleApiClient mGoogleApiClient;
    //private static final int RC_SIGN_IN = 0;
    //private static final int REQUEST_CODE_RESOLUTION = 3;
    //private GoogleApiClient mGoogleDiveApiClient;
    //private GoogleApiClient mGoogleApiClient;
    private boolean checkPermission() {
        if(Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                return true;
            else
                return false;
        }else return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkPermission()) {
            setContentView(R.layout.activity_main);
            initWidget();
        }else {
            setContentView(R.layout.activity_main_nopermission);
            initNoPermissionWidget();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("permissions","permissions = "+permissions.length);
        switch (requestCode) {
            case REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 取得權限
                    Log.d("MainActivity", "onRequestPermissionsResult");
                    setContentView(R.layout.activity_main);
                    initWidget();
                } else {
                    // 未取得權限
                }
                break;
        }
    }
    public static final String FILE_DIR = "/hung.vocabularynote.database";
    public static final String DATABASE_NAME = "vocabulary.db";
    public void copyDBtoSDCard() {
        try {
            String tDBFolderPath = Environment.getExternalStorageDirectory() + FILE_DIR;
            File dirDBFolder = new File(tDBFolderPath);
            String tDBFilePath = Environment.getExternalStorageDirectory() + FILE_DIR + File.separator + DATABASE_NAME;
            File dirDBFile = new File(tDBFilePath);
            Log.d("SQLite",tDBFilePath);
            //先檢查該目錄是否存在
            if (!dirDBFolder.exists()){
                //若不存在則建立它
                dirDBFolder.mkdir();
            }else {
                if(!dirDBFile.exists()) {
                    InputStream inputStream = getResources().openRawResource(R.raw.vocabulary);
                    FileOutputStream fileOutputStream = new FileOutputStream(tDBFilePath);
                    byte[] tBuffer = new byte[5120];
                    int tCount = 0;
                    while ((tCount = inputStream.read(tBuffer)) > 0) {
                        fileOutputStream.write(tBuffer, 0, tCount);
                    }
                    fileOutputStream.close();
                    inputStream.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        try {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }catch (Exception e) {
            Log.d("MainActivity", e.getMessage());
            finish();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
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

        if (id == R.id.nav_all) {
            Log.d("MainActivity", "全部單字");
            if(lastSelectedItem!=R.id.nav_all) {
                toolbar.setTitle("全部單字");
                readSQLite();
            }
            lastSelectedItem = R.id.nav_all;
        } else if (id == R.id.nav_mark) {
            Log.d("MainActivity", "不熟單字");
            if(lastSelectedItem!=R.id.nav_mark) {
                toolbar.setTitle("不熟單字");
                readMarkSQLite();
            }
            lastSelectedItem = R.id.nav_mark;
        } else if (id == R.id.nav_share) {
            shareTo("Android app 單字筆記本", "推薦你好用又方便的 Android app「單字筆記本」 https://play.google.com/store/apps/details?id=hung.vocabularynote  趕快去下載吧！", "選擇分享對象");
            //lastSelectedItem = R.id.nav_share;
        } else if (id == R.id.nav_score) {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this,"無法啟動Google Play ! 請確認網路連線後再試",Toast.LENGTH_SHORT).show();
            }
            //lastSelectedItem = R.id.nav_score;
        } else if (id == R.id.nav_info) {
            Intent intent = new Intent(this, InformationActivity.class);
            startActivity(intent);
            //lastSelectedItem = R.id.nav_info;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void initWidget() {
        copyDBtoSDCard();
        sqlite=new SQLiteHelper(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //lastSelectedItem = R.id.nav_all;
        //navigationView.getMenu().getItem(0).setChecked(true);
        //lastSelectedItem = R.id.nav_all;
        //toolbar.setTitle("全部單字");
        View header = navigationView.inflateHeaderView(R.layout.nav_header_description);
        /*
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
        */
        setRecyclerView();
        /*
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestEmail()
                .build();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this , this )
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }*/
    }
    private void initNoPermissionWidget() {
        Button button = (Button) findViewById(R.id.btn_getpermission);
        button.setOnClickListener(this);
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
                        if(lastSelectedItem==R.id.nav_all || lastSelectedItem==0)
                            readSQLite();
                        else if(lastSelectedItem==R.id.nav_mark)
                            readMarkSQLite();
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
    private void readMarkSQLite() {
        mData.clear();
        mData.addAll(sqlite.getMarkData());
        mDataType.clear();
        for(int i=0;i<mData.size();i++) {
            if(i==mData.size()-1) mDataType.add(1);
            else mDataType.add(0);
        }
        recyclerViewAdapter.notifyDataSetChanged();
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume");
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.d("MainActivity", "onStart");
        /*
        if(mGoogleApiClient!=null) {
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);// mGoogleApiClient);
            if (opr.isDone()) {
                Log.d("MainActivity", "Got cached sign-in");
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            } else {
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
            */
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
                    if(lastSelectedItem==R.id.nav_mark)
                        sqlite.insert(strEnglish, strChinese, strExample, "1");
                    else
                        sqlite.insert(strEnglish, strChinese, strExample, "0");
                }
                if(lastSelectedItem==R.id.nav_all || lastSelectedItem==0)
                    readSQLite();
                else if(lastSelectedItem==R.id.nav_mark)
                    readMarkSQLite();
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
                        //signOut();
                        //Toast.makeText(MainActivity.this,"登出",Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("取消", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.linear_NoLogin:
                //signIn();
                //Toast.makeText(MainActivity.this,"登入",Toast.LENGTH_SHORT).show();
                break;
            case R.id.fab:
                showDialog("新增單字",null);
                break;
            case R.id.btn_getpermission:
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST);
                break;
        }
    }
    /*
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"登入失敗   請確認網路連線良好",Toast.LENGTH_SHORT).show();
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("MainActivity", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
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
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);// mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(// ;mGoogleApiClient).setResultCallback(
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
    */
    private void shareTo(String subject, String body, String chooserTitle) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(sharingIntent, chooserTitle));
    }
    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } */
    }
}

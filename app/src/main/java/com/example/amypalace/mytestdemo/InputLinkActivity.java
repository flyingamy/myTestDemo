package com.example.amypalace.mytestdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class InputLinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_link);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.share));

        // Bottom navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Input URL to scrape web information
        final EditText mUrlEdit = (EditText) findViewById(R.id.edittext_url_publish);

        findViewById(R.id.button_submit_publish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("".equals(mUrlEdit.getText().toString())) {
                    Toast.makeText(InputLinkActivity.this, "请输入一个严选链接", Toast.LENGTH_SHORT).show();
                    return;
                }

                //================== 调用云函数提取商品的名称和图片 ========================
                // 在 Android SDK 中，AVCloud 提供了一系列的静态方法来实现客户端调用云函数构建参数
                HashMap<String, String> dicParameters = new HashMap<String, String>();
                dicParameters.put("url", mUrlEdit.getText().toString());

                // 调用云函数 yanxuanscrape
                Log.i("yanxuan", "===== call cloudfunc ======");
                AVCloud.callFunctionInBackground("yanxuanscrape", dicParameters, new FunctionCallback() {
                    public void done(Object object, AVException e) {
                        if (e == null) {
                            String object_class = object.getClass().toString();
                            String scraped = "", product_title = "", image_url = "";
                            scraped = object.toString();

                            if (object instanceof HashMap) {
                                HashMap map = (HashMap) object;
                                if (map.containsKey("title"))
                                    product_title = map.get("title").toString();
                                if (map.containsKey("image_url"))
                                    image_url = map.get("image_url").toString();
                            }

                            Log.i("scraped", scraped);
                            Log.i("object_class", object_class);
                            Log.i("product_title", product_title);
                            Log.i("image_url", image_url);
                            Log.i("end", "======end of test code=========");

                            Intent intent = new Intent(InputLinkActivity.this, ShareActivity.class);
                            intent.putExtra("product_title", product_title);
                            intent.putExtra("image_url", image_url);

                            startActivity(intent);
                            InputLinkActivity.this.finish();
                            // 处理返回结果
                        } else {
                            // 处理报错
                        }
                    }
                });
                //=========== 结束云函数调用 =====================================

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }


    // ========== Navigation Bar ========================
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Log.i("navigation", "item selected");
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Log.i("navigation", "discover");
                    startActivity(new Intent(InputLinkActivity.this, MainActivity.class));
                    InputLinkActivity.this.finish();
                    return true;
                case R.id.navigation_dashboard:
                    Log.i("navigation", "my friends");
                    return true;
                case R.id.navigation_notifications:
                    Log.i("navigation", "my");
                    return true;
            }
            Log.i("navigation", "end");
            return false;
        }
    };

}
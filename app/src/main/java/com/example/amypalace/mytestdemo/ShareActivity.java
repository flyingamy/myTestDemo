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
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class ShareActivity extends AppCompatActivity {

    private ImageView mImageViewSelect;
    private byte[] mImageBytes = null;
    private Handler mHandler = new Handler();
    private ProgressBar mProgerss;
    private TextView mProductName;
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        // Show the scraped product_title and image
        mImage = (ImageView) findViewById(R.id.image_detail);
        mProductName = (TextView) findViewById(R.id.name_detail);
        final String product_title = getIntent().getStringExtra("product_title");
        final String image_url = getIntent().getStringExtra("image_url");
        mProductName.setText(product_title);
        Picasso.with(ShareActivity.this).load(image_url).into(mImage);

        // Let the user input comment.
        mImageViewSelect = (ImageView) findViewById(R.id.imageview_select_publish);
        mProgerss = (ProgressBar) findViewById(R.id.mProgess);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.share));

        Button mButtonSelect = (Button) findViewById(R.id.button_select_publish);
        mButtonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 42);
            }
        });

        final EditText mCommentEdit = (EditText) findViewById(R.id.edittext_comment_publish);
        final EditText mPriceEdit = (EditText) findViewById(R.id.edittext_price_publish);

        findViewById(R.id.button_submit_publish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                if ("".equals(mCommentEdit.getText().toString())) {
                    Toast.makeText(ShareActivity.this, "请输入对商品的评价", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("".equals(mPriceEdit.getText().toString())) {
                    Toast.makeText(ShareActivity.this, "请输入购买价格", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mImageBytes == null) {
                    Toast.makeText(ShareActivity.this, "请选择一张照片", Toast.LENGTH_SHORT).show();
                    return;
                }
                */
                mProgerss.setVisibility(View.VISIBLE);

                AVObject product = new AVObject("Todo");
                product.put("product_title", product_title);
                product.put("image_url", image_url);
                product.put("comment", mCommentEdit.getText().toString());
                product.put("price", Integer.parseInt(mPriceEdit.getText().toString()));
                product.put("owner", AVUser.getCurrentUser());
                product.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            mProgerss.setVisibility(View.GONE);
                            ShareActivity.this.finish();
                        } else {
                            mProgerss.setVisibility(View.GONE);
                            Toast.makeText(ShareActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 42 && resultCode == RESULT_OK) {
            try {
                mImageViewSelect.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData()));
                mImageBytes = getBytes(getContentResolver().openInputStream(data.getData()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        return byteArrayOutputStream.toByteArray();
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
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    startActivity(new Intent(ShareActivity.this, MainActivity.class));
                    ShareActivity.this.finish();
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

}
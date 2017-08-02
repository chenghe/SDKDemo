package com.zsmarter.sdklib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by hechengbin on 2017/6/22.
 */

public class SDKMainActivity extends Activity{

    private Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdk_main);

        Intent dataIntent = getIntent();
        final String url = dataIntent.getStringExtra(SDKWebActivity.SDKWEBURL);


        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(url)){
                    Intent intent = new Intent(SDKMainActivity.this,SDKWebActivity.class);
                    intent.putExtra(SDKWebActivity.SDKWEBURL,url);
                    startActivity(intent);
                }else {
                    Toast.makeText(SDKMainActivity.this,"url is empty!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

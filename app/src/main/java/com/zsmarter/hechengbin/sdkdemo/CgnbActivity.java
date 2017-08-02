package com.zsmarter.hechengbin.sdkdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.cgnb.bean.RequestBean;

/**
 * Created by hechengbin on 2017/7/31.
 */

public class CgnbActivity extends Activity implements View.OnClickListener{

    private Button btFirst;
    private Button btStart;
    private Button btClean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cgnb);


        RequestBean bean = new RequestBean();
        bean.setUrl("http://172.32.11.96:8080/mobilebank/ws/login");
        bean.setURL_PREFIX("http://172.32.11.96:8080/mobilebank/");
        bean.setFIRST_URL("ses/mykey/");
        bean.setSECOND_URL("ses/getKey/");
        bean.setTHIRD_URL("ses/setKey/");
        bean.setPUBLIC_URL("ses/public/");
        bean.setType("post");
        bean.setAes(true);
        bean.setJson("{\"userAccount\":13550064079,\"userPassword\":\"6E81A58BE4A3E2D60DAB3460D4AF88BA190133A5\"}");



        btFirst = (Button) findViewById(R.id.btfirst);
        btFirst.setOnClickListener(this);
        btStart = (Button) findViewById(R.id.btstart);
        btStart.setOnClickListener(this);
        btClean = (Button) findViewById(R.id.btclean);
        btClean.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btfirst:
                break;
            case R.id.btstart:
                break;
            case R.id.btclean:
                break;
        }
    }
}

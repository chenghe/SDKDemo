package com.zsmarter.hechengbin.sdkdemo.securityplugin.bean;

/**
 * Created by hechengbin on 2017/11/6.
 */

public class GetListBean extends SecurityBean {
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public class Context{
        private String list;

        public String getList() {
            return list;
        }

        public void setList(String list) {
            this.list = list;
        }
    }
}

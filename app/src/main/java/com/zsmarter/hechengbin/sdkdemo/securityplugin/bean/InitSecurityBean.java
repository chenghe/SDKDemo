package com.zsmarter.hechengbin.sdkdemo.securityplugin.bean;

/**
 * Created by hechengbin on 2017/11/2.
 */

public class InitSecurityBean extends SecurityBean {
    private Context context;

    public class Context{
        private String duration;
        private String publicKey;
        private String privateKey;
        private String token;

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}

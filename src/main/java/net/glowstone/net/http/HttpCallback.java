package net.glowstone.net.http;

public interface HttpCallback {

    void done(String response);

    void error(Throwable throwable);

}

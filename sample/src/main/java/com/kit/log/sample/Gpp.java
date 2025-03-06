package com.kit.log.sample;

import com.orhanobut.logger.LogKit;

public class Gpp {

    public void test() {
        LogKit.INSTANCE.d("Hello World");
        LogKit.INSTANCE.d("GG", "Hello World");

    }
}

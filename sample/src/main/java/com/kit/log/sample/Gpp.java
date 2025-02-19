package com.kit.log.sample;

import com.kit.log.LogKit;

public class Gpp {

    public void test() {
        LogKit.INSTANCE.d("Hello World");
        LogKit.INSTANCE.d("GG", "Hello World");

    }
}

package com.laomazk.tools;

import com.laomazk.tools.model.MyObject;
import org.junit.jupiter.api.Test;

class JsonUtilsTest {

    @Test
    void toJsonStr(){
        MyObject myObject = new MyObject("foo", 42);
        String s = JsonUtils.writeValueAsString(myObject);
        System.out.println("s = " + s);
    }

    @Test
    void toBean(){
        String str = "{\"foo\":\"foo\",\"bar\":42}";
        MyObject myObject = JsonUtils.readValue(str, MyObject.class);
        System.out.println(myObject);
    }
}
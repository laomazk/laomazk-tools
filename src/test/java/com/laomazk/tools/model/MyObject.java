package com.laomazk.tools.model;

public class MyObject {

    private String foo;
    private int bar;

    public MyObject() {
    }

    public MyObject(String foo, int bar) {
        this.foo = foo;
        this.bar = bar;
    }

    public String getFoo() {
        return foo;
    }

    public int getBar() {
        return bar;
    }

    @Override
    public String toString() {
        return "MyObject{" +
                "foo='" + foo + '\'' +
                ", bar=" + bar +
                '}';
    }
}

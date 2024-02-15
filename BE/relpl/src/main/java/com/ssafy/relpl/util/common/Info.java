package com.ssafy.relpl.util.common;

public class Info {
    public int cur; public long distSum; public double weightSum;
    public Info(int _cur, long _distSum) {
        cur = _cur; distSum = _distSum;
    }

    public Info(int _cur, double _weightSum) {
        cur = _cur; weightSum = _weightSum;
    }
}

package com.ssafy.relpl.util.common;


public class Edge {
    public int to, dist;
    public long roadHash;
    public Edge(int _to, int _dist, long _roadHash) {
        to = _to; dist = _dist; roadHash = _roadHash;
    }
}

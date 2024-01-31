package com.ssafy.relpl.util.common;


public class Edge {
    public int to, dist, roadHash;
    public Edge(int _to, int _dist, int _roadHash) {
        to = _to; dist = _dist; roadHash = _roadHash;
    }
}

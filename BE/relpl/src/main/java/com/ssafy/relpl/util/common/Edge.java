package com.ssafy.relpl.util.common;


public class Edge {
    public int to, dist;
    public double weight;
    public Long roadHash;
    public Edge(int _to, int _dist, Long _roadHash, double _weight) {
        to = _to; dist = _dist; roadHash = _roadHash; weight = _weight;
    }
}

package com.perfma.xlab.xpocket.structure;

/**
 * @author: ZQF
 * @date: 2021-04-16
 * @description: desc
 */
public class Pair<F, S> {
    private F first;

    private S second;

    public Pair(F first, S second){
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}

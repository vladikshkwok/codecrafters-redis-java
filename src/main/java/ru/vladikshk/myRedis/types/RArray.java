package ru.vladikshk.myRedis.types;

import java.util.List;

public class RArray extends RType {
    private List<String> array;
    public RArray(List<String> array) {
        this.array = array;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("*").append(array.size()).append("\r\n");
        for (String el : array) {
            sb.append(new RBulkString(el));
        }
        return sb.toString();
    }
}

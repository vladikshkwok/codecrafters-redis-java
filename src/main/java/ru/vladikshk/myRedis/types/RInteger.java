package ru.vladikshk.myRedis.types;

public class RInteger extends RType {
    private Integer val;
    public RInteger(Integer val) {
        this.val = val;
    }

    @Override
    public String toString() {
        if (val == null) {
            val = -1;
        }

        return ":" + val + "\r\n";
    }
}

package ru.vladikshk.myRedis.types;

public class RString extends RType {
    private String str;
    public RString(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        if (str == null) {
            str = "-1";
        }

        return "+" + str + "\r\n";
    }
}

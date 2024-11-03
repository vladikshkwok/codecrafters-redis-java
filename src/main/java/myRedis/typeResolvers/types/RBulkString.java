package myRedis.typeResolvers.types;

public class RBulkString extends RType {
    private String str;
    public RBulkString(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        if (str == null) {
            return "$-1\r\n";
        }
        return "$" + str.length() + "\r\n" + str + "\r\n";
    }
}

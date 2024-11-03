package commands.typeResolvers.types;

public class RBulkString extends RType {
    private String str;
    public RBulkString(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return "$" + str.length() + "\r\n" + str + "\r\n";
    }
}

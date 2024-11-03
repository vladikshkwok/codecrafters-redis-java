package commands.typeResolvers.types;

public class RString extends RType {
    private String str;
    public RString(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return "$" + str.length() + "\r\n" + str + "\r\n";
    }
}

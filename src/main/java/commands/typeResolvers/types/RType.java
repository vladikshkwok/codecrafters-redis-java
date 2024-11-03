package commands.typeResolvers.types;

public abstract class RType {
    public byte[] getBytes() {
        return toString().getBytes();
    }
}

package netpong.network;

// TODO use enum insted of string value "BAX", "BAY" ...
public class PacketBuilder {

    private StringBuilder builder = new StringBuilder();

    public PacketBuilder setClient() {
        builder.append('C');
        return this;
    }
    public PacketBuilder setServer() {
        builder.append('S');
        return this;
    }
    public PacketBuilder setOk() {
        builder.append("OK");
        return this;
    }
    public PacketBuilder setReady() {
        builder.append("REA");
        return this;
    }
    public PacketBuilder setPause() {
        builder.append("PAU");
        return this;
    }
    public PacketBuilder setServerPlayerPoint(int point) {
        builder.append("PS");
        builder.append(String.format("%03d", point));
        return this;
    }
    public PacketBuilder setClientPlayerPoint(int point) {
        builder.append("PC");
        builder.append(String.format("%03d", point));
        return this;
    }
    public PacketBuilder setPlayerY(int y) {
        builder.append("PY");
        builder.append(String.format("%03d", y));
        return this;
    }
    public PacketBuilder setPlayerX(int x) {
        builder.append("PX");
        builder.append(String.format("%03d", x));
        return this;
    }
    public PacketBuilder setBallX(int x) {
        builder.append("BX");
        builder.append(String.format("%03d", x));
        return this;
    }
    public PacketBuilder setBallY(int y) {
        builder.append("BY");
        builder.append(String.format("%03d", y));
        return this;
    }

    public String toString() {
        return builder.toString();
    }

}

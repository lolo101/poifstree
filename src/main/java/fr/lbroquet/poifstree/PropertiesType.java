package fr.lbroquet.poifstree;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Function;

 enum PropertiesType {
    PtypInteger32(0x0003, (byte[] ba) -> String.format("value: %d (%1$08X)", asInt32(ba))),
    PtypString(0x001F, (byte[] ba) -> "size: " + asInt32(ba)),
    PtypBinary(0x0102, (byte[] ba) -> "size: " + asInt32(ba)),
    PtypBoolean(0x000B, (ba) -> "value: " + (ba[0] == 1));

    private static int asInt32(byte[] ba) {
        return ByteBuffer.wrap(ba).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private final int id;
    private final Function<byte[], String> formatter;

    private PropertiesType(int id, Function<byte[], String> formatter) {
        this.id = id;
        this.formatter = formatter;
    }

    static PropertiesType fromId(int id) {
        for (PropertiesType t : values()) {
            if (t.id == id) {
                return t;
            }
        }
        throw new IllegalArgumentException("No Type with value " + id);
    }

    public int getId() {
        return id;
    }

    public String format(byte[] value) {
        return formatter.apply(value);
    }

}

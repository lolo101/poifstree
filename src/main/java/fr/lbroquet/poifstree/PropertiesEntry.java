package fr.lbroquet.poifstree;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Function;

import org.apache.poi.poifs.filesystem.DocumentInputStream;

public class PropertiesEntry {

	private enum Type {
		INT32(0x0003, ba -> String.format("value: %d (%1$08X)", asInt32(ba))),
		STRING(0x001F, ba -> "size: " + asInt32(ba)),
		BYTES(0x0102, ba -> "size: " + asInt32(ba)),
		BOOLEAN(0x000B, ba -> "value: " + (ba[0] == 1));

		private static int asInt32(byte[] ba) {
			return ByteBuffer.wrap(ba).order(ByteOrder.LITTLE_ENDIAN).getInt();
		}

		private final int id;
		private final Function<byte[], String> formatter;

		private Type(int id, Function<byte[], String> formatter) {
			this.id = id;
			this.formatter = formatter;
		}

		private static Type fromId(int id) {
			for (Type t : values()) {
				if (t.id == id) {
					return t;
				}
			}
			throw new IllegalArgumentException("No Type with value " + id);
		}

		private String format(byte[] value) {
			return formatter.apply(value);
		}
	}

	private final int tag;
	private final int flags;
	private final Type type;
	private final byte[] value = new byte[8];

	public PropertiesEntry(DocumentInputStream inputStream) throws IOException {
		int tagAndType = inputStream.readInt();
		tag = tagAndType >> 16;
		flags = inputStream.readInt();
		type = Type.fromId(tagAndType & 0x0000FFFF);
		inputStream.read(value);
	}

	@Override
	public String toString() {
        return dumpTagAndFlags() + dumpValue();
	}

	private String dumpTagAndFlags() {
		return String.format("tag  : %04X%ntype: %04X (%s)%nflags: %08X%n", tag, type.id, type, flags);
	}

	private String dumpValue() {
		return type.format(value);
	}
}

package fr.lbroquet.poifstree;

import java.io.IOException;
import java.util.ResourceBundle;

import org.apache.poi.poifs.filesystem.DocumentInputStream;

public class PropertiesEntry {

    private static final ResourceBundle TAGS_REFERENCE = ResourceBundle.getBundle("fr.lbroquet.poifstree.tags");


    private final int tag;
    private final int flags;
    private final PropertiesType type;
    private final byte[] value = new byte[8];

    public PropertiesEntry(DocumentInputStream inputStream) throws IOException {
        int tagAndType = inputStream.readInt();
        tag = tagAndType >> 16;
        flags = inputStream.readInt();
        type = PropertiesType.fromId(tagAndType & 0x0000FFFF);
        inputStream.read(value);
    }

    @Override
    public String toString() {
        return dumpTagAndTypeAndFlags() + dumpValue();
    }

    private String dumpTagAndTypeAndFlags() {
        return String.format("tag  : %04X (%s)%ntype: %04X (%s)%nflags: %08X%n", tag, getTagName(), type.getId(), type, flags);
    }

    private String dumpValue() {
        return type.format(value);
    }

    private String getTagName() {
        return TAGS_REFERENCE.getString(String.format("%04X", tag));
    }
}

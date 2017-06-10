package fr.lbroquet.poifstree;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

public class SubstgEntry {

    private static final ResourceBundle TAGS_REFERENCE = ResourceBundle.getBundle("fr.lbroquet.poifstree.tags");

    private final int tag;
    private final PropertiesType type;
    private final DocumentEntry entry;

    public SubstgEntry(DocumentEntry document) {
        String name = document.getName();
        int nameLength = name.length();
        String tagString = name.substring(nameLength - 8, nameLength - 4);
        String typeString = name.substring(nameLength - 4, nameLength);
        tag = Integer.parseInt(tagString, 16);
        type = PropertiesType.fromId(Integer.parseInt(typeString, 16));
        entry = document;
    }

    @Override
    public String toString() {
        return dumpTagAndType() + dumpStream();
    }

    private String dumpTagAndType() {
        return String.format("tag  : %04X (%s)%ntype: %04X (%s)%n>>>>>>>>%n", tag, getTagName(), type.getId(), type);
    }

    private String dumpStream() {
        byte[] content = getContent();

        switch (type) {
            case PtypString:
                return dumpText(content);
            case PtypBinary:
                return dumpBinary(content);
        }
        throw new RuntimeException("Unsupported type " + type);
    }

    private byte[] getContent() {
        byte[] content = new byte[entry.getSize()];
        try (DocumentInputStream inputStream = new DocumentInputStream(entry)) {
            inputStream.read(content);
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
        return content;
    }

    private static String dumpText(byte[] content) {
        return new String(content, StandardCharsets.UTF_16LE);
    }

    private static String dumpBinary(byte[] content) {
        StringBuilder builder = new StringBuilder(content.length * 3);
        for (byte b : content) {
            builder.append(String.format("%02X ", b));
        }
        return builder.toString();
    }

    private String getTagName() {
        return TAGS_REFERENCE.getString(String.format("%04X", tag));
    }
}

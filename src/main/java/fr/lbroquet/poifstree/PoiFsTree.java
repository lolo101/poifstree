package fr.lbroquet.poifstree;

import java.io.IOException;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;

public class PoiFsTree {

    private final NPOIFSFileSystem document;

    public PoiFsTree(NPOIFSFileSystem document) {
        this.document = document;
    }

    public void analyse() {
        analyse(document.getRoot(), 0);
    }

    private static void analyse(Entry node, int depth) {
        printIndented(depth, node.getName());
        if (node.isDirectoryEntry()) {
            DirectoryEntry directory = (DirectoryEntry) node;
            directory.getEntries().forEachRemaining(e -> analyse(e, depth + 1));
        }
        if (node.isDocumentEntry()) {
            DocumentEntry document = (DocumentEntry) node;
            showContent(document, depth);
        }
    }

    private static void showContent(DocumentEntry document, int depth) {
        try (DocumentInputStream inputStream = new DocumentInputStream(document)) {
            System.out.println("--------");
            if (document.getName().equals("__properties_version1.0")) {
                printProperties(inputStream, depth);
            } else if (document.getName().endsWith("001F")) {
                printText(inputStream);
            } else {
                printBinary(inputStream);
            }
            System.out.println("\n--------");
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    private static void printIndented(int depth, Object value) {
        for(int i = 0; i < depth; ++i) {
            System.out.print("\t");
        }
        System.out.println(value);
    }

    private static void printProperties(DocumentInputStream inputStream, int depth) throws IOException {
        dumpHeader(inputStream);
        if (depth == 1) {
            printTopLevelProperties(inputStream);
        } else {
            printLowLevelProperties(inputStream);
        }
    }

    private static void printTopLevelProperties(DocumentInputStream inputStream) {
        System.out.println("Next Recipient Id : " + inputStream.readInt());
        System.out.println("Next Attachment Id: " + inputStream.readInt());
        System.out.println("Recipient Count   : " + inputStream.readInt());
        System.out.println("Attachment Count  : " + inputStream.readInt());
    }

    private static void printLowLevelProperties(DocumentInputStream inputStream) throws IOException {
        while (inputStream.available() > 0) {
            System.out.println();
            System.out.println(new PropertiesEntry(inputStream));
        }
    }

    private static void dumpHeader(DocumentInputStream inputStream) throws IOException {
        inputStream.read(new byte[8]);
    }

    private static void printText(DocumentInputStream inputStream) {
        while (inputStream.available() > 0) {
            short readShort = inputStream.readShort();
            System.out.print((char) readShort);
        }
    }

    private static void printBinary(DocumentInputStream inputStream) {
        while (inputStream.available() > 0) {
            byte readByte = inputStream.readByte();
            System.out.printf("%02X ", readByte);
        }
    }
}

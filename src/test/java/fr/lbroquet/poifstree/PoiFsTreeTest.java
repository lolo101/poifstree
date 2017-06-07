package fr.lbroquet.poifstree;

import java.io.IOException;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.junit.Before;
import org.junit.Test;

public class PoiFsTreeTest {

    private PoiFsTree instance;
    @Before
    public void before() throws IOException {
        instance = new PoiFsTree(new NPOIFSFileSystem(PoiFsTreeTest.class.getResourceAsStream("/fox.msg")));
    }

    @Test
    public void testAnalyse() {
        instance.analyse();
    }

}

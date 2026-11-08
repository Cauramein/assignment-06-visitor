import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemVisitorTest {

    @Test
    @DisplayName("T1: Both element types reach the correct overloaded visit methods")
    void testDoubleDispatchResolution() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        FileNode file = new FileNode("single.txt", 100);
        DirectoryNode dir = new DirectoryNode("emptyDir");

        file.accept(visitor);
        assertEquals(100, visitor.getTotalSize());

        dir.accept(visitor);
        assertEquals(100, visitor.getTotalSize());
    }

    @Test
    @DisplayName("T2: Size accumulation across multiple nesting levels is correct")
    void testSizeAccumulationNested() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode sub1 = new DirectoryNode("sub1");
        DirectoryNode sub2 = new DirectoryNode("sub2");

        root.addElement(new FileNode("f1.txt", 100));
        sub1.addElement(new FileNode("f2.txt", 200));
        sub2.addElement(new FileNode("f3.txt", 300));

        sub1.addElement(sub2);
        root.addElement(sub1);

        TotalSizeVisitor visitor = new TotalSizeVisitor();
        root.accept(visitor);

        assertEquals(600, visitor.getTotalSize());
    }

    @Test
    @DisplayName("T3: Search correctly combines both configurable filter conditions")
    void testSearchCombinedConditions() {
        DirectoryNode root = new DirectoryNode("root");
        root.addElement(new FileNode("log_a.txt", 50));
        root.addElement(new FileNode("log_b.txt", 150));
        root.addElement(new FileNode("info.txt", 200));

        SearchVisitor visitor = new SearchVisitor("log", 100);
        root.accept(visitor);

        List<String> results = visitor.getMatchingPaths();
        assertEquals(1, results.size());
        assertEquals("log_b.txt", results.get(0));
    }

    @Test
    @DisplayName("T4: Search produces correct, deterministic, root-relative paths")
    void testSearchDeterministicRootRelativePaths() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode bDir = new DirectoryNode("b_dir");
        DirectoryNode aDir = new DirectoryNode("a_dir");

        bDir.addElement(new FileNode("test.txt", 10));
        aDir.addElement(new FileNode("test.txt", 10));

        root.addElement(bDir);
        root.addElement(aDir);

        SearchVisitor visitor = new SearchVisitor("test.txt", 0);
        root.accept(visitor);

        List<String> results = visitor.getMatchingPaths();
        assertEquals(List.of("a_dir/test.txt", "b_dir/test.txt"), results);
    }

    @Test
    @DisplayName("T5: Statistics are correct for nesting, counting, and maximum depth")
    void testStatisticsCalculations() {
        DirectoryNode root = new DirectoryNode("root");
        root.addElement(new FileNode("file1.txt", 10));
        DirectoryNode l1 = new DirectoryNode("l1");
        DirectoryNode l2 = new DirectoryNode("l2");
        l2.addElement(new FileNode("deep.txt", 20));

        l1.addElement(l2);
        root.addElement(l1);

        StatisticsVisitor visitor = new StatisticsVisitor();
        root.accept(visitor);

        assertEquals(2, visitor.getFileCount());
        assertEquals(3, visitor.getDirectoryCount());
        assertEquals(3, visitor.getMaxDepth());
    }

    @Test
    @DisplayName("T6: Empty structures and no-match search cases are handled")
    void testEmptyAndNoMatch() {
        DirectoryNode root = new DirectoryNode("emptyRoot");

        StatisticsVisitor stats = new StatisticsVisitor();
        root.accept(stats);
        assertEquals(0, stats.getFileCount());
        assertEquals(1, stats.getDirectoryCount());
        assertEquals(0, stats.getMaxDepth());

        SearchVisitor search = new SearchVisitor("missing", 100);
        root.accept(search);
        assertTrue(search.getMatchingPaths().isEmpty());
    }

    @Test
    @DisplayName("T7: Repeat executions do not leak or multiply accumulated state")
    void testRepeatExecutionsIsolation() {
        DirectoryNode root = new DirectoryNode("root");
        root.addElement(new FileNode("file.txt", 100));

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);
        assertEquals(100, sizeVisitor.getTotalSize());

        sizeVisitor.reset();
        root.accept(sizeVisitor);
        assertEquals(100, sizeVisitor.getTotalSize());
    }

    @Test
    @DisplayName("T8: Invalid tree mutations are rejected while valid nesting remains supported")
    void testTreeIntegrityRejections() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad.txt", -5));

        DirectoryNode dir = new DirectoryNode("dir");
        dir.addElement(new FileNode("file.txt", 10));
        assertThrows(IllegalArgumentException.class, () -> dir.addElement(new FileNode("file.txt", 20)));

        DirectoryNode parentA = new DirectoryNode("parentA");
        DirectoryNode parentB = new DirectoryNode("parentB");
        FileNode shared = new FileNode("shared.txt", 50);
        parentA.addElement(shared);
        assertThrows(IllegalStateException.class, () -> parentB.addElement(shared));

        DirectoryNode a = new DirectoryNode("a");
        DirectoryNode b = new DirectoryNode("b");
        a.addElement(b);
        assertThrows(IllegalArgumentException.class, () -> b.addElement(a));
    }
}
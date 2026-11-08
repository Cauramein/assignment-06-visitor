public class Main {
    public static void main(String[] args) {
        DirectoryNode root = new DirectoryNode("workspace");
        DirectoryNode src = new DirectoryNode("src");
        DirectoryNode assets = new DirectoryNode("assets");

        src.addElement(new FileNode("App.java", 1200));
        src.addElement(new FileNode("Utils.java", 600));
        assets.addElement(new FileNode("logo.png", 5400));
        root.addElement(src);
        root.addElement(assets);
        root.addElement(new FileNode("README.md", 300));

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);
        System.out.println("Total Size: " + sizeVisitor.getTotalSize() + " bytes");

        SearchVisitor searchVisitor = new SearchVisitor(".java", 800);
        root.accept(searchVisitor);
        System.out.println("\nSearch (.java, >= 800 bytes):");
        searchVisitor.getMatchingPaths().forEach(p -> System.out.println(" - " + p));

        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        root.accept(statsVisitor);
        System.out.println("\nStatistics:");
        System.out.printf("Files: %d, Directories: %d, Max Depth: %d%n",
                statsVisitor.getFileCount(), statsVisitor.getDirectoryCount(), statsVisitor.getMaxDepth());
    }
}
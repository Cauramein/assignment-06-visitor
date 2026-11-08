public class TotalSizeVisitor implements FileSystemVisitor {
    private long totalSize = 0;

    @Override
    public void visitFile(FileNode file) {
        totalSize += file.getSize();
    }

    @Override
    public void visitDirectory(DirectoryNode directory) {
        for (FileSystemElement child : directory.getChildren()) {
            child.accept(this);
        }
    }

    public long getTotalSize() {
        return totalSize;
    }

    @Override
    public void reset() {
        this.totalSize = 0;
    }
}
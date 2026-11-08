public interface FileSystemVisitor {
    void visitFile(FileNode file);
    void visitDirectory(DirectoryNode directory);
    void reset();
}
import java.util.Objects;

public class FileNode implements FileSystemElement {
    private final String name;
    private final long size;
    private DirectoryNode parent;

    public FileNode(String name, long size) {
        if (name == null || name.isBlank() || name.contains("/")) {
            throw new IllegalArgumentException("Invalid file name: " + name);
        }
        if (size < 0) {
            throw new IllegalArgumentException("File size cannot be negative: " + size);
        }
        this.name = name;
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DirectoryNode getParent() {
        return parent;
    }

    @Override
    public void setParent(DirectoryNode parent) {
        this.parent = parent;
    }

    @Override
    public void accept(FileSystemVisitor visitor) {
        Objects.requireNonNull(visitor, "Visitor cannot be null");
        visitor.visitFile(this);
    }
}
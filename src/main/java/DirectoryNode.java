import java.util.*;

public class DirectoryNode implements FileSystemElement {
    private final String name;
    private DirectoryNode parent;
    private final Map<String, FileSystemElement> children = new HashMap<>();

    public DirectoryNode(String name) {
        if (name == null || name.isBlank() || name.contains("/")) {
            throw new IllegalArgumentException("Invalid directory name: " + name);
        }
        this.name = name;
    }

    public void addElement(FileSystemElement element) {
        Objects.requireNonNull(element, "Child element cannot be null");

        if (element.getParent() != null) {
            throw new IllegalStateException("Element already has a parent: " + element.getName());
        }
        if (children.containsKey(element.getName())) {
            throw new IllegalArgumentException("Duplicate sibling name detected: " + element.getName());
        }
        if (element == this || isAncestorOfThis(element)) {
            throw new IllegalArgumentException("Adding this element introduces a cyclic reference: " + element.getName());
        }

        element.setParent(this);
        children.put(element.getName(), element);
    }

    private boolean isAncestorOfThis(FileSystemElement candidate) {
        DirectoryNode curr = this;
        while (curr != null) {
            if (curr == candidate) {
                return true;
            }
            curr = curr.getParent();
        }
        return false;
    }

    public List<FileSystemElement> getChildren() {
        List<FileSystemElement> sorted = new ArrayList<>(children.values());
        sorted.sort(Comparator.comparing(FileSystemElement::getName));
        return Collections.unmodifiableList(sorted);
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
        visitor.visitDirectory(this);
    }
}
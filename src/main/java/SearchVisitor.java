import java.util.*;

public class SearchVisitor implements FileSystemVisitor {
    private final String nameCriterion;
    private final long minSizeThreshold;
    private final List<String> matchingPaths = new ArrayList<>();
    private final Deque<String> pathStack = new ArrayDeque<>();
    private boolean isRootInitialized = false;

    public SearchVisitor(String nameCriterion, long minSizeThreshold) {
        this.nameCriterion = (nameCriterion == null) ? "" : nameCriterion;
        this.minSizeThreshold = Math.max(0, minSizeThreshold);
    }

    @Override
    public void visitFile(FileNode file) {
        boolean nameMatch = file.getName().contains(nameCriterion);
        boolean sizeMatch = file.getSize() >= minSizeThreshold;

        if (nameMatch && sizeMatch) {
            String relativePath = buildCurrentRelativePath(file.getName());
            matchingPaths.add(relativePath);
        }
    }

    @Override
    public void visitDirectory(DirectoryNode directory) {
        boolean isRootCall = !isRootInitialized;
        if (isRootCall) {
            isRootInitialized = true;
        } else {
            pathStack.addLast(directory.getName());
        }

        for (FileSystemElement child : directory.getChildren()) {
            child.accept(this);
        }

        if (!isRootCall) {
            pathStack.removeLast();
        }
    }

    private String buildCurrentRelativePath(String fileName) {
        if (pathStack.isEmpty()) {
            return fileName;
        }
        return String.join("/", pathStack) + "/" + fileName;
    }

    public List<String> getMatchingPaths() {
        List<String> sorted = new ArrayList<>(matchingPaths);
        Collections.sort(sorted);
        return Collections.unmodifiableList(sorted);
    }

    @Override
    public void reset() {
        this.matchingPaths.clear();
        this.pathStack.clear();
        this.isRootInitialized = false;
    }
}
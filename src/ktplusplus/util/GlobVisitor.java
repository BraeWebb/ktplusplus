package ktplusplus.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class GlobVisitor extends SimpleFileVisitor<Path> {
    private List<PathMatcher> matchers = new ArrayList<>();
    private List<PathMatcher> antimatchers = new ArrayList<>();
    private List<File> files = new ArrayList<>();

    public GlobVisitor() {
    }

    public void addMatch(PathMatcher matcher) {
        matchers.add(matcher);
    }

    public void addAntiMatch(PathMatcher matcher) {
        antimatchers.add(matcher);
    }

    @Override
    public FileVisitResult visitFile(Path path,
                                     BasicFileAttributes attrs) throws IOException {
        boolean matches = false;
        for (PathMatcher matcher : matchers) {
            if (matcher.matches(path)) {
                matches = true;
                break;
            }
        }

        for (PathMatcher matcher : antimatchers) {
            if (matcher.matches(path)) {
                matches = false;
                break;
            }
        }

        if (matches) {
            this.files.add(path.toFile());
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc)
            throws IOException {
        return FileVisitResult.CONTINUE;
    }

    public List<File> getFiles() {
        return new ArrayList<>(files);
    }
}
package ktplusplus.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;

public class StudentFolder {
    private Path base;
    private List<File> files;

    private StudentFolder(Path base) {
        this.base = base;
    }

    public static StudentFolder load(Path base, List<String> files) throws IOException {
        StudentFolder folder = new StudentFolder(base);

        GlobVisitor visitor = new GlobVisitor();
        for (String pattern : files) {
            String patt = pattern.startsWith("~") ? pattern.substring(1) : pattern;
            PathMatcher matcher = FileSystems.getDefault()
                    .getPathMatcher("glob:" + patt);
            if (pattern.startsWith("~")) {
                visitor.addAntiMatch(matcher);
            } else {
                visitor.addMatch(matcher);
            }
        }

        Files.walkFileTree(base, visitor);

        folder.files = visitor.getFiles();

        return folder;
    }

    public String getStudent() {
        return this.base.subpath(base.getNameCount() - 1, base.getNameCount()).toString();
    }

    public List<File> getFiles() {
        return files;
    }
}

package ktplusplus.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileLoader {
    private Path base;
    private List<String> files;
    private List<IOException> ioExceptions;

    private FileLoader(Path base, List<String> files) {
        this.base = base;
        this.files = files;
    }

    public static FileLoader load(Path base, List<String> files) {
        return new FileLoader(base, files);
    }

    private StudentFolder loadFolder(Path folder) {
        try {
            return StudentFolder.load(folder, files);
        } catch (IOException e) {
            ioExceptions.add(e);
            return null;
        }
    }

    public List<StudentFolder> getFolders() throws IOException {
        return Files.list(base).map(this::loadFolder).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<IOException> getIoExceptions() {
        return ioExceptions;
    }
}

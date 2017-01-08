package net.team33.order;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

public class Movement {

    private final Resolver resolver;

    public Movement(final Path targetRoot) {
        this.resolver = new Resolver(targetRoot);
    }

    public void accept(final Path path) throws IOException {
        final FileTime lastModifiedTime = Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS);
        final Path target = resolve(lastModifiedTime, new Filename(path.getFileName().toString()));
        Files.createDirectories(target.getParent());
        Files.move(path, target);
    }

    private Path resolve(final FileTime lastModifiedTime, final Filename fileName) {
        return resolve(resolver.apply(lastModifiedTime), fileName);
    }

    private Path resolve(final Path parent, final Filename fileName) {
        Path result = parent.resolve(fileName.compose());
        for (int index = 1; Files.exists(result, LinkOption.NOFOLLOW_LINKS); ++index) {
            result = parent.resolve(fileName.compose(index));
        }
        return result;
    }

    private static class Filename {
        private final String prefix;
        private final String postfix;

        private Filename(final String fileName) {
            final int limit = fileName.lastIndexOf('.');
            if (0 > limit) {
                prefix = fileName;
                postfix = "";
            } else {
                prefix = fileName.substring(0, limit);
                postfix = fileName.substring(limit);
            }
        }

        public String compose() {
            return prefix + postfix;
        }

        public String compose(final int index) {
            return String.format("%s[%02d]%s", prefix, index, postfix);
        }
    }
}

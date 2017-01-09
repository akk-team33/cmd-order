package net.team33.order;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;

public class Movement {

    private final Resolver resolver;

    public Movement(final Path targetRoot, final List<Resolver.PathElement> elements) {
        this.resolver = new Resolver(targetRoot, elements);
    }

    public void accept(final Path path) throws IOException {
        final FileTime lastModifiedTime = Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS);
        final Path target = resolve(lastModifiedTime, path.getFileName().toString());
        Files.createDirectories(target.getParent());
        Files.move(path, target);
    }

    private Path resolve(final FileTime lastModifiedTime, final String fileName) {
        return new Filename(fileName).resolve(lastModifiedTime);
    }

    private class Filename {
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

        public final Path resolve(final FileTime lastModifiedTime) {
            return resolve(resolver.resolve(
                    lastModifiedTime,
                    postfix.startsWith(".") ? postfix.substring(1) : postfix));
        }

        private Path resolve(final Path parent) {
            Path result = parent.resolve(compose());
            for (int index = 1; Files.exists(result, LinkOption.NOFOLLOW_LINKS); ++index) {
                result = parent.resolve(compose(index));
            }
            return result;
        }

        private String compose() {
            return prefix + postfix;
        }

        private String compose(final int index) {
            return String.format("%s[%02d]%s", prefix, index, postfix);
        }
    }
}

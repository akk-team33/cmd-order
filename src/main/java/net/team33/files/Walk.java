package net.team33.files;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Walk {

    private final Path path;

    private Walk(final Path path) {
        this.path = path;
    }

    public static Walk through(final Path path) {
        return new Walk(path);
    }

    public final Map<Path, Exception> foreach(final Consumer<Path> consumer) {
        return when(path -> true).then(consumer);
    }

    public final Condition when(final Predicate<Path> predicate) {
        return new Condition(predicate);
    }

    public class Condition {
        private final Predicate<Path> predicate;

        private Condition(final Predicate<Path> predicate) {
            this.predicate = predicate;
        }

        public final Map<Path, Exception> then(final Consumer<Path> consumer) {
            return new Consequence(consumer, predicate).through(path).problems;
        }
    }

    private class Consequence {
        private final Consumer<Path> consumer;
        private final Predicate<Path> predicate;
        private final Map<Path, Exception> problems = new TreeMap<>();

        private Consequence(final Consumer<Path> consumer, final Predicate<Path> predicate) {
            this.consumer = consumer;
            this.predicate = predicate;
        }

        private Consequence through(final Path path) {
            if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                try (final DirectoryStream<Path> paths = Files.newDirectoryStream(path)) {
                    through(paths);
                } catch (IOException e) {
                    problems.put(path, e);
                }
            }
            if (predicate.test(path)) {
                consumer.accept(path);
            }
            return this;
        }

        private void through(final DirectoryStream<Path> paths) {
            for (final Path path : paths) {
                through(path);
            }
        }
    }
}

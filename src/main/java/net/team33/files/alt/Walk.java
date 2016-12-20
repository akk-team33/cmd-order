package net.team33.files.alt;

import com.google.common.collect.ImmutableList;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class Walk {

    private static final Predicate TRUE = path -> true;
    private static final ExceptionHandler FAST_FAIL = (path, caught) -> {
        throw new IllegalArgumentException("Problem on <" + path + ">", caught);
    };

    private final Path path;
    private final Predicate intoDirectory;
    private final LinkOption[] options;

    private Walk(final Path path, final Predicate intoDirectory, final LinkOption[] options) {
        this.path = path;
        this.intoDirectory = intoDirectory;
        this.options = options;
    }

    public static Walk through(final Path path) {
        return through(path, TRUE, LinkOption.NOFOLLOW_LINKS);
    }

    public static Walk through(final Path path, final Predicate intoDirectory) {
        return through(path, intoDirectory, LinkOption.NOFOLLOW_LINKS);
    }

    public static Walk through(final Path path, final LinkOption... options) {
        return through(path, TRUE, options);
    }

    public static Walk through(final Path path, final Predicate intoDirectory, final LinkOption... options) {
        return new Walk(path, intoDirectory, options);
    }

    public final void foreach(final Consumer consumer) {
        foreach(consumer, FAST_FAIL);
    }

    public final void foreach(final Consumer consumer, final ExceptionHandler handler) {
        new Action(Collections.singletonList(consumer), handler, intoDirectory, options).walk(path);
    }

    @FunctionalInterface
    public interface Consumer {
        void accept(Path path) throws Exception;
    }

    @FunctionalInterface
    public interface Predicate {
        boolean test(Path path) throws Exception;
    }

    @FunctionalInterface
    public interface ExceptionHandler {
        void accept(Path path, Exception caught);
    }

    private class Action {

        private final List<Consumer> consumers;
        private final ExceptionHandler handler;

        private Action(final List<Consumer> consumers, final ExceptionHandler handler,
                       final Predicate recursive, final LinkOption... options) {
            this.handler = handler;
            this.consumers = ImmutableList.<Consumer>builder()
                    .add(path -> {
                        if (Files.isDirectory(path, options) && recursive.test(path)) {
                            try (final DirectoryStream<Path> paths = Files.newDirectoryStream(path)) {
                                walk(paths);
                            }
                        }
                    })
                    .addAll(consumers)
                    .build();
        }

        private void walk(final DirectoryStream<Path> paths) {
            for (final Path path : paths) {
                walk(path);
            }
        }

        private void walk(final Path path) {
            for (final Consumer consumer : consumers) {
                try {
                    consumer.accept(path);
                } catch (final Exception caught) {
                    handler.accept(path, caught);
                }
            }
        }
    }
}

package net.team33.files;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableList;

public class Walk {

    private final Path path;
    private final List<Consumer> consumers = new LinkedList<>();

    private Walk(final Path path) {
        this.path = path;
    }

    public static Walk through(final Path path) {
        return new Walk(path);
    }

    public final Condition when(final Predicate<Path> predicate) {
        return new Condition(predicate);
    }

    public final Walk doing(final Consumer consumer) {
        consumers.add(consumer);
        return this;
    }

    public final void go(final BiConsumer<Path, Exception> catching) {
        new Run(consumers, catching).run(path);
    }

    @FunctionalInterface
    public interface Consumer {
        void accept(Path path) throws Exception;
    }

    public class Condition {
        private final Predicate<Path> predicate;

        private Condition(final Predicate<Path> predicate) {
            this.predicate = predicate;
        }

        public final Walk then(final Consumer consumer) {
            return doing(path -> {
                if (predicate.test(path)) {
                    consumer.accept(path);
                }
            });
        }
    }

    private class Run {
        private final List<Consumer> consumers;
        private final BiConsumer<Path, Exception> catching;

        private Run(final List<Consumer> consumers, final BiConsumer<Path, Exception> catching) {
            final ArrayList<Consumer> backing = new ArrayList<Consumer>(singleton(path -> {
                if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                    try (final DirectoryStream<Path> paths = Files.newDirectoryStream(path)) {
                        run(paths);
                    }
                }
            }));
            backing.addAll(consumers);
            this.consumers = unmodifiableList(backing);
            this.catching = catching;
        }

        private void run(final DirectoryStream<Path> paths) {
            for (final Path entry : paths) {
                run(entry);
            }
        }

        private void run(final Path path) {
            for (final Consumer consumer : consumers) {
                try {
                    consumer.accept(path);
                } catch (final Exception caught) {
                    catching.accept(path, caught);
                }
            }
        }
    }
}

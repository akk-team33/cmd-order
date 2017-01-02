package net.team33.files;

import com.google.common.collect.ImmutableList;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class Walk {

    public static Through through(final Path path) {
        return new Through(path);
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

    public static class Through {
        private final Path path;

        private Through(final Path path) {
            this.path = path;
        }

        public final When when(final Predicate predicate) {
            return recursive(path -> true).when(predicate);
        }

        public final Apply apply(final Consumer consumer) {
            return recursive(path -> true).apply(consumer);
        }

        public final Recursive recursive(final Predicate predicate) {
            return options(LinkOption.NOFOLLOW_LINKS).recursive(predicate);
        }

        public final Options options(final LinkOption... options) {
            return new Options(path, options);
        }
    }

    public static class Options {
        private final Path path;
        private final LinkOption[] options;

        private Options(final Path path, final LinkOption[] options) {
            this.path = path;
            this.options = options;
        }

        public final Recursive recursive(final Predicate predicate) {
            return new Recursive(this, predicate);
        }
    }

    public static class Recursive {
        private final Options options;
        private final Predicate predicate;

        private Recursive(final Options options, final Predicate predicate) {
            this.options = options;
            this.predicate = predicate;
        }

        public final Apply apply(final Consumer consumer) {
            return new Apply(this).apply(consumer);
        }

        public When when(final Predicate predicate) {
            return new When(new Apply(this), predicate);
        }
    }

    public static class When {
        private final Apply apply;
        private final Predicate predicate;

        private When(final Apply apply, final Predicate predicate) {
            this.apply = apply;
            this.predicate = predicate;
        }

        public final Apply apply(final Consumer consumer) {
            return apply.apply(path -> {
                if (predicate.test(path)) {
                    consumer.accept(path);
                }
            });
        }
    }

    public static class Apply {
        private final Recursive recursive;
        private final List<Consumer> consumers = new LinkedList<>();

        public Apply(final Recursive recursive) {
            this.recursive = recursive;
        }

        public final When when(final Predicate predicate) {
            return recursive.when(predicate);
        }

        public final Apply apply(final Consumer consumer) {
            this.consumers.add(consumer);
            return this;
        }

        public final void catching(final ExceptionHandler handler) {
            new Action(consumers, handler, recursive.predicate, recursive.options.options)
                    .walk(recursive.options.path);
        }

        public final void go() {
            catching((path, caught) -> {
                throw new IllegalArgumentException("Problem in <" + path + ">", caught);
            });
        }
    }

    private static class Action {

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

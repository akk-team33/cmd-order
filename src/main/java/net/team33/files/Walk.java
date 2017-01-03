package net.team33.files;

import com.google.common.collect.ImmutableList;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Walk implements Runnable {

    private static final ExceptionHandler DEFAULT_HANDLER = (path, caught) -> {
        throw new IllegalArgumentException("Problem in <" + path + ">", caught);
    };
    private static final Predicate DEFAULT_PREDICATE = path -> true;

    private final Catching catching;
    private final List<Consumer> consumers = new LinkedList<>();

    private Walk(final Catching catching) {
        this.catching = catching;
    }

    public static Through through(final Path path) {
        return new Through(path);
    }

    public final Walk apply(final Consumer consumer) {
        consumers.add(consumer);
        return this;
    }

    public final When when(final Predicate predicate) {
        return new When(this, predicate);
    }

    @Override
    public final void run() {
        new Action(consumers, catching.handler, catching.recursion.predicate, catching.recursion.options.options)
                .walk(catching.recursion.options.path);
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

        public final Options options(final LinkOption... options) {
            return new Options(this, options);
        }

        public final Recursion recursive(final Predicate predicate) {
            return options(LinkOption.NOFOLLOW_LINKS).recursive(predicate);
        }

        public final Catching catching(final ExceptionHandler handler) {
            return recursive(DEFAULT_PREDICATE).catching(handler);
        }

        public final When when(final Predicate predicate) {
            return catching(DEFAULT_HANDLER).when(predicate);
        }

        public final Walk apply(final Consumer consumer) {
            return catching(DEFAULT_HANDLER).apply(consumer);
        }

        public final <C extends Collection<? super Path>> C to(final C target) {
            return catching(DEFAULT_HANDLER).to(target);
        }
    }

    public static class Options {
        private final Path path;
        private final LinkOption[] options;

        private Options(final Through through, final LinkOption[] options) {
            this.path = through.path;
            this.options = options;
        }

        public final Recursion recursive(final Predicate predicate) {
            return new Recursion(this, predicate);
        }

        public final Catching catching(final ExceptionHandler handler) {
            return recursive(DEFAULT_PREDICATE).catching(handler);
        }

        public final When when(final Predicate predicate) {
            return catching(DEFAULT_HANDLER).when(predicate);
        }

        public final Walk apply(final Consumer consumer) {
            return catching(DEFAULT_HANDLER).apply(consumer);
        }

        public final <C extends Collection<? super Path>> C to(final C target) {
            return catching(DEFAULT_HANDLER).to(target);
        }
    }

    public static class Recursion {
        private final Options options;
        private final Predicate predicate;

        private Recursion(final Options options, final Predicate predicate) {
            this.options = options;
            this.predicate = predicate;
        }

        public final Catching catching(final ExceptionHandler handler) {
            return new Catching(this, handler);
        }

        public final When when(final Predicate predicate) {
            return catching(DEFAULT_HANDLER).when(predicate);
        }

        public final Walk apply(final Consumer consumer) {
            return catching(DEFAULT_HANDLER).apply(consumer);
        }

        public final <C extends Collection<? super Path>> C to(final C target) {
            return catching(DEFAULT_HANDLER).to(target);
        }
    }

    public static class Catching {
        private final Recursion recursion;
        private final ExceptionHandler handler;

        private Catching(final Recursion recursion, final ExceptionHandler handler) {
            this.recursion = recursion;
            this.handler = handler;
        }

        public final Walk apply(final Consumer consumer) {
            return new Walk(this).apply(consumer);
        }

        public final When when(final Predicate predicate) {
            return new When(new Walk(this), predicate);
        }

        public final <C extends Collection<? super Path>> C to(final C target) {
            apply(target::add).run();
            return target;
        }
    }

    public static class When {
        private final Walk walk;
        private final Predicate predicate;

        private When(final Walk walk, final Predicate predicate) {
            this.walk = walk;
            this.predicate = predicate;
        }

        public Walk apply(final Consumer consumer) {
            return walk.apply(path -> {
                if (predicate.test(path)) {
                    consumer.accept(path);
                }
            });
        }

        public final <C extends Collection<? super Path>> C to(final C target) {
            apply(target::add).run();
            return target;
        }
    }

    private static class Action {
        private final List<Consumer> consumers;
        private final ExceptionHandler handler;

        private Action(final List<Consumer> consumers, final ExceptionHandler handler,
                       final Predicate recursive, final LinkOption[] options) {
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

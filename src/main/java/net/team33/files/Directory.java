package net.team33.files;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Predicate;

public class Directory {

    public static Read read(final Path path) {
        return new Read(path);
    }

    public static class Read {
        private final Path path;

        private Read(final Path path) {
            this.path = path;
        }

        public final Recursion recursive(final Predicate<Path> predicate) {
            return options(LinkOption.NOFOLLOW_LINKS).recursive(predicate);
        }

        public final Options options(final LinkOption... options) {
            return new Options(this, options);
        }

        public final When when(final Predicate<Path> predicate) {
            return options(LinkOption.NOFOLLOW_LINKS).when(predicate);
        }

        public final <C extends Collection<? super Path>> C into(final C target) {
            return when(path -> true).into(target);
        }
    }

    public static class Options {
        private final Read read;
        private final LinkOption[] options;

        private Options(final Read read, final LinkOption[] options) {
            this.read = read;
            this.options = options;
        }

        public final Recursion recursive(final Predicate<Path> predicate) {
            return new Recursion(this, predicate);
        }

        public final When when(final Predicate<Path> predicate) {
            return recursive(path -> true).when(predicate);
        }

        public final <C extends Collection<? super Path>> C into(final C target) {
            return when(path -> true).into(target);
        }
    }

    public static class Recursion {
        private final Options options;
        private final Predicate<Path> predicate;

        private Recursion(final Options options, final Predicate<Path> predicate) {
            this.options = options;
            this.predicate = predicate;
        }

        public final When when(final Predicate<Path> predicate) {
            return new When(this, predicate);
        }

        public final <C extends Collection<? super Path>> C into(final C target) {
            return when(path -> true).into(target);
        }
    }

    public static class When {
        private final Recursion recursion;
        private final Predicate<Path> predicate;

        private When(final Recursion recursion, final Predicate<Path> predicate) {
            this.recursion = recursion;
            this.predicate = predicate;
        }

        public final <C extends Collection<? super Path>> C into(final C target) {
            return new Action<C>(target, recursion.options.options, recursion.predicate, predicate)
                    .read(recursion.options.read.path).target;
        }
    }

    private static class Action<C extends Collection<? super Path>> {
        private final C target;
        private final LinkOption[] options;
        private final Predicate<Path> recursion;
        private final Predicate<Path> selection;

        private Action(final C target, final LinkOption[] options,
                       final Predicate<Path> recursion, final Predicate<Path> selection) {
            this.target = target;
            this.options = options;
            this.recursion = recursion;
            this.selection = selection;
        }

        public final Action<C> read(final Path path) {
            if (Files.isDirectory(path, options) && recursion.test(path)) {
                try (final DirectoryStream<Path> paths = Files.newDirectoryStream(path)) {
                    read(paths);
                } catch (IOException e) {
                    e.printStackTrace(); // TODO!
                }
            }
            if (selection.test(path)) {
                target.add(path);
            }
            return this;
        }

        private void read(final DirectoryStream<Path> paths) {
            for (final Path path : paths) {
                read(path);
            }
        }
    }
}

package net.team33.order;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

public class Main implements Runnable {

    private final LinkOption[] options = new LinkOption[]{NOFOLLOW_LINKS};
    private final Map<Path, Exception> problems = new TreeMap<>();
    private final List<Path> skipped = new LinkedList<>();
    private final List<Path> ignored = new LinkedList<>();

    private final Path targetRoot;
    private final List<Resolver.Element> targetSub;
    private final List<Path> origins;
    private final Movement movement;
    private final Output out;

    public Main(final Output out, final List<String> args) throws InitialisationException {
        try {
            this.targetRoot = normal(args.get(0));
            this.targetSub = Stream.of(args.get(1).split(Pattern.quote("/")))
                    .filter(e -> !"".equals(e))
                    .map(Resolver.Element::valueOf)
                    .collect(Collectors.toList());
            this.origins = args.stream().skip(2).map(Main::normal).collect(Collectors.toList());
            this.movement = new Movement(targetRoot, targetSub, out);
            this.out = out;
        } catch (final Exception ignored) {
            throw new InitialisationException();
        }
    }

    public static void main(final String[] args) {
        final Output out = new Output();
        try {
            new Main(out, Arrays.asList(args)).run();
        } catch (InitialisationException e) {
            if (args.length > 0) {
                out.println("Given Arguments:")
                        .println()
                        .println("    ", Stream.of(args).collect(Collectors.joining(" ")))
                        .println();
            }
            out.println("Required Arguments:")
                    .println()
                    .println("    $target $substructure $origin [$origin [...]]")
                    .println()
                    .println("$target:")
                    .println("    Path to a target root directory")
                    .println()
                    .println("$substructure:")
                    .println(
                            "    the formal target subdirectory structure: a subset of ",
                            Stream.of(Resolver.Element.values())
                                    .map(Resolver.Element::name)
                                    .collect(Collectors.joining("/")),
                            " in arbitrary order where ...")
                    .printEach(
                            Stream.of(Resolver.Element.values()),
                            element -> new Object[]{"    - ", element, ": ", element.description})
                    .println()
                    .println("$origin:")
                    .println("    Path to a file or directory to be ordered (recursively)");
        }
    }

    private static Path normal(final String path) {
        return normal(Paths.get(path));
    }

    private static Path normal(final Path path) {
        return path.toAbsolutePath().normalize();
    }

    @Override
    public void run() {
        proceed(origins.iterator());

        if (0 < skipped.size()) {
            out.println().println("[skipped]");
            skipped.forEach(out::println);
        }

        if (0 < ignored.size()) {
            out.println().println("[ignored]");
            ignored.forEach(out::println);
        }

        if (0 < problems.size()) {
            out.println().println("[problems]");
            problems.entrySet().forEach(entry -> {
                out.println(entry.getKey(), " -> ")
                        .printStackTrace(entry.getValue())
                        .println();
            });
        }
    }

    private void proceed(final Iterator<Path> paths) {
        while (paths.hasNext()) {
            proceed(paths.next());
        }
    }

    private void proceed(final Path path) {
        if (path.equals(targetRoot)) {
            skipped.add(path);
        } else if (Files.isRegularFile(path, options)) {
            move(path);
        } else if (Files.isDirectory(path, options)) {
            recursion(path);
        } else {
            ignored.add(path);
        }
    }

    private void move(final Path path) {
        try {
            movement.accept(path);
        } catch (IOException e) {
            problems.put(path, e);
        }
    }

    private void recursion(final Path path) {
        try (final DirectoryStream<Path> paths = Files.newDirectoryStream(path)) {
            proceed(paths.iterator());
        } catch (IOException e) {
            problems.put(path, e);
        }
        try {
            Files.delete(path);
            out.println("removed: ", path);
        } catch (final IOException ignored) {
        }
    }

    private static class InitialisationException extends Exception {
    }
}

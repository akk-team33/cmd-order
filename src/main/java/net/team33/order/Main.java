package net.team33.order;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

@SuppressWarnings("UseOfSystemOutOrSystemErr")
public final class Main implements Runnable {

    private final LinkOption[] options = new LinkOption[]{NOFOLLOW_LINKS};
    private final Map<Path, Exception> problems = new TreeMap<>();
    private final List<Path> skipped = new LinkedList<>();
    private final List<Path> ignored = new LinkedList<>();
    private final Args args;
    private final Movement movement;

    public Main(final Args args) {
        this.args = args;
        this.movement = new Movement(args.target, null);
    }

    public static void main(final String[] args) {
        try {
            new Main(new Args(args)).run();
        } catch (Exception e) {
            System.out.println("expected arguments: TARGET PATH_ELEMENTS SOURCE1 [SOURCE2 [...]]");
            System.out.println();
            System.out.println("where PATH_ELEMENTS: Y/M/D/X");
            System.out.println();
            System.out.print("given arguments: ");
            System.out.println(Arrays.asList(args));
            System.out.println();
            System.out.print("problem: ");
            e.printStackTrace(System.out);
        }
    }

    @Override
    public final void run() {
        proceed(args.sources.iterator());

        if (0 < skipped.size()) {
            System.out.println();
            System.out.println("[skipped]");
            skipped.forEach(System.out::println);
        }

        if (0 < ignored.size()) {
            System.out.println();
            System.out.println("[ignored]");
            ignored.forEach(System.out::println);
        }

        if (0 < problems.size()) {
            System.out.println();
            System.out.println("[problems]");
            problems.entrySet().forEach(entry -> {
                System.out.print(entry.getKey());
                System.out.println(" -> ");
                entry.getValue().printStackTrace(System.out);
                System.out.println();
            });
        }
    }

    private void proceed(final Iterator<Path> iterator) {
        while (iterator.hasNext()) {
            proceed(iterator.next());
        }
    }

    private void proceed(final Path path) {
        System.out.println(path);
        if (path.equals(args.target)) {
            skipped.add(path);
        } else if (Files.isRegularFile(path, options)) {
            movement(path);
        } else if (Files.isDirectory(path, options)) {
            recursion(path);
        } else {
            ignored.add(path);
        }
    }

    private void movement(final Path path) {
        try {
            movement.accept(path);
        } catch (final IOException caught) {
            problems.put(path, caught);
        }
    }

    private void recursion(final Path path) {
        try {
            try (final DirectoryStream<Path> paths = Files.newDirectoryStream(path)) {
                proceed(paths.iterator());
            }
            Files.delete(path);
        } catch (final IOException ignored) {
        }
    }
}

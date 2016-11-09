package net.team33.order;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;

@SuppressWarnings("UseOfSystemOutOrSystemErr")
public final class Main {

    public static void main(final String[] args) {
        proceed(new Args(args));
    }

    private static void proceed(final Args args) {
        if (args.isScheduled()) {
            throw new UnsupportedOperationException("not yet implemented");
        } else {
            order(args.target, args.pattern, args.sources);
        }
    }

    private static void order(final Path target, final Pattern pattern, final Iterable<Path> sources) {
        for (final Path source : sources) {
            try {
                order(target, pattern, source);
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        }
    }

    private static void order(final Path target, final Pattern pattern, final Path source) throws IOException {
        System.out.print(source);
        System.out.print(" ");
        if (Files.isDirectory(source, LinkOption.NOFOLLOW_LINKS)) {
            orderDir(target, pattern, source);
        } else {
            orderRegular(target, pattern, source);
        }
    }

    private static void orderRegular(final Path target, final Pattern pattern, final Path source) throws IOException {
        final FileTime time = Files.getLastModifiedTime(source, LinkOption.NOFOLLOW_LINKS);
        final Path resolved = target.resolve(pattern.resolve(time, source.getFileName().toString()));
        System.out.print("-> ");
        System.out.print(resolved);
        Files.move(source, resolved, StandardCopyOption.COPY_ATTRIBUTES);
        System.out.println(" OK");
    }

    private static void orderDir(final Path target, final Pattern pattern, final Path source) throws IOException {
        try (final DirectoryStream<Path> paths = Files.newDirectoryStream(source)) {
            System.out.println("...");
            order(target, pattern, paths);
        }
    }
}

package net.team33.order;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.function.Consumer;

public class Movement implements Consumer<Path> {

    private final Resolver resolver;

    public Movement(final Path targetRoot) {
        this.resolver = new Resolver(targetRoot);
    }

    @Override
    public void accept(final Path path) {
        try {
            final FileTime lastModifiedTime = Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS);
            final Path target = resolver.apply(lastModifiedTime, path.getFileName().toString());
            Files.createDirectories(target.getParent());
            Files.move(path, target);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

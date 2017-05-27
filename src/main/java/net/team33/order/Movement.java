package net.team33.order;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;

public class Movement {

    private final Path targetRoot;
    private final Solver resolver;

    public Movement(final Path targetRoot, final List<Solver.Element> elements) {
        this.targetRoot = targetRoot;
        this.resolver = new Solver(elements);
    }

    public void accept(final Path path) throws IOException {
        final FileTime lastModifiedTime = Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS);
        final Path target = targetRoot.resolve(resolver.apply(path.getFileName().toString(), lastModifiedTime));
        Files.createDirectories(target.getParent());
        Files.move(path, target);
    }
}

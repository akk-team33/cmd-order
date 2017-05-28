package net.team33.order;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class Movement {

    private final Path targetRoot;
    private final Resolver resolver;

    public Movement(final Path targetRoot, final List<Resolver.Element> elements) {
        this.targetRoot = targetRoot;
        this.resolver = new Resolver(elements);
    }

    public void accept(final Path path) throws IOException {
        final Function<Integer, String> resolving = resolver.apply(new FileName(path));
        final Path target = newTarget(resolving);
        Files.createDirectories(target.getParent());
        Files.move(path, target);
    }

    private Path newTarget(final Function<Integer, String> resolving) {
        Path target = targetRoot.resolve(resolving.apply(0));
        for (int index = 1; Files.exists(target, LinkOption.NOFOLLOW_LINKS); ++index) {
            target = targetRoot.resolve(resolving.apply(index));
        }
        return target;
    }
}

package net.team33.order;

import net.team33.order.Resolver.PathElement;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

class Args {

    final Path target;
    final List<Path> sources;
    final List<PathElement> pathElements;

    Args(final String[] args) {
        if (1 > args.length) {
            throw new IllegalArgumentException("missing: TARGET");
        }
        if (2 > args.length) {
            throw new IllegalArgumentException("missing: PATH_ELEMENTS");
        }
        if (3 > args.length) {
            throw new IllegalArgumentException("missing: SOURCE(s)");
        }
        target = Paths.get(args[0]).toAbsolutePath().normalize();
        pathElements = Stream.of(args[1].split(Pattern.quote("/")))
                .map(PathElement::from)
                .collect(toList());
        sources = Stream.of(args).skip(2)
                .map(path -> Paths.get(path).toAbsolutePath().normalize())
                .collect(toList());
    }
}

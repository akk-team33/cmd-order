package net.team33.order;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Args {

    Path target;
    List<Path> sources;

    Args(final String[] args) {
        target = Paths.get(args[0]).toAbsolutePath().normalize();
        sources = Stream.of(args).skip(1)
                .map(path -> Paths.get(path).toAbsolutePath().normalize())
                .collect(Collectors.toList());
    }

    final boolean isScheduled() {
        return false;
    }
}

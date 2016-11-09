package net.team33.order;

import java.nio.file.Path;
import java.util.List;

class Args {

    Path target;
    Pattern pattern;
    List<Path> sources;

    Args(final String[] args) {
    }

    boolean isScheduled() {
        throw new UnsupportedOperationException("not yet implemented");
    }
}

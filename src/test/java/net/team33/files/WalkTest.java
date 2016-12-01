package net.team33.files;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class WalkTest {

    private static final Set<Path> EXPECTED = new TreeSet<>(Arrays.asList(
            Paths.get("src", "main", "java"),
            Paths.get("src", "main", "java", "net"),
            Paths.get("src", "main", "java", "net", "team33"),
            Paths.get("src", "main", "java", "net", "team33", "files"),
            Paths.get("src", "main", "java", "net", "team33", "files", "Walk.java"),
            Paths.get("src", "main", "java", "net", "team33", "order"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Args.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Main.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Movement.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Pattern.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Resolver.java")
    ));
    private static final Set<Path> REGULAR = new TreeSet<>(Arrays.asList(
            Paths.get("src", "main", "java", "net", "team33", "files", "Walk.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Args.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Main.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Movement.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Pattern.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Resolver.java")
    ));

    @Test
    public void throughAll() throws Exception {
        final Set<Path> result = new TreeSet<>();
        final Map<Path, Exception> problems = Walk.through(Paths.get("src", "main", "java"))
                .foreach(path -> result.add(path));
        Assert.assertEquals(EXPECTED, result);
        Assert.assertEquals(Collections.emptyMap(), problems);
    }

    @Test
    public void throughRegular() throws Exception {
        final Set<Path> regular = new TreeSet<>();
        final Map<Path, Exception> result = Walk.through(Paths.get("src", "main", "java"))
                .when(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
                .then(path -> regular.add(path));
        Assert.assertEquals(REGULAR, regular);
        Assert.assertEquals(Collections.emptyMap(), result);
    }
}
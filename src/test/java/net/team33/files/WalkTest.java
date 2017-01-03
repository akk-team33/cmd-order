package net.team33.files;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class WalkTest {

    public static final Walk.Predicate IS_REGULAR_FILE = path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS);
    public static final Walk.Predicate IS_DIRECTORY = path -> Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS);

    private static final List<Path> ALL_REGULAR_FILES = Arrays.asList(
            Paths.get("src", "main", "java", "net", "team33", "files", "Walk.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Args.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Main.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Movement.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Pattern.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Resolver.java")
    );

    private static final List<Path> SOME_REGULAR_FILES = Arrays.asList(
            Paths.get("src", "main", "java", "net", "team33", "order", "Args.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Main.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Movement.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Pattern.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Resolver.java")
    );

    private static final List<Path> ALL_FILES = Arrays.asList(
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
    );

    private static final List<Path> SOME_FILES = Arrays.asList(
            Paths.get("src", "main", "java"),
            Paths.get("src", "main", "java", "net"),
            Paths.get("src", "main", "java", "net", "team33"),
            Paths.get("src", "main", "java", "net", "team33", "files"),
            Paths.get("src", "main", "java", "net", "team33", "order"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Args.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Main.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Movement.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Pattern.java"),
            Paths.get("src", "main", "java", "net", "team33", "order", "Resolver.java")
    );

    private static final List<Path> DIRECTORIES = Arrays.asList(
            Paths.get("src", "main", "java"),
            Paths.get("src", "main", "java", "net"),
            Paths.get("src", "main", "java", "net", "team33"),
            Paths.get("src", "main", "java", "net", "team33", "files"),
            Paths.get("src", "main", "java", "net", "team33", "order")
    );

    @Test
    public void through_options_recursive_catching_apply_apply_run() throws Exception {
        final Set<Path> result = new TreeSet<>();
        final Set<Path> additional = new TreeSet<>();
        final Map<Path, Exception> problems = new TreeMap<>();
        Walk
                .through(Paths.get("src", "main", "java"))
                .options(LinkOption.NOFOLLOW_LINKS)
                .recursive(path -> !path.endsWith(Paths.get("files")))
                .catching(problems::put)
                .apply(result::add)
                .apply(additional::add)
                .run();
        Assert.assertEquals(SOME_FILES, new ArrayList<>(result));
        Assert.assertEquals(SOME_FILES, new ArrayList<>(additional));
        Assert.assertEquals(Collections.emptyMap(), problems);
    }

    @Test
    public void through_options_recursive_catching_when_apply_when_apply_run() throws Exception {
        final Set<Path> regular = new TreeSet<>();
        final Set<Path> directories = new TreeSet<>();
        final Map<Path, Exception> problems = new TreeMap<>();
        Walk
                .through(Paths.get("src", "main", "java"))
                .options(LinkOption.NOFOLLOW_LINKS)
                .recursive(path -> !path.endsWith(Paths.get("files")))
                .catching(problems::put)
                .when(IS_REGULAR_FILE)
                .apply(regular::add)
                .when(IS_DIRECTORY)
                .apply(directories::add)
                .run();
        Assert.assertEquals(SOME_REGULAR_FILES, new ArrayList<>(regular));
        Assert.assertEquals(DIRECTORIES, new ArrayList<>(directories));
        Assert.assertEquals(Collections.emptyMap(), problems);
    }

    @Test
    public void through_recursive_catching_apply_when_apply_run() throws Exception {
        final Set<Path> result = new TreeSet<>();
        final Set<Path> directories = new TreeSet<>();
        final Map<Path, Exception> problems = new TreeMap<>();
        Walk
                .through(Paths.get("src", "main", "java"))
                .recursive(path -> !path.endsWith(Paths.get("files")))
                .catching(problems::put)
                .apply(result::add)
                .when(IS_DIRECTORY)
                .apply(directories::add)
                .run();
        Assert.assertEquals(SOME_FILES, new ArrayList<>(result));
        Assert.assertEquals(DIRECTORIES, new ArrayList<>(directories));
        Assert.assertEquals(Collections.emptyMap(), problems);
    }

    @Test
    public void through_catching_when_apply_apply_run() throws Exception {
        final Set<Path> regular = new TreeSet<>();
        final Set<Path> all = new TreeSet<>();
        final Map<Path, Exception> problems = new TreeMap<>();
        Walk
                .through(Paths.get("src", "main", "java"))
                .catching(problems::put)
                .when(IS_REGULAR_FILE)
                .apply(regular::add)
                .apply(all::add)
                .run();
        Assert.assertEquals(ALL_REGULAR_FILES, new ArrayList<>(regular));
        Assert.assertEquals(ALL_FILES, new ArrayList<>(all));
        Assert.assertEquals(Collections.emptyMap(), problems);
    }

    @Test
    public void through_when_apply_run() throws Exception {
        final Set<Path> result = new TreeSet<>();
        Walk
                .through(Paths.get("src", "main", "java"))
                .when(IS_REGULAR_FILE)
                .apply(result::add)
                .run();
        Assert.assertEquals(ALL_REGULAR_FILES, new ArrayList<>(result));
    }

    @Test
    public void through_apply_run() throws Exception {
        final Set<Path> result = new TreeSet<>();
        Walk
                .through(Paths.get("src", "main", "java"))
                .apply(result::add)
                .run();
        Assert.assertEquals(ALL_FILES, new ArrayList<>(result));
    }

    @Test
    public void through_options_catching_when_apply_run() throws Exception {
        final Set<Path> regular = new TreeSet<>();
        final Map<Path, Exception> problems = new TreeMap<>();
        Walk
                .through(Paths.get("src", "main", "java"))
                .options(LinkOption.NOFOLLOW_LINKS)
                .catching(problems::put)
                .when(IS_REGULAR_FILE)
                .apply(regular::add)
                .run();
        Assert.assertEquals(ALL_REGULAR_FILES, new ArrayList<>(regular));
        Assert.assertEquals(Collections.emptyMap(), problems);
    }

    @Test
    public void through_options_when_apply_run() throws Exception {
        final Set<Path> regular = new TreeSet<>();
        Walk
                .through(Paths.get("src", "main", "java"))
                .options(LinkOption.NOFOLLOW_LINKS)
                .when(IS_REGULAR_FILE)
                .apply(regular::add)
                .run();
        Assert.assertEquals(ALL_REGULAR_FILES, new ArrayList<>(regular));
    }

    @Test
    public void through_options_apply_run() throws Exception {
        final Set<Path> regular = new TreeSet<>();
        Walk
                .through(Paths.get("src", "main", "java"))
                .options(LinkOption.NOFOLLOW_LINKS)
                .apply(regular::add)
                .run();
        Assert.assertEquals(ALL_FILES, new ArrayList<>(regular));
    }

    @Test
    public void through_options_recursive_when_apply_run() throws Exception {
        final Set<Path> directories = new TreeSet<>();
        Walk
                .through(Paths.get("src", "main", "java"))
                .options(LinkOption.NOFOLLOW_LINKS)
                .recursive(path -> !path.endsWith(Paths.get("files")))
                .when(IS_DIRECTORY)
                .apply(directories::add)
                .run();
        Assert.assertEquals(DIRECTORIES, new ArrayList<>(directories));
    }

    @Test
    public void through_options_recursive_apply_run() throws Exception {
        final Set<Path> directories = new TreeSet<>();
        Walk
                .through(Paths.get("src", "main", "java"))
                .options(LinkOption.NOFOLLOW_LINKS)
                .recursive(path -> !path.endsWith(Paths.get("files")))
                .apply(directories::add)
                .run();
        Assert.assertEquals(SOME_FILES, new ArrayList<>(directories));
    }

    @Test
    public void through_options_recursive_catching_when_to() throws Exception {
        final Map<Path, Exception> problems = new TreeMap<>();
        final Set<Path> result = Walk
                .through(Paths.get("src", "main", "java"))
                .options(LinkOption.NOFOLLOW_LINKS)
                .recursive(path -> !path.endsWith(Paths.get("files")))
                .catching(problems::put)
                .when(IS_REGULAR_FILE)
                .to(new TreeSet<>());
        Assert.assertEquals(SOME_REGULAR_FILES, new ArrayList<>(result));
        Assert.assertEquals(Collections.emptyMap(), problems);
    }

    @Test
    public void through_options_recursive_catching_to() throws Exception {
        final Map<Path, Exception> problems = new TreeMap<>();
        final Set<Path> result = Walk
                .through(Paths.get("src", "main", "java"))
                .options(LinkOption.NOFOLLOW_LINKS)
                .recursive(path -> !path.endsWith(Paths.get("files")))
                .catching(problems::put)
                .to(new TreeSet<>());
        Assert.assertEquals(SOME_FILES, new ArrayList<>(result));
        Assert.assertEquals(Collections.emptyMap(), problems);
    }

    @Test
    public void through_options_recursive_to() throws Exception {
        final Set<Path> result = Walk
                .through(Paths.get("src", "main", "java"))
                .options(LinkOption.NOFOLLOW_LINKS)
                .recursive(path -> !path.endsWith(Paths.get("files")))
                .to(new TreeSet<>());
        Assert.assertEquals(SOME_FILES, new ArrayList<>(result));
    }

    @Test
    public void through_options_to() throws Exception {
        final Set<Path> result = Walk
                .through(Paths.get("src", "main", "java"))
                .options(LinkOption.NOFOLLOW_LINKS)
                .to(new TreeSet<>());
        Assert.assertEquals(ALL_FILES, new ArrayList<>(result));
    }

    @Test
    public void through_to() throws Exception {
        final Set<Path> result = Walk
                .through(Paths.get("src", "main", "java"))
                .to(new TreeSet<>());
        Assert.assertEquals(ALL_FILES, new ArrayList<>(result));
    }
}
package net.team33.files;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class DirectoryTest {

    private static final List<Path> ALL_REGULAR_FILES = Arrays.asList(
            Paths.get("src", "main", "java", "net", "team33", "files", "Directory.java"),
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
            Paths.get("src", "main", "java", "net", "team33", "files", "Directory.java"),
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

    @Test
    public void read_options_recursive_when_into() throws Exception {
        final List<Path> paths = new ArrayList<>(Directory
                .read(Paths.get("src", "main", "java"))
                .options(LinkOption.NOFOLLOW_LINKS)
                .recursive(path -> !path.endsWith(Paths.get("files")))
                .when(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
                .into(new TreeSet<>()));
        Assert.assertEquals(SOME_REGULAR_FILES, paths);
    }

    @Test
    public void read_recursive_when_into() throws Exception {
        final List<Path> paths = new ArrayList<>(Directory
                .read(Paths.get("src", "main", "java"))
                .recursive(path -> !path.endsWith(Paths.get("files")))
                .when(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
                .into(new TreeSet<>()));
        Assert.assertEquals(SOME_REGULAR_FILES, paths);
    }

    @Test
    public void read_when_into() throws Exception {
        final List<Path> paths = new ArrayList<>(Directory
                .read(Paths.get("src", "main", "java"))
                .when(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
                .into(new TreeSet<>()));
        Assert.assertEquals(ALL_REGULAR_FILES, paths);
    }

    @Test
    public void read_recursive_into() throws Exception {
        final List<Path> paths = new ArrayList<>(Directory
                .read(Paths.get("src", "main", "java"))
                .recursive(path -> !path.endsWith(Paths.get("files")))
                .into(new TreeSet<>()));
        Assert.assertEquals(SOME_FILES, paths);
    }

    @Test
    public void read_into() throws Exception {
        final List<Path> paths = new ArrayList<>(Directory
                .read(Paths.get("src", "main", "java"))
                .into(new TreeSet<>()));
        Assert.assertEquals(ALL_FILES, paths);
    }

    @Test
    public void read_options_when_into() throws Exception {
        final List<Path> paths = new ArrayList<>(Directory
                .read(Paths.get("src", "main", "java"))
                .options(LinkOption.NOFOLLOW_LINKS)
                .when(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
                .into(new TreeSet<>()));
        Assert.assertEquals(ALL_REGULAR_FILES, paths);
    }

    @Test
    public void read_options_recursive_into() throws Exception {
        final List<Path> paths = new ArrayList<>(Directory
                .read(Paths.get("src", "main", "java"))
                .options(LinkOption.NOFOLLOW_LINKS)
                .recursive(path -> !path.endsWith(Paths.get("files")))
                .into(new TreeSet<>()));
        Assert.assertEquals(SOME_FILES, paths);
    }

    @Test
    public void read_options_into() throws Exception {
        final List<Path> paths = new ArrayList<>(Directory
                .read(Paths.get("src", "main", "java"))
                .options(LinkOption.NOFOLLOW_LINKS)
                .into(new TreeSet<>()));
        Assert.assertEquals(ALL_FILES, paths);
    }
}
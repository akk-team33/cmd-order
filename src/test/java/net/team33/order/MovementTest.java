package net.team33.order;

import net.team33.files.Walk;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class MovementTest {

    private static final String FILENAME = "MovementTest.java";

    private Path origin;
    private Path subject;
    private Path targetRoot;
    private Path result;
    private Movement movement;
    private ZonedDateTime dateTime;

    @Before
    public void setUp() throws Exception {
        origin = Paths.get("src", "test", "java", "net", "team33", "order", FILENAME);
        subject = Paths.get("target", "test", "movement", FILENAME);
        targetRoot = Paths.get("target", "test", "movement", "moved");
        result = Paths.get("target", "test", "movement", "moved", "2016", "11", "28", "101343000." + FILENAME);
        movement = new Movement(targetRoot);
        dateTime = ZonedDateTime.of(2016, 11, 28, 10, 13, 43, 1000000, ZoneId.systemDefault());
        Walk.through(subject.getParent())
                .when(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
                .apply(Files::delete)
                .catching((p, e) -> {
                });
        Files.createDirectories(subject.getParent());
        Files.copy(origin, subject, REPLACE_EXISTING, COPY_ATTRIBUTES);
        Files.setLastModifiedTime(subject, FileTime.from(dateTime.toInstant()));
    }

    @Test
    public void accept() throws Exception {
        Assert.assertTrue(Files.exists(subject));
        Assert.assertFalse(Files.exists(result));
        movement.accept(subject);
        Assert.assertTrue(Files.exists(result));
        Assert.assertFalse(Files.exists(subject));
    }
}
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
import static java.util.Arrays.asList;
import static net.team33.order.Resolver.Element.D;
import static net.team33.order.Resolver.Element.M;
import static net.team33.order.Resolver.Element.N;
import static net.team33.order.Resolver.Element.X;
import static net.team33.order.Resolver.Element.Y;

public class MovementTest {

    private static final String FILENAME = "MovementTest.java";

    private Path subject;
    private Path result;
    private Movement movement;

    @Before
    public void setUp() throws Exception {
        final Path origin = Paths.get("src", "test", "java", "net", "team33", "order", FILENAME);
        final Path targetRoot = Paths.get("target", "test", "movement", "moved");
        final ZonedDateTime dateTime = ZonedDateTime.of(2016, 11, 28, 10, 13, 43, 1000000, ZoneId.systemDefault());

        subject = Paths.get("target", "test", "movement", FILENAME);
        result = Paths.get("target", "test", "movement", "moved", "11", "java", "2016", "28", FILENAME);
        movement = new Movement(targetRoot, asList(M, X, Y, D, N));

        Walk.through(subject.getParent())
                .when(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
                .apply(Files::delete)
                .run();

        Files.createDirectories(subject.getParent());
        Files.copy(origin, subject, REPLACE_EXISTING, COPY_ATTRIBUTES);
        Files.setLastModifiedTime(subject, FileTime.from(dateTime.toInstant()));
    }

    @Test
    public void accept() throws Exception {
        Assert.assertTrue(Files.exists(subject));
        Assert.assertFalse(Files.exists(result));
        movement.accept(subject);
        Assert.assertFalse(Files.exists(subject));
        Assert.assertTrue(Files.exists(result));
    }
}
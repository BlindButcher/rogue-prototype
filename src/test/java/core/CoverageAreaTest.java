package core;

import org.junit.Test;
import util.Point;

import java.util.Set;

import static core.CoverageArea.ARC;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static util.Point.point;

/**
 * Tests hit square.
 *
 * @author Blind
 */
public class CoverageAreaTest {
    @Test
    public void testSquares() {
        try {
            ARC.effectedPoints(point(0, 0), point(0, 0));
            fail();
        } catch (Exception e) {
        }

        Set<Point> points = ARC.effectedPoints(point(0, 0), point(0, 1));
        assertEquals(3, points.size());

        for (Point checked : asList(point(0, 1), point(1, 1), point(-1, 1))) {
            assertTrue(points.contains(checked));
        }

        points = ARC.effectedPoints(point(0, 1), point(0, 0));
        assertEquals(3, points.size());

        for (Point checked : asList(point(0, 0), point(1, 0), point(-1, 0))) {
            assertTrue(points.contains(checked));
        }

        points = ARC.effectedPoints(point(0, 0), point(1, 0));
        assertEquals(3, points.size());

        for (Point checked : asList(point(1, 1), point(1, 0), point(1, -1))) {
            assertTrue(points.contains(checked));
        }

        points = ARC.effectedPoints(point(1, 0), point(0, 0));
        assertEquals(3, points.size());

        for (Point checked : asList(point(0, 1), point(0, 0), point(0, -1))) {
            assertTrue(points.contains(checked));
        }

        points = ARC.effectedPoints(point(0, 0), point(1, 1));
        assertEquals(3, points.size());

        for (Point checked : asList(point(0, 1), point(1, 0), point(1, 1))) {
            assertTrue(points.contains(checked));
        }

        points = ARC.effectedPoints(point(0, 0), point(1, -1));
        assertEquals(3, points.size());

        for (Point checked : asList(point(1, -1), point(1, 0), point(0, -1))) {
            assertTrue(points.contains(checked));
        }

        points = ARC.effectedPoints(point(0, 0), point(-1, -1));
        assertEquals(3, points.size());

        for (Point checked : asList(point(-1, -1), point(-1, 0), point(0, -1))) {
            assertTrue(points.contains(checked));
        }

        points = ARC.effectedPoints(point(0, 0), point(-1, 1));
        assertEquals(3, points.size());

        for (Point checked : asList(point(-1, 1), point(-1, 0), point(0, 1))) {
            assertTrue(points.contains(checked));
        }
    }
}

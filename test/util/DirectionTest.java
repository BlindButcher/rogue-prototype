package util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests different direction methods.
 *
 * @author Blind
 */
public class DirectionTest
{

    @Test
    public void testFindDirection()
    {
        assertEquals(Direction.NORTH, Direction.vector(Point.point(0, 0), Point.point(0, 1)));
        assertEquals(Direction.SOUTH, Direction.vector(Point.point(0, 1), Point.point(0, 0)));

        assertEquals(Direction.EAST, Direction.vector(Point.point(0, 0), Point.point(1, 0)));
        assertEquals(Direction.WEST, Direction.vector(Point.point(1, 0), Point.point(0, 0)));

        try {
            assertEquals(Direction.EAST, Direction.vector(Point.point(0, 0), Point.point(2, 0)));
            fail();
        } catch (Exception e)
        {

        }
    }
}

package util;

import creature.Creature;
import dungeon.DungeonService;
import main.AbstractInjectionTest;
import main.LevelContext;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * PathFinder test.
 *
 * @author Blind
 */
public class PathFinderTest extends AbstractInjectionTest
{
    @Test
    public void testSimple()
    {
        Creature c = new Creature(getDummy());
        LevelContext level = new LevelContext(DungeonService.TEST_MAP, 0);
        c.register(level, Point.point(0, 0), 0);
        PathFinder pathFinder = new PathFinder();
        assertEquals(2, pathFinder.findPath(level, c, Point.point(0, 0), Point.point(2, 2), 10).size());
    }
}

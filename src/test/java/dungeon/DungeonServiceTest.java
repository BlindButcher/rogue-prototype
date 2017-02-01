package dungeon;

import main.AbstractInjectionTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests dungeon service.
 *
 * @author Blind
 */
public class DungeonServiceTest extends AbstractInjectionTest
{
    @Before
    public void init()
    {
        super.init();
    }

    @Test
    public void generateDungeon()
    {
        DungeonService service = injector.getInstance(DungeonService.class);
        service.createDungeon("BFS", 25, 25);
    }
}

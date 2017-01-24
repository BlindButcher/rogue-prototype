package dungeon;

import main.AbstractInjectionTest;
import main.LevelContext;
import org.junit.Test;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test Scenario.
 *
 * @author Blind
 */
public class ScenarioTest extends AbstractInjectionTest
{
    @Test
    public void testLoad()
    {
        Scenario scenario = injector.getInstance(Scenario.class);
        assertFalse(scenario.getLevelMap().isEmpty());
        assertFalse(scenario.checkWin(Collections.<LevelContext>emptyList()));
        assertFalse(scenario.getReservedBosses().isEmpty());
        assertFalse(scenario.getGeneratorList().isEmpty());
        LevelContext context = scenario.createLevel(scenario.maxDepth());
        assertFalse(scenario.checkWin(asList(context)));
        scenario.getBossUsed().add(scenario.getWinCondition().id);
        assertFalse(scenario.checkWin(asList(context)));
        context.getCreatures().clear();
        assertTrue(scenario.checkWin(asList(context)));
    }
}

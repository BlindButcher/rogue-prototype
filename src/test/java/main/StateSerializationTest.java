package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import creature.Creature;
import creature.CreatureGeneratorService;
import creature.CreatureTemplate;
import dungeon.DungeonService;
import inventory.ItemFactory;
import loader.EntityLoader;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Predicate;

import static util.Point.point;

/**
 * @author BlindButcher
 * @since 2/1/17
 */
public class StateSerializationTest extends AbstractInjectionTest {
    private EntityLoader entityLoader;
    private CreatureGeneratorService generatorService;
    private static final Predicate<CreatureTemplate> predicate = creatureTemplate -> !creatureTemplate.isBoss();

    @Before
    public void init() {
        super.init();
        entityLoader = injector.getInstance(EntityLoader.class);
        generatorService = injector.getInstance(CreatureGeneratorService.class);
    }


    @Test
    public void testSampleSerialization() throws Exception {
        WorldState worldState = new WorldState(injector.getInstance(EventHolder.class));
        LevelContext level = new LevelContext(DungeonService.TEST_MAP, 0);
        worldState.currentLevel = level;
        CreatureTemplate first =  entityLoader.templates().stream().filter(predicate).skip(1L).findFirst().get();
        CreatureTemplate second = entityLoader.templates().stream().filter(predicate).findFirst().get();

        Creature c1 = generatorService.generateCreature(first, level, point(0, 0));
        c1.setEventHolder(injector.getInstance(EventHolder.class));
        Creature c2 = generatorService.generateCreature(second, level, point(4, 4));
        c2.setEventHolder(injector.getInstance(EventHolder.class));

        ObjectMapper mapper = new ObjectMapper();

        System.out.println(mapper.writeValueAsString(worldState));
    }
}

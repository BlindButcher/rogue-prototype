package dungeon;


import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import dungeon.generator.*;
import loader.EntityLoader;
import util.Utils;

import java.util.List;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.tryFind;
import static dungeon.Cell.BLANK;
import static java.lang.String.format;
import static java.util.Arrays.asList;

/**
 * Service, which is responsible for dungeon generation. Use different methods for different level generation
 * strategies.
 *
 * @author Blind
 */
public class DungeonService implements IDungeonService
{
    private final List<? extends DungeonGenerator> GENERATORS =
            asList(new RoomBasedGenerator(), new BackTrackGenerator(), new SpiralGenerator(),
                    new BfsGenerator(), new PredefinedMapGenerator());

    public static final Cells TEST_MAP = new Cells(new Cell[][] {{BLANK, BLANK, BLANK, BLANK, BLANK},
            {BLANK, BLANK, BLANK, BLANK, BLANK},
            {BLANK, BLANK, BLANK, BLANK, BLANK},
            {BLANK, BLANK, BLANK, BLANK, BLANK},
            {BLANK, BLANK, BLANK, BLANK, BLANK}}, "TEST");

    private GenerationStrategy strategy = GenerationStrategy.RANDOM;
    private EntityLoader entityLoader;

    public Cells createDungeon(int row, int column) {
       switch (strategy)
       {
           case RANDOM:
               return random(row, column);
           case TEST:
               return TEST_MAP;
           default: throw new IllegalArgumentException("No handler for " + strategy);
       }
    }

    @Override
    public Cells createDungeon(final String generatorId, int row, int column)
    {
        Optional<? extends DungeonGenerator> result = tryFind(GENERATORS, new Predicate<DungeonGenerator>()
        {
            public boolean apply(DungeonGenerator dungeonGenerator)
            {
                return dungeonGenerator.getId().equals(generatorId);
            }
        });
        try
        {
            return appendStairs(result.isPresent() ? result.get().generate(row, column) : find(entityLoader.mapBase(),
                    new Predicate<Cells>()
                    {
                        public boolean apply(Cells cells)
                        {
                            return cells.getId().equals(generatorId);
                        }
                    }));
        } catch (Exception e)
        {
            throw new RuntimeException(format("Map with id: %s not found", generatorId), e);
        }
    }

    private Cells appendStairs(Cells dungeon)
    {
        if (!(dungeon.hasCellType(Cell.DOWN_STAIRS))) putObject(dungeon, Cell.DOWN_STAIRS, 1);
        if (!(dungeon.hasCellType(Cell.UP_STAIRS))) putObject(dungeon, Cell.UP_STAIRS, 1);

        return dungeon;
    }

    private Cells random(int row, int column)
    {
        int index = Utils.roll(GENERATORS.size());
        Cells dungeon = GENERATORS.get(index).generate(row, column);

        return appendStairs(dungeon);
    }

    private void putObject(Cells map, Cell cell, int count) {
        for (int i = 0; i < count; i++) {
            int[] randomCell = map.getRandomPassableCell();
            map.setCell(randomCell[0], randomCell[1], cell);
        }
    }

    @Inject
    public DungeonService(@Named("dungeonGenerationStrategy") GenerationStrategy strategy)
    {
        this.strategy = strategy;
    }

    @Inject
    public void setEntityLoader(EntityLoader entityLoader)
    {
        this.entityLoader = entityLoader;
    }

    private class PredefinedMapGenerator implements DungeonGenerator
    {
        public Cells generate(int row, int column)
        {
            int rnd = Utils.roll(entityLoader.mapBase().size());

            int i = 0;
            for (Cells cells : entityLoader.mapBase())
            {
                if (rnd == i) return cells;
                i++;
            }

            throw new RuntimeException("This should not happen");
        }

        @Override
        public String getId()
        {
            return "PREDEFINED";
        }
    }
}

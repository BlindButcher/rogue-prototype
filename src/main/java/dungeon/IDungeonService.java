package dungeon;

/**
 * Contains methods for dungeon generation.
 *
 * @author Blind
 */
public interface IDungeonService
{
    Cells createDungeon(int row, int column);
    public Cells createDungeon(final String generatorId, int row, int column);
}

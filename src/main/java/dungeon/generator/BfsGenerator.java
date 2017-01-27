package dungeon.generator;


import com.google.common.base.Preconditions;
import dungeon.Cell;
import dungeon.Cells;
import dungeon.DungeonUtils;
import util.Direction;
import util.Point;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static dungeon.DungeonUtils.fillWith;
import static java.lang.String.format;

/**
 * Creates dungeon base on BFS algorithm.
 *
 * @author Blind
 */
public class BfsGenerator implements DungeonGenerator
{

    public Cells generate(int row, int column)
    {
        Preconditions.checkArgument(row > 0 && column > 0,
                format("Row and Column should be greater than zero col: %s, row: %s", column, row));

        Cell[][] map = new Cell[row][column];

        fillWith(map, Cell.WALL);

        while (!DungeonUtils.hasPassableCells(map))
        {

            DungeonPart part = new DungeonPart(new int[]{0, 0, row - 1, column - 1});

            fillWith(map, Cell.WALL);

            List<DungeonPart> parts = part.split();

            while (!parts.isEmpty())
            {
                DungeonPart curr = parts.iterator().next();
                parts.remove(curr);
                if (curr.canSplit())
                {
                    parts.addAll(curr.split());
                }
            }

            part.projectOnMap(map);

        }

        Cells dungeon = new Cells(map, getId());

        for (int i = 0; i < dungeon.getRows(); i++)
            for (int j = 0; j < dungeon.getCols(); j++)
            {
                if (dungeon.getCell(i, j).isPassable())
                {
                    Set<Direction> passDirections = newHashSet();
                    for (Direction dir : Direction.values())
                    {
                        Point point = dir.step(i, j);
                        Cell cell = dungeon.getCell(point.x, point.y);
                        if (cell.isPassable())
                            passDirections.add(dir);
                    }


                }
            }


        return dungeon;
    }

    @Override
    public String getId()
    {
        return "BFS";
    }

    public static void main(String[] args)
    {
        System.out.println(new BfsGenerator().generate(50, 50));
    }
}

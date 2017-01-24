package dungeon.generator;

import dungeon.Cell;
import dungeon.Cells;

/**
 * Generate spiral map.
 *
 * @author Blind
 */
public class SpiralGenerator implements DungeonGenerator {

    public Cells generate(int row, int column) {
        Cell[][] map = new Cell[row][column];

        for (int i = 0; i < row; i++)
            for (int j = 0; j < column; j++)
                map[i][j] = Cell.WALL;

        int x = row / 2 - 1;
        int y = column / 2 - 1;

        int[] cur = {x, y};
        cur[1]++;
        blank(map, cur);
        cur[1]++;
        blank(map, cur);
        cur[0]--;
        blank(map, cur);
        cur[0]--;
        blank(map, cur);

        int dir = 2;
        int dist = 3;
        while (cur[0] >= 0 && cur[1] >= 0 && cur[0] < row && cur[1] < column) {
            for (int i = 0; i < dist; i++)
                makeBlank(dir, map, cur);
            dist++;
            dir = dir == 3 ? 0 : dir + 1;

        }


        return new Cells(map, getId());
    }

    @Override
    public String getId()
    {
        return "SPIRAL";
    }

    private void makeBlank(int dir, Cell[][] map, int[] cur) {
        if (dir == 0) {
            cur[1]++;
        } else if (dir == 1) {
            cur[0]--;
        } else if (dir == 2) {
            cur[1]--;
        } else if (dir == 3) {
            cur[0]++;
        } else {
            throw new RuntimeException("Illegal argument " + dir);
        }

        if (cur[0] >= 0 && cur[1] >= 0 && cur[0] < map.length && cur[1] < map[0].length)
            blank(map, cur);
    }

    private void blank(Cell[][] map, int[] cur) {
        map[cur[0]][cur[1]] = Cell.BLANK;
    }

}

package dungeon;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

/**
 * Provides utils methods for map generation.
 * 
 * @author Blind
 */
public class DungeonUtils
{

	public static boolean hasPassableCells(Cell[][] cells)
	{
		return hasPassableCells(cells, 0, 0, cells.length, cells[0].length);
	}

	public static void fillWith(Cell[][] cells, Cell fillWith)
	{
		for (int i = 0; i < cells.length; i++)
			for (int j = 0; j < cells[0].length; j++)
				cells[i][j] = fillWith;
	}

	public static boolean hasPassableCells(Cell[][] cells, int x1, int y1, int x2, int y2)
	{
		for (int i = x1; i < x2; i++)
			for (int j = y1; j < y2; j++)
			{
				if (cells[i][j].isPassable()) return true;
			}
		return false;
	}

	/**
	 * Map path from (x1, y1) to (x2, y2) with straight lines.
	 * 
	 * @param map
	 *            origin
	 * @param fillWith
	 *            will corridor with such cell type
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * 
	 * @return full path as a list of points.
	 */
	public static List<int[]> p2p(Cell[][] map, Cell fillWith, int x1, int y1, int x2, int y2)
	{
		int x = x1, y = y1;

		List<int[]> path = newArrayList();

		map[x][y] = fillWith;

		while (x != x2)
		{
			x = x > x2 ? x - 1 : x + 1;
			map[x][y] = fillWith;
			path.add(new int[] { x, y });
		}

		while (y != y2)
		{
			y = y > y2 ? y - 1 : y + 1;
			map[x][y] = fillWith;
			path.add(new int[] { x, y });
		}

		return path;
	}
}

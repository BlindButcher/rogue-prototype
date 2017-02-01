package dungeon.generator;

import dungeon.Cell;
import dungeon.Cells;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

public class BackTrackGenerator implements DungeonGenerator
{
	public Cells generate(int rows, int cols)
	{
		Cell[][] cells = new Cell[rows][cols];
		Random random = new Random();
		int numberOfCells = rows * cols;

		for (int i = 0; i < cells.length; i++)
			for (int j = 0; j < cells[i].length; j++)
				cells[i][j] = Cell.WALL;

		Set<Pair> visited = new HashSet<Pair>();

		Pair currentCell = new Pair(1, 1);
		cells[1][1] = Cell.BLANK;

		visited.add(currentCell);

		Stack<Pair> stack = new Stack<Pair>();
		while (visited.size() < numberOfCells)
		{
			List<Pair> neighbours = unvisitedNeighbours(rows, cols, currentCell, visited);
			if (!neighbours.isEmpty())
			{
				Pair chosen = neighbours.get(random.nextInt(neighbours.size()));
				stack.push(currentCell);
				cells[chosen.x][chosen.y] = Cell.BLANK;
				currentCell = chosen;
				visited.add(currentCell);
				numberOfCells--;
			}
			else if (!stack.isEmpty())
			{
				currentCell = stack.pop();
			}
			else
			{
				int rRow = random.nextInt(rows);
				int rCol = random.nextInt(cols);
				if (!visited.contains(new Pair(rRow, rCol)))
				{
					currentCell = new Pair(rRow, rCol);
					cells[rRow][rCol] = Cell.WALL;
					visited.add(currentCell);
				}
			}

		}

		return new Cells(cells, getId());
	}

    @Override
    public String getId()
    {
        return "BACK_TRACK";
    }

    private static List<Pair> unvisitedNeighbours(int rows, int cols, Pair currentCell,
			Set<Pair> visited)
	{
		List<Pair> results = new ArrayList<Pair>();

		results.add(new Pair(currentCell.x + 1, currentCell.y));
		results.add(new Pair(currentCell.x, currentCell.y + 1));
		results.add(new Pair(currentCell.x - 1, currentCell.y));
		results.add(new Pair(currentCell.x, currentCell.y - 1));
		results.add(new Pair(currentCell.x + 1, currentCell.y + 1));
		results.add(new Pair(currentCell.x - 1, currentCell.y - 1));
		results.add(new Pair(currentCell.x + 1, currentCell.y - 1));
		results.add(new Pair(currentCell.x - 1, currentCell.y + 1));

		List<Pair> forFilter = new ArrayList<Pair>();
		for (Pair result : results)
		{
			if (result.x < 0 || result.y < 0 || result.x > rows - 1 || result.y > cols - 1
					|| visited.contains(result)) forFilter.add(result);
		}

		results.removeAll(forFilter);

		return results;
	}

	private static class Pair
	{
		public final int x, y;

		public Pair(int x, int y)
		{
			this.x = x;
			this.y = y;
		}

		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Pair pair = (Pair) o;

			if (x != pair.x) return false;
			if (y != pair.y) return false;

			return true;
		}

		public int hashCode()
		{
			int result = x;
			result = 31 * result + y;
			return result;
		}
	}
}

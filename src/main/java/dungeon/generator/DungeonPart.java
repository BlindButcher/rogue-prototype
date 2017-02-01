package dungeon.generator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static dungeon.DungeonUtils.hasPassableCells;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Range;
import dungeon.Cell;
import dungeon.Cells;
import util.Direction;
import dungeon.DungeonUtils;

/**
 * Contains logic for managing dungeon parts
 * 
 * @author Blind
 */
class DungeonPart
{
	private static final Random random = new Random();
	private static final int ROOM_SIZE = 8;
	private static final double MAX_DELTA = 0.25;
	private static final double C = 2;
	public static final int MIN_ROOM_SIZE = 3;
	public static final int MIN_PATH_FOR_TWO_DOORS = 3;

	private static enum IntersectionType
	{
		X,
		Y,
		NONE
	}

	public int[] points;

	public List<DungeonPart> children = newArrayList();

	public DungeonPart(int[] points)
	{
		this.points = points;
	}

	public List<DungeonPart> split()
	{
		List<DungeonPart> results = newArrayList();

		if (!canSplit() || !children.isEmpty()) return Collections.emptyList();

		boolean xSplit = xSplit() && ySplit() ? random.nextBoolean() : xSplit();

		if (xSplit)
		{
			final int length = points[2] - points[0];
			final int center = length / 2;

			int delta = (int) (center * MAX_DELTA);
			delta = delta == 0 ? 1 : delta;
			final int size = random.nextBoolean() ? center - random.nextInt(delta) + 1 : center
					+ random.nextInt(delta) + 1;

			results.add(new DungeonPart(new int[] { points[0], points[1], points[0] + size,
					points[3] }));
			results.add(new DungeonPart(new int[] { points[0] + size, points[1], points[2],
					points[3] }));
		}
		else
		{
			final int length = points[3] - points[1];
			final int center = length / 2;
			int delta = (int) (center * MAX_DELTA);
			delta = delta == 0 ? 1 : delta;
			final int size = random.nextBoolean() ? center - random.nextInt(delta) + 1 : center
					+ random.nextInt(delta) + 1;

			results.add(new DungeonPart(new int[] { points[0], points[1], points[2],
					points[1] + size }));
			results.add(new DungeonPart(new int[] { points[0], points[1] + size, points[2],
					points[3] }));
		}

		children = new ArrayList<>(results);

		return results;
	}

	public boolean canSplit()
	{
		return xSplit() || ySplit();
	}

	private boolean ySplit()
	{
		return points[3] - points[1] > ROOM_SIZE * C;
	}

	private boolean xSplit()
	{
		return points[2] - points[0] > ROOM_SIZE * C;
	}

	public void projectOnMap(Cell[][] map)
	{
		if (children.isEmpty() && canPlaceRoom())
		{
			placeRoom(map);
		}
		else
		{
			for (DungeonPart part : children)
				part.projectOnMap(map);
		}

		connectChildren(map);
	}

	private void connectChildren(Cell[][] map)
	{
		if (children.size() == 2)
		{
			DungeonPart first = children.get(0);
			DungeonPart second = children.get(1);

			if (!hasPassableCells(map, first.points[0], first.points[1], first.points[2],
					first.points[3])
					|| !hasPassableCells(map, second.points[0], second.points[1], second.points[2],
							second.points[3]))
			{
				return;
			}

			IntersectionType type = first.xRange().isConnected(second.xRange()) ? IntersectionType.X
					: first.yRange().isConnected(second.yRange()) ? IntersectionType.Y
							: IntersectionType.NONE;

			switch (type)
			{
			case X:
				Range<Integer> intersection = first.xRange().intersection(second.xRange());
				List<int[]> list = newArrayList();

				for (int j = intersection.lowerEndpoint(); j < intersection.upperEndpoint(); j++)
				{
					int firstY = -1, secondY = -1;

					for (int i = first.yRange().lowerEndpoint(); i < first.yRange().upperEndpoint(); i++)
					{
						if (map[j][i] == Cell.BLANK)
						{
							firstY = i;
							break;
						}

					}

					for (int i = second.yRange().lowerEndpoint(); i < second.yRange()
							.upperEndpoint(); i++)
					{
						if (map[j][i] == Cell.BLANK)
						{
							secondY = i;
							break;
						}

					}

					if ((firstY != -1) && (secondY != -1))
					{
						list.add(new int[] { j, firstY, secondY });
					}
				}

				checkArgument(!list.isEmpty());

				int[] chosen = list.get(random.nextInt(list.size()));

				List<int[]> path = DungeonUtils.p2p(map, Cell.BLANK, chosen[0], chosen[1],
                        chosen[0], chosen[2]);

				placeDoors(map, path, asList(Direction.WEST, Direction.EAST));

				break;
			case Y:
				intersection = first.yRange().intersection(second.yRange());
				list = newArrayList();

				for (int j = intersection.lowerEndpoint(); j < intersection.upperEndpoint(); j++)
				{
					int firstX = -1, secondX = -1;

					for (int i = first.xRange().lowerEndpoint(); i < first.xRange().upperEndpoint(); i++)
					{
						if (map[i][j] == Cell.BLANK)
						{
							firstX = i;
							break;
						}

					}

					for (int i = second.xRange().lowerEndpoint(); i < second.xRange()
							.upperEndpoint(); i++)
					{
						if (map[i][j] == Cell.BLANK)
						{
							secondX = i;
							break;
						}

					}

					if ((firstX != -1) && (secondX != -1))
					{
						list.add(new int[] { j, firstX, secondX });
					}
				}

				boolean bool = !list.isEmpty();
				checkArgument(bool);

				chosen = list.get(random.nextInt(list.size()));

				path = DungeonUtils
						.p2p(map, Cell.BLANK, chosen[1], chosen[0], chosen[2], chosen[0]);

				placeDoors(map, path, asList(Direction.NORTH, Direction.SOUTH));

				break;
			case NONE:
				break;
			default:
				throw new IllegalArgumentException("Not handler for type" + type);
			}
		}

	}

	private void placeDoors(Cell[][] map, List<int[]> path, List<Direction> wallDirs)
	{
		Cells fullDungeon = new Cells(map, "EMPTY");
		boolean firstPlaced = false, secondPlaced = false;
		int dist = 0;

		for (int[] point : path)
		{
			boolean walls = true;

			for (Direction dir : wallDirs)
				walls &= !fullDungeon.getCell(dir.step(point[0], point[1])).isPassable();

			if (walls && !firstPlaced)
			{
				map[point[0]][point[1]] = Cell.DOOR;
				firstPlaced = true;
			}
			else if (dist < MIN_PATH_FOR_TWO_DOORS && firstPlaced)
			{
				dist++;
			}
			else if (firstPlaced && dist >= MIN_PATH_FOR_TWO_DOORS && walls && !secondPlaced)
			{
				map[point[0]][point[1]] = Cell.DOOR;
				secondPlaced = true;
			}

		}
	}

	public Range<Integer> xRange()
	{
		return Range.open(points[0], points[2]);
	}

	public Range<Integer> yRange()
	{
		return Range.open(points[1], points[3]);
	}

	private boolean canPlaceRoom()
	{
		return points[2] - points[0] >= MIN_ROOM_SIZE && points[3] - points[1] >= MIN_ROOM_SIZE;
	}

	private void placeRoom(Cell[][] map)
	{
		final int width = points[2] - points[0];
		final int height = points[3] - points[1];

		int wDelta = (width - MIN_ROOM_SIZE) / 2;
		int hDelta = (height - MIN_ROOM_SIZE) / 2;

		wDelta = wDelta == 0 ? 1 : random.nextInt(wDelta) + 1;
		hDelta = hDelta == 0 ? 1 : random.nextInt(hDelta) + 1;

		for (int i = points[0] + wDelta; i < points[2] - wDelta; i++)
			for (int j = points[1] + hDelta; j < points[3] - hDelta; j++)
				map[i][j] = Cell.BLANK;
	}
}

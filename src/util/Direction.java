package util;

import static com.google.common.collect.Lists.newArrayList;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;

/**
 * Direction enum.
 * 
 * @author Blind
 */
public enum Direction
{
	NORTH(0, 1),
	SOUTH(0, -1),
	EAST(1, 0),
	WEST(-1, 0),
	NORTH_EAST(1, 1),
	NORTH_WEST(-1, 1),
	SOUTH_EAST(1, -1),
	SOUTH_WEST(-1, -1);

	private int dx, dy;

	private Direction(int x, int y)
	{
		this.dx = x;
		this.dy = y;
	}

	public Point step(int x, int y)
	{
		return new Point(x + dx, y + dy);
	}

	public Point step(Point point)
	{
		return new Point(point.x + dx, point.y + dy);
	}

	public Point steps(Point point, int stepCount)
	{
		return new Point(point.x + dx * stepCount, point.y + dy * stepCount);
	}

	public static Direction vector(Point start, Point end)
	{
		final int x = end.x - start.x;
		final int y = end.y - start.y;

		Preconditions.checkArgument(Range.closed(-1, 1).contains(x));
		Preconditions.checkArgument(Range.closed(-1, 1).contains(y));
		return Iterables.find(newArrayList(Direction.values()), new Predicate<Direction>()
		{
			public boolean apply(Direction direction)
			{
				return direction.dx == x && direction.dy == y;
			}
		});
	}
}

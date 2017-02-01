package util;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import creature.Creature;
import dungeon.Cells;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Utils
{
	private static final Random random = new Random();

	private Utils()
	{
	}

	static public int randomInRange(int min, int max)
	{
		Preconditions.checkArgument(max >= min);
		return min + random.nextInt(max - min + 1);
	}

	static public int roll(int dice)
	{
		return random.nextInt(dice);
	}

	static public boolean chance(int percentage)
	{
		return roll(100) < percentage;
	}

	static public double normal()
	{
		return random.nextGaussian();
	}

	static public boolean reach(int x1, int y1, int x2, int y2, int dist)
	{
		return dist2(x1, y1, x2, y2) <= dist * dist;
	}

    static public boolean reach(Point p1, Point p2, int dist)
    {
        return dist2(p1.x, p1.y, p2.x, p2.y) <= dist * dist;
    }

	static public boolean reach(Creature a, Creature b, int dist)
	{
		return dist2(a, b) <= dist * dist;
	}

	static public int dist2(Creature a, Creature b)
	{
		return dist2(a.getX(), a.getY(), b.getX(), b.getY());
	}

	static public int dist2(int x1, int y1, int x2, int y2)
	{
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
	}

	static public boolean los(Cells map, int x1, int y1, int x2, int y2)
	{
		List<int[]> points = bresenham(x1, y1, x2, y2);
		for (int i = 1; i < points.size() - 1; i++)
		{
			int x = points.get(i)[0], y = points.get(i)[1];
			if (!map.getCell(x, y).isPassable()) return false;
		}
		return true;
	}

	static public int[] nextPoint(int x1, int y1, int x2, int y2)
	{
		return bresenham(x1, y1, x2, y2).get(1);
	}

	static public List<int[]> bresenham(int x1, int y1, int x2, int y2)
	{
		List<int[]> result = new ArrayList<int[]>();

		int dx = Math.abs(x2 - x1);
		int dy = Math.abs(y2 - y1);

		int sx = x1 < x2 ? 1 : -1;
		int sy = y1 < y2 ? 1 : -1;
		int err = dx - dy;

		while (true)
		{
			result.add(new int[] { x1, y1 });

			if (x1 == x2 && y1 == y2) break;

			int e2 = err * 2;
			if (e2 > -dy)
			{
				err -= dy;
				x1 += sx;
			}
			if (e2 < dx)
			{
				err += dx;
				y1 += sy;
			}
		}

		return result;
	}

    public static List<Point> bresenham(Point p1, Point p2) {
        return Lists.transform(bresenham(p1.x, p1.y, p2.x, p2.y), new Function<int[], Point>()
        {
            public Point apply(int[] ints)
            {
                return Point.point(ints[0], ints[1]);
            }
        });
    }

    public static int dist2(Point p1, Point p2)
    {
        return dist2(p1.x, p1.y, p2.x, p2.y);
    }
}

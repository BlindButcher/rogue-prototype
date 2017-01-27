package util;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class Point
{

	public final int x, y;

	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Point pair = (Point) o;

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

	public static Point point(int x, int y)
	{
		return new Point(x, y);
	}

	public static Point point(int[] xy)
	{
		return new Point(xy[0], xy[1]);
	}

    public Point setX(int x) {
        return point(x, y);
    }

    public Point setY(int y) {
        return point(x, y);
    }

    public String toString()
    {
        return "Point{x=" + x + ", y=" + y + '}';
    }

    public Collection<Point> neighbours()
	{
		return Collections2.transform(newArrayList(Direction.values()), new Function<Direction, Point>()
		{
			public Point apply(Direction direction)
			{
				return direction.step(Point.this);
			}

		});
	}

    public Collection<Point> neighboursAndSelf() {
        List<Point> pointList = newArrayList(neighbours());
        pointList.add(this);
        return pointList;
    }
}
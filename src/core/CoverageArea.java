package core;

import com.google.common.collect.Sets;
import util.Direction;
import util.Point;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

/**
 * Types of area effecting abilities, weapons etc.
 *
 * @author Blind
 */
public enum CoverageArea {
    SINGLE_SQUARE("Performs common attack.") {
        public Set<Point> effectedPoints(Point start, Point end) {
            return Sets.newHashSet(end);
        }
    },
    ARC("Fire in arc.") {
        public Set<Point> effectedPoints(Point start, Point end) {
            Direction direction = Direction.vector(start, end);

            if (direction == Direction.NORTH || Direction.SOUTH == direction) {
                return newHashSet(Point.point(end.x - 1, end.y), end, Point.point(end.x + 1, end.y));
            }

            if (direction == Direction.EAST || Direction.WEST == direction) {
                return newHashSet(Point.point(end.x, end.y - 1), end, Point.point(end.x, end.y + 1));
            }

            if (direction == Direction.NORTH_EAST) {
                return newHashSet(Point.point(end.x - 1, end.y), end, Point.point(end.x, end.y - 1));
            }

            if (direction == Direction.NORTH_WEST) {
                return newHashSet(Point.point(end.x + 1, end.y), end, Point.point(end.x, end.y - 1));
            }

            if (direction == Direction.SOUTH_EAST) {
                return newHashSet(Point.point(end.x, end.y + 1), end, Point.point(end.x - 1, end.y));
            }

            if (direction == Direction.SOUTH_WEST) {
                return newHashSet(Point.point(end.x, end.y + 1), end, Point.point(end.x + 1, end.y));
            }

            return newHashSet(end);
        }
    },

    LINE3("Flushes in line.") {
        public Set<Point> effectedPoints(Point start, Point end) {
            Direction direction = Direction.vector(start, end);
            return newHashSet(end, direction.step(end), direction.steps(end, 2));
        }
    };

    private CoverageArea(String text) {
        this.text = text;
    }

    public final String text;

    public abstract Set<Point> effectedPoints(Point start, Point end);

}

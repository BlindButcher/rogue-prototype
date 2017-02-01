package main;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import creature.Creature;
import creature.Hero;
import dungeon.Cell;
import dungeon.Cells;
import dungeon.Dwelling;
import inventory.Item;
import util.Point;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;

public class LevelContext {
    private static final Logger LOG = Logger.getLogger(LevelContext.class.getName());
    private final int depth;
    private final Cells map;
    private final List<Creature> creatures = Lists.newArrayList();
    private final ListMultimap<Point, Item> items = ArrayListMultimap.create();
    private List<int[]> potions = Lists.newArrayList();
    private int[] downStairs, upStairs;
    private List<Dwelling> dwellings = newArrayList();

    public LevelContext(Cells cells, int depth) {
        this.depth = depth;
        map = cells;
        extractStairs(map.getRows(), map.getCols());
    }

    private void extractStairs(int rows, int cols) {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (map.getCell(i, j) == Cell.DOWN_STAIRS) {
                    downStairs = new int[]{i, j};
                } else if (map.getCell(i, j) == Cell.UP_STAIRS) {
                    upStairs = new int[]{i, j};
                }
    }

    public void addCreature(Creature creature) {
        creatures.add(creature);
    }

    public void addItem(Item item, int x, int y) {
        LOG.info(format("%s was added for %s level on %s, %s", item, depth, x, y));
        items.put(new Point(x, y), item);
    }

    public Cells getMap() {
        return map;
    }

    public List<Creature> getCreatures() {
        return creatures;
    }

    public Point getRandomEmptyCell() {
        Point point;
        do {
            point = Point.point(map.getRandomPassableCell());
        } while (getCreature(point) != null);
        return point;
    }

    public boolean canEnter(Point point) {
        return map.getCell(point).isPassable() && getCreature(point) == null;
    }

    public Creature getCreature(int x, int y) {
        for (Creature c : getCreatures()) {
            if (c.getX() == x && c.getY() == y) return c;
        }
        return null;
    }

    public Creature getCreature(Point p) {
        return getCreature(p.x, p.y);
    }

    public Optional<Creature> in(Point p) {
        return Optional.ofNullable(getCreature(p));
    }

    public Set<Creature> locatedIn(Collection<Point> points) {
        Set<Creature> results = newHashSet();
        for (Point p : points) {
            Optional<Creature> creature = in(p);
            if (creature.isPresent()) results.add(creature.get());
        }
        return results;
    }

    public Optional<Creature> findHero() {
        return getCreatures().stream().filter(c -> c instanceof Hero).findFirst();
    }

    public boolean inhabitByBoss(final String bossId) {
        return creatures.stream().filter(Creature::isBoss).
                filter(c -> c.getTemplateId().equals(bossId)).findAny().isPresent();

    }

    public List<Item> getItems(int x, int y) {
        return items.get(new Point(x, y));
    }

    public void openDoor(int x, int y) {
        if (map.getCell(x, y) != Cell.DOOR) return;
        map.setCell(x, y, Cell.OPENED_DOOR);
    }

    public int getDepth() {
        return depth;
    }

    public void addPotion(int[] point) {
        potions.add(point);
    }

    public List<int[]> getPotions() {
        return potions;
    }

    public int[] getDownStairs() {
        return downStairs;
    }

    public int[] getUpStairs() {
        return upStairs;
    }

    public List<Dwelling> getDwellings() {
        return dwellings;
    }
}

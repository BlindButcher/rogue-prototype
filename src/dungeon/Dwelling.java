package dungeon;

import util.INamedEntity;
import util.MapObject;
import util.Point;
import view.IRenderable;

/**
 * Contains map object, which can generate creatures.
 *
 * @author Blind
 */
public class Dwelling implements INamedEntity, MapObject, IRenderable
{
    private final String id;
    private final String name;
    private final CreatureGenerator strategy;
    private final Point point;

    public Dwelling(String id, String name, CreatureGenerator strategy, Point point)
    {
        this.id = id;
        this.name = name;
        this.strategy = strategy;
        this.point = point;
    }

    public void birth()
    {
        strategy.scheduleBirth(point);
    }

    public String getId()
    {
        return id;
    }

    public String getImagePath()
    {
        return "graphic/dwelling/rat.png";
    }

    public String getName()
    {
        return name;
    }

    public Point getLocation()
    {
        return point;
    }

    public void setLocation(Point point)
    {
        throw new RuntimeException("Not supported.");
    }
}

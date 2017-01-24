package main;

import creature.Creature;
import creature.CreatureTemplate;
import util.Point;

/**
 * Schedules birth of new Creature.
 *
 * @author Blind
 */
public class BirthEvent extends TimeEvent
{
    public final Point point;
    public final CreatureTemplate template;

    public BirthEvent(long time, Creature creature, Point point, CreatureTemplate template)
    {
        super(time, creature, EventType.BIRTH);
        this.point = point;
        this.template = template;
    }
}

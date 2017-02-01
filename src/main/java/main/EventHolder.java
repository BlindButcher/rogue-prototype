package main;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import creature.Creature;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Holds game event state and TimeEvent queue.
 *
 * @author Blind
 */
public class EventHolder
{
    public long time = 0;
    public long lastSpawnTime = time;
    private Queue<TimeEvent> queue = new PriorityQueue<>();

    public void addEvent(TimeEvent event)
    {
        queue.offer(event);
    }

    public TimeEvent poll()
    {
        return queue.poll();
    }

    public List<TimeEvent> relatedFor(final Creature creature)
    {
        return newArrayList(Collections2.filter(queue, new Predicate<TimeEvent>()
        {
            public boolean apply(TimeEvent event)
            {
                return creature.equals(event.getCreature());
            }
        }));
    }
}

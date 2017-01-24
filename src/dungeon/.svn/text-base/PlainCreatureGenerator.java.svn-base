package dungeon;

import creature.CreatureTemplate;
import main.BirthEvent;
import main.EventHolder;
import main.TimeEvent;
import main.TimeEventAware;
import util.Point;
import util.RandomList;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Generates creature base on different strategies.
 *
 * @author Blind
 */
public class PlainCreatureGenerator implements TimeEventAware, CreatureGenerator
{
    private static final int BIRTH_INTERVAL = 300;
    private static final int MAX_GENERATED = 3;

    private List<CreatureTemplate> templates = newArrayList();
    private long firstGenerated;
    private int generatedCount = 0;
    private EventHolder eventHolder;

    public PlainCreatureGenerator(List<CreatureTemplate> templates, EventHolder eventHolder)
    {
        this.templates = templates;
        this.eventHolder = eventHolder;
    }

    private boolean canBirth()
    {
        return getEventHolder().time > firstGenerated + BIRTH_INTERVAL && generatedCount <= MAX_GENERATED;
    }

    @Override
    public void scheduleBirth(Point point)
    {
        if (!canBirth()) return;

        CreatureTemplate template = new RandomList<>(templates).getItem();
        firstGenerated = getEventHolder().time;
        generatedCount++;
        getEventHolder().addEvent(new BirthEvent(10, null, point, template));
    }

    public void addEvent(TimeEvent event)
    {
        getEventHolder().addEvent(event);
    }

    public void setEventHolder(EventHolder eventHolder)
    {
        this.eventHolder = eventHolder;
    }

    public EventHolder getEventHolder()
    {
        return eventHolder;
    }
}

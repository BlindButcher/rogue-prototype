package dungeon;


import creature.Creature;
import creature.CreatureTemplate;
import main.*;
import util.Point;
import util.RandomList;
import util.Utils;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Just like a plain generator, but spawns creatures only if hero is near.
 *
 * @author Blind
 */
public class NearCreatureGenerator implements TimeEventAware, CreatureGenerator {
    private static final int BIRTH_INTERVAL = 50;
    private static final int MAX_GENERATED = 3;
    private static final int LIMIT_DISTANCE = 5;

    private List<CreatureTemplate> templates = newArrayList();
    private long firstGenerated;
    private int generatedCount = 0;
    private EventHolder eventHolder;
    private LevelContext levelContext;

    public NearCreatureGenerator(List<CreatureTemplate> templates, EventHolder eventHolder, LevelContext context) {
        this.templates = templates;
        this.eventHolder = eventHolder;
        this.levelContext = context;
    }

    private boolean canBirth(Point point) {
        Optional<Creature> hero = levelContext.findHero();
        return getEventHolder().time > firstGenerated + BIRTH_INTERVAL && generatedCount <= MAX_GENERATED &&
                hero.isPresent() && Utils.dist2(hero.get().getXY(), point) < LIMIT_DISTANCE;
    }

    public void scheduleBirth(Point point) {
        if (!canBirth(point)) return;


        CreatureTemplate template = new RandomList<>(templates).getItem();
        firstGenerated = getEventHolder().time;
        generatedCount++;
        getEventHolder().addEvent(new BirthEvent(1, null, point, template));
    }

    public void addEvent(TimeEvent event) {
        getEventHolder().addEvent(event);
    }

    public void setEventHolder(EventHolder eventHolder) {
        this.eventHolder = eventHolder;
    }

    public EventHolder getEventHolder() {
        return eventHolder;
    }
}


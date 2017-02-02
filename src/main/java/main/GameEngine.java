package main;


import ability.AbilityType;
import com.google.common.base.Optional;
import creature.Creature;
import creature.CreatureTemplate;
import effect.IEffect;
import util.Point;

import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.tryFind;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static main.EventType.MOVE;

/**
 * Applies action to Game State, mutate it and returns back.
 *
 * @author BlindButcher
 * @since  2/1/17
 */
public class GameEngine implements Function<WorldState, WorldState> {

    @Override
    public WorldState apply(WorldState state) {
        TimeEvent event = pollEvent(state);
        Creature c = event.getCreature();
        switch (event.getType())
        {
            case MOVE:
                c.onMove();
                if (state.hero.filter(h -> h.equals(c)).isPresent())
                {
                    state.heroMove = true;
                    return state;
                }
                if (!c.isAlive())
                {
                    state.currentLevel.getCreatures().remove(c);
                }
                else
                {
                    int tm = c.update();
                    state.eventHolder.addEvent(new TimeEvent(state.eventHolder.time + tm, c, MOVE));
                }
                break;
            case KILLED:
                if (!c.isAlive())
                {
                    state.currentLevel.getCreatures().remove(c);
                }
                break;
            case EFFECT:
                if (c.isAlive())
                {
                    IEffect effect = ((EffectTimeEvent) event).getEffect();
                    effect.remove(c);
                }
                break;

            case BIRTH:
                CreatureTemplate creatureTemplate = ((BirthEvent) event).template;
                Point point =  ((BirthEvent) event).point;
                Creature creature = new Creature(creatureTemplate);

                Optional<Point> availablePoint = tryFind(concat(asList(((BirthEvent) event).point),
                        point.neighbours()), state.currentLevel::canEnter);

                if (availablePoint.isPresent())
                {
                    checkArgument(state.currentLevel.canEnter(availablePoint.get()));
                    creature.register(state.currentLevel, availablePoint.get(), 1);
                    creature.setEventHolder(state.eventHolder);
                    state.currentLevel.addCreature(creature);
                    state.eventHolder.addEvent(new TimeEvent(state.eventHolder.time + 1, creature, MOVE));
                }
                break;
            case ABILITY:
                AbilityType type = ((RegainAbilityEvent) event).getAbilityType();
                event.getCreature().activateAbility(type);
                break;
            default: throw new RuntimeException(format("Event not handled %s", event.getType()));
        }

        return state;
    }

    public TimeEvent pollEvent(WorldState state)
    {
        TimeEvent res = state.eventHolder.poll();
        state.eventHolder.time = res.getTime();
        state.currentLevel.getDwellings().forEach(dungeon.Dwelling::birth);
        return res;
    }
}

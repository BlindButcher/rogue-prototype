package main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import creature.Hero;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * Wrapper around world state, contains position and state of all creatures.
 *
 * @author BlindButcher
 * @since 2/1/17
 */
public class WorldState {
    final EventHolder eventHolder;
    private List<LevelContext> levels = newArrayList();
    @JsonInclude
    public LevelContext currentLevel;
    @JsonInclude
    public Optional<Hero> hero = empty();
    public boolean heroMove = false;

    public WorldState(EventHolder eventHolder) {
        this.eventHolder = requireNonNull(eventHolder);
    }

    public void setHero(Hero hero) {
        this.hero = of(hero);
    }

    public void addEvent(TimeEvent event) {
        eventHolder.addEvent(event);
    }

    @JsonIgnore
    public List<LevelContext> getLevels() {
        return levels;
    }

    public void setLevels(List<LevelContext> levels) {
        this.levels = levels;
    }
}

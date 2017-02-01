package main;

import ability.AbilityType;
import com.google.common.base.Preconditions;
import creature.Creature;

/**
 * Contains ability type to regain after the event resolves.
 *
 * @author Blind
 */
public class RegainAbilityEvent extends TimeEvent
{
    private final AbilityType abilityType;

    public RegainAbilityEvent(long time, Creature creature, AbilityType abilityType)
    {
        super(time, creature, EventType.ABILITY);
        Preconditions.checkNotNull(abilityType);
        this.abilityType = abilityType;
    }

    public AbilityType getAbilityType()
    {
        return abilityType;
    }
}

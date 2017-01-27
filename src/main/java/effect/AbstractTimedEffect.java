package effect;

import main.EffectTimeEvent;
import core.Mechanics;
import creature.Creature;

public abstract class AbstractTimedEffect implements IEffect
{
	private final int duration;

	protected AbstractTimedEffect(int duration)
	{
		super();
		this.duration = Mechanics.normal(duration);
	}

	public int getDuration()
	{
		return duration;
	}

	protected abstract void internalApply(Creature creature);

	protected abstract void internalRemove(Creature creature);

	@Override
	public final void apply(Creature creature)
	{
		internalApply(creature);
		creature.getActiveEffects().add(this);
		creature.addEvent(new EffectTimeEvent(creature.getEventHolder().time + duration, creature, this));
	}

	@Override
	public final void remove(Creature creature)
	{
		internalRemove(creature);
		creature.getActiveEffects().remove(this);
	}
	
}

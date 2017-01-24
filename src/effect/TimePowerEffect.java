package effect;

import creature.Creature;

/**
 * Time powered effect.
 * 
 * @author Blind
 */
public class TimePowerEffect extends AbstractTimedEffect implements IPowerTimeEffect
{
	private final int power;
	private final EffectType type;

	protected TimePowerEffect(EffectType type, int power, int duration)
	{
		super(duration);
		this.power = power;
		this.type = type;
	}

	@Override
	protected void internalApply(Creature creature)
	{
		type.apply(creature, this);
	}

	@Override
	protected void internalRemove(Creature creature)
	{
		type.remove(creature, this);
	}

	@Override
	public int getPower()
	{
		return power;
	}

	@Override
	public EffectType getType()
	{
		return type;
	}
}

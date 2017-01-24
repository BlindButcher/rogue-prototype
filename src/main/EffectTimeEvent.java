package main;

import creature.Creature;
import effect.IEffect;

public class EffectTimeEvent extends TimeEvent
{
	private final IEffect effect;

	public EffectTimeEvent(long time, Creature creature, IEffect effect)
	{
		super(time, creature, EventType.EFFECT);
		this.effect = effect;
	}

	public IEffect getEffect()
	{
		return effect;
	}
}

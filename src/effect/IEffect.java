package effect;

import creature.Creature;

public interface IEffect
{
	void apply(Creature creature);
	
	void remove(Creature creature);

    EffectType getType();
}

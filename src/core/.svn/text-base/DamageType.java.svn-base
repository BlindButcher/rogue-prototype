package core;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import creature.Creature;
import creature.HitContext;
import effect.EffectType;
import effect.IEffect;
import effect.TimePowerEffect;
import inventory.armor.ArmorType;
import main.EffectTimeEvent;
import main.TimeEvent;

import java.util.Collection;
import java.util.logging.Logger;

import static com.google.common.collect.Collections2.filter;
import static java.lang.String.format;
import static main.EventType.EFFECT;

public enum DamageType
{
	PHYSICAL(true, false),
	FIRE(false, false)
	{
		public void applyEffect(HitContext context)
		{
			Creature target = context.getTarget();
			if (target.getArmorType() == ArmorType.NONE)
			{
				int oldDamage = context.getDamage(this);
				int newDamage = oldDamage * 150 / 100;
				context.setDamage(this, newDamage);
			}
		}
	},
    HEAT(false, false)
    {
        public void applyEffect(HitContext context)
        {
            Creature target = context.getTarget();

            boolean hasHeat = Iterables.tryFind(target.getActiveEffects(), new Predicate<IEffect>()
            {
                public boolean apply(IEffect iEffect)
                {
                    return iEffect.getType() == EffectType.HEAT;
                }
            }).isPresent();

            if (hasHeat)
            {
                int sum = 0;
                Collection<TimeEvent> events = filter(target.getEventHolder().relatedFor(target), new Predicate<TimeEvent>()
                {
                    public boolean apply(TimeEvent event)
                    {
                        return event.getType() == EFFECT && ((EffectTimeEvent) event).getEffect().getType() == EffectType.HEAT;
                    }
                });
                for (TimeEvent event : events)
                {
                    EffectTimeEvent effectTimeEvent = (EffectTimeEvent) event;
                    sum += ((TimePowerEffect) effectTimeEvent.getEffect()).getPower();
                }
                int oldDamage = context.getDamage(this);
                int newDamage = oldDamage * sum;
                MAIN.fine(format("Increase damage due to heat by %s", sum));
                context.setDamage(this, newDamage);
            }

            if (Mechanics.tryEffect(context.getDamage(this), 0))
            {
                EffectType.HEAT.effectOn(target, (int) (context.getDamage(this) * HEAT_MOD), context.getDamage(this));
            }

        }
    },
	ELECTRIC(false, true),
	COLD(false, false);

    private static final Logger MAIN = Logger.getLogger("MAIN");

	public final boolean physical;
	public final boolean ignoreArmor;

    public static final float HEAT_MOD = 0.25f;

	private DamageType(boolean physical, boolean ignoreArmor)
	{
		this.physical = physical;
		this.ignoreArmor = ignoreArmor;
	}

	public void applyEffect(HitContext context)
	{
	}
}

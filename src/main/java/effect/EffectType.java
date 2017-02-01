package effect;

import core.Mechanics;
import creature.Attribute;
import creature.Creature;
import util.Utils;

import java.util.logging.Logger;

import static java.lang.String.format;
import static main.Delays.DEFAULT_DELAY;

/**
 * Enum for different effects types.
 *
 * @author Blind
 */
public enum EffectType {
    EMPTY_EFFECT {
        protected int findPower(int power, int damageDone) {
            throw new RuntimeException("Method call is not supported.");
        }

        protected int findDuration(int power, int damageDone) {
            throw new RuntimeException("Method call is not supported.");
        }
    },
    BLEED {
        protected void apply(Creature target, IPowerTimeEffect effect) {
            MAIN.fine(target.getName() + " is bleeding.");
        }

        protected void remove(Creature creature, IPowerTimeEffect effect) {
            MAIN.fine(creature.getName() + " bleeds for 1 damage.");
            creature.applyDamage(null, 1);
            if (effect.getPower() > 0) {
                new TimePowerEffect(this, effect.getPower() - 1, effect.getDuration()).apply(creature);
            }
        }

        protected int findPower(int power, int damageDone) {
            return Mechanics.normal(power + damageDone * 4 / Mechanics.DEFAULT_DAMAGE);
        }

        protected int findDuration(int power, int damageDone) {
            return DEFAULT_DELAY.amount;
        }
    },
    CRUSH {
        protected int findDuration(int power, int damageDone) {
            return Mechanics.BASE_CRUSH_DURATION + DEFAULT_DELAY.amount * power;
        }

        protected void apply(Creature creature, IPowerTimeEffect effect) {
            MAIN.fine(creature.getName() + " was crushed.");
            creature.addAttribute(Attribute.ARMOR, -effect.getPower());
        }

        protected void remove(Creature creature, IPowerTimeEffect effect) {
            creature.addAttribute(Attribute.ARMOR, effect.getPower());
        }

        protected int findPower(int power, int damageDone) {
            return Utils.roll(power) + 1;
        }
    },
    POISON {
        protected void apply(Creature creature, IPowerTimeEffect effect) {
            MAIN.fine(creature.getName() + " is poisoned.");
        }

        protected int findDuration(int power, int damageDone) {
            return DEFAULT_DELAY.amount;
        }

        protected void remove(Creature creature, IPowerTimeEffect effect) {
            MAIN.fine(creature.getName() + " loses 1 health to posion.");
            creature.applyDamage(null, 1);
            if (effect.getPower() > 0) {
                new TimePowerEffect(this, effect.getPower() - 1, effect.getDuration()).apply(creature);
            }
        }

        protected int findPower(int power, int damageDone) {
            return Mechanics.normal(power * Mechanics.DEFAULT_HEALTH / 10);
        }
    },
    STUN {
        protected void apply(Creature creature, IPowerTimeEffect effect) {
            MAIN.fine(creature.getName() + " is stunned: power = " + effect.getPower() + ", duration " + effect.getDuration());
            creature.addAttribute(Attribute.MELEE, -effect.getPower());
            creature.addAttribute(Attribute.RANGED, -effect.getPower());
            creature.addAttribute(Attribute.DEFENCE, -effect.getPower());
        }

        protected void remove(Creature creature, IPowerTimeEffect effect) {
            creature.addAttribute(Attribute.MELEE, effect.getPower());
            creature.addAttribute(Attribute.RANGED, effect.getPower());
            creature.addAttribute(Attribute.DEFENCE, effect.getPower());
        }

        protected int findPower(int power, int damageDone) {
            return Mechanics.BASE_STUN_VALUE * power;
        }

        protected int findDuration(int power, int damageDone) {
            return Mechanics.BASE_STUN_DURATION + DEFAULT_DELAY.amount * power;
        }
    },
    HEAT {
        protected void apply(Creature creature, IPowerTimeEffect effect) {
            MAIN.fine(format("%s is heated: power = %s, duration %s", creature.getName(), effect.getPower(), effect.getDuration()));
        }

        protected int findPower(int power, int damageDone) {
            return power;
        }

        protected int findDuration(int power, int damageDone) {
            return power;
        }
    },
    DISABLED {
        protected void apply(Creature creature, IPowerTimeEffect effect) {
            MAIN.fine(format("%s can't move for %s", creature.getName(), effect.getDuration()));
        }

        protected void remove(Creature target, IPowerTimeEffect effect) {
            if (effect.getPower() > 0) {
                new TimePowerEffect(this, effect.getPower() - 1, effect.getDuration()).apply(target);
            }
        }

        protected int findPower(int power, int damageDone) {
            return power;
        }

        protected int findDuration(int power, int damageDone) {
            return power;
        }
    },
    PIN {
        protected void apply(Creature creature, IPowerTimeEffect effect) {
            MAIN.fine(format("%s is pinned for %s", creature.getName(), effect.getDuration()));
        }

        protected void remove(Creature target, IPowerTimeEffect effect) {
            if (effect.getPower() > 0) {
                new TimePowerEffect(this, effect.getPower() - 1, effect.getDuration()).apply(target);
            }
        }

        protected int findPower(int power, int damageDone) {
            return power;
        }

        protected int findDuration(int power, int damageDone) {
            return power;
        }
    };

    private static final Logger MAIN = Logger.getLogger("MAIN");

    public static final double PIN_SPEED_DELAY = 1.2;

    protected void apply(Creature target, IPowerTimeEffect effect) {
    }

    protected void remove(Creature target, IPowerTimeEffect effect) {
    }

    protected abstract int findPower(int power, int damageDone);

    protected abstract int findDuration(int power, int damageDone);

    public void effectOn(Creature creature, int power, int damageDone) {
        new TimePowerEffect(this, findPower(power, damageDone), findDuration(power, damageDone)).apply(creature);
    }

}

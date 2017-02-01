package creature;

import inventory.weapon.IWeapon;
import main.Delays;

import static main.Delays.DEFAULT_DELAY;

/**
 * Resolves attack/damage calculation logic.
 * 
 * @author Blind
 */
public final class HitResolver
{

    public static final int SKILL_DELAY_DECREASE = 2;

    public static int getDelay(Creature creature, IWeapon weapon)
	{
		final int delay = weapon == null ? DEFAULT_DELAY.amount : weapon.getDelay();
		final double speedFactor = creature.getSpeedFactor();
        final int fullDelay = (int) (delay * speedFactor);
        final int skillDelay = fullDelay - creature.getRank() / SKILL_DELAY_DECREASE;
        final int halfDelay = delay / 2;
		return Math.max(skillDelay < halfDelay ? halfDelay : skillDelay, Delays.MIN_ATTACK_DELAY.amount);
	}
}

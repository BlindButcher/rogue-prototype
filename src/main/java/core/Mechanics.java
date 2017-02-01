package core;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import creature.Creature;
import creature.HitContext;
import inventory.weapon.IWeapon;
import util.Utils;

import java.util.Map;
import java.util.logging.Logger;

import static creature.Attribute.PENETRATION;
import static creature.HitContext.Property.ATTACKER;
import static creature.HitContext.Property.ATTACK_PULSED;
import static inventory.weapon.WeaponProperty.PIERCE;
import static java.lang.String.format;
import static main.Delays.DEFAULT_DELAY;

public class Mechanics
{
	public static final int GROWTH_THRESHOLD = 100;

	public static final int TIER_LEVEL = 5;
	public static final int DEFAULT_HEALTH = 50;
	public static final int DEFAULT_DAMAGE = 20;
	public static final int DEFAULT_ARMOR = 8;

	public static final int DEFAULT_ATT_DEF = 10;
	public static final int DEFAULT_BLOCK = 6;

    public static final int PARRY_MODIFIER = 2;

	public static final int BASE_CRUSH_DURATION = 16 * DEFAULT_DELAY.amount;

	public static final int BASE_STUN_VALUE = 1;
	public static final int BASE_STUN_DURATION = 10 * DEFAULT_DELAY.amount;

	public static final int BASE_CRIT_MODIFIER = 125;
	public static final int CRIT_DAMAGE_MODIFIER = 10;
	public static final int CRIT_CHANCE_MODIFIER = 5;

	private static final int HIT_BASE = 60, HIT_LOW = 1, HIT_HIGH = 99;
	private static final int[] HIT_TABLE = { 5, 4, 4, 3, 3, 3, 2, 2, 2, 2 };

	private static final int CRITICAL_BASE = 5, CRITIAL_LOW = 1, CRITICAL_HIGH = 50;
	private static final int CRITICAL_FACTOR = 3;

	private static final double DEVIATION_FACTOR = 1.0 / 9;

    private static final int DECREASE_ATTACK_BY_DIST = 2;
    private static final int BASE_HALF_ARMOR_CHANCE = 5;

    private static final Logger LOG = Logger.getLogger(Mechanics.class.getName());
    public static final int PULSE_ARMOR_COEF = 2;
    public static final String ARMOR_MESSAGE = "Target armor: %s, isPulsed: %s, Pierce: %s, HalfArmorChange: %s, HasHalfArmored: %s, ArmorPointsLeft: %s";


    private static int limit(int value, int low, int high)
	{
		if (low > high) throw new IllegalArgumentException("Low greater than high: " + low + " > " + high);
		if (value > high) return high;
		if (value < low) return low;
		return value;
	}

    public static int hitChance(int attack, int defence) {
        int percentage = HIT_BASE;
        int d = attack > defence ? 1 : -1;
        int diff = Math.abs(attack - defence);
        for (int i = 0; i < diff && i < HIT_TABLE.length; i++)
        {
            percentage += d * HIT_TABLE[i];
        }
        percentage += d * Math.max(0, diff - HIT_TABLE.length);
        percentage = limit(percentage, HIT_LOW, HIT_HIGH);
        return percentage;
    }

    public static int calculateAttack(Creature creature, Creature target, IWeapon weapon) {
        int val = weapon.isRanged() ? creature.getRanged() : creature.getMelee();
        int dist = Utils.dist2(creature, target);
        Preconditions.checkArgument(dist >= 1);
        return weapon.getRangeInfluence().modifyAttack(weapon, val, dist);
    }

	public static boolean hit(int attack, int defence)
	{
		return Utils.chance(hitChance(attack, defence));
	}

	public static boolean critical(int attack, int defence, int bonus)
	{
		int percentage = CRITICAL_BASE;
		percentage += (attack - defence) / CRITICAL_FACTOR + bonus;
		percentage = limit(percentage, CRITIAL_LOW, CRITICAL_HIGH);

		return Utils.chance(percentage);
	}

	public static int normal(int value)
	{
		return (int) Math.max(0, Math.round(Utils.normal() * DEVIATION_FACTOR * value + value));
	}

    public static void damage(final HitContext context, final int damageAttr, final int damageMod) {
        context.put(HitContext.Property.DAMAGE_ROLLS, Maps.<DamageType, Integer> newHashMap());
        context.put(HitContext.Property.DAMAGE_DONE, Maps.<DamageType, Integer> newHashMap());
        final Map<DamageType, Integer> damageHolder = context.get(HitContext.Property.DAMAGE_ROLLS);
        final Map<DamageType, Integer> doneDamage = context.get(HitContext.Property.DAMAGE_DONE);

        final IWeapon weapon = context.getWeapon();
        final Creature target = context.getTarget();
        final Creature attacker = context.get(ATTACKER);

        final boolean isPulsed = context.get(ATTACK_PULSED);

        final int halfArmorChance = isPulsed ? 0 : BASE_HALF_ARMOR_CHANCE * weapon.getProperty(PIERCE) +
                attacker.getAttribute(PENETRATION);
        final boolean halfArmor = Utils.chance(halfArmorChance);
        final int armor = isPulsed ? target.getArmor() * PULSE_ARMOR_COEF : target.getArmor();
        int armorLeft = (halfArmor ? armor / 2 : armor);
        armorLeft = armorLeft < 0 ? 0 : armorLeft;

        LOG.fine(format(ARMOR_MESSAGE, armor, isPulsed, weapon.getProperty(PIERCE), halfArmorChance,
                halfArmor, armorLeft));

        Preconditions.checkArgument(weapon.getDamage().size() > 0);
        for (Map.Entry<DamageType, Integer> damage : weapon.getDamage().entrySet())
        {
            DamageType damageType = damage.getKey();

            final int value = damage.getValue() + (damage.getKey().physical
                    ? (int) Math.round(weapon.getBonusFactor() * damageAttr)
                    : 0);

            final int roll = Mechanics.normal(value * damageMod / 100);

            damageHolder.put(damageType, roll);
            damageType.applyEffect(context);

            final int reduction = !damageType.ignoreArmor && armorLeft > 0 ? Math.min(roll, armorLeft) : 0;

            armorLeft -= reduction;
            doneDamage.put(damageType, roll - reduction);
        }
    }

	public static boolean tryEffect(int power, int bonus)
	{
		if (power == 0) return false;

		int chance = bonus;
		if (power > 6)
		{
			chance += power - 6;
			power = 6;
		}
		chance += power * (power + 1) / 2;

		return Utils.chance(chance);
	}

}

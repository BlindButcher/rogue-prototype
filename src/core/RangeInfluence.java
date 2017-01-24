package core;

import inventory.weapon.IWeapon;
import inventory.weapon.WeaponProperty;

import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Contains values for different attack/damage modifications with range.
 *
 * @author Blind
 */
public enum RangeInfluence {
    DEFAULT {
        public int modifyAttack(IWeapon weapon, int attack, int dist) {
            int mod = dist == 1 ? 0 : -1 * (dist - 1) * DECREASE_ATTACK_BY_DIST;

            mod += (dist == 1 ? weapon.getProperty(WeaponProperty.CLOSE_RANGE_SHOT) : 0);
            mod += weapon.getProperty(WeaponProperty.ACCURATE);

            if (mod != 0) {
                LOG.fine(String.format("Attack was modified by %s", mod));
            }

            return attack + mod;
        }
    },
    SNIPER {
        public int modifyAttack(IWeapon weapon, int attack, int dist) {
            checkArgument(weapon.isRanged(), "Should be used only on range weapon.");
            return attack + (dist > weapon.getRange() / 3 ? DECREASE_ATTACK_BY_DIST : -DECREASE_ATTACK_BY_DIST);
        }
    };

    private static final Logger LOG = Logger.getLogger(RangeInfluence.class.getName());
    public static final int DECREASE_ATTACK_BY_DIST = 2;

    public abstract int modifyAttack(IWeapon weapon, int attack, int dist);

}

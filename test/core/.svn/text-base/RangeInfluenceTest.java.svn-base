package core;

import inventory.Material;
import inventory.weapon.IWeapon;
import inventory.weapon.Weapon;
import inventory.weapon.WeaponTemplate;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Tests RangeInfluence class.
 *
 * @author Blind
 */
public class RangeInfluenceTest
{
    @Test
    public void testAttackLoss() {
        IWeapon weapon = new Weapon(WeaponTemplate.UNARMED_RANGE, Material.NATURAL);


        assertEquals(10, RangeInfluence.DEFAULT.modifyAttack(weapon, 10, 1));
        assertEquals(8, RangeInfluence.DEFAULT.modifyAttack(weapon, 10, 2));

        assertEquals(10 - RangeInfluence.DECREASE_ATTACK_BY_DIST, RangeInfluence.SNIPER.modifyAttack(weapon, 10, 1));
        assertEquals(10 + RangeInfluence.DECREASE_ATTACK_BY_DIST, RangeInfluence.SNIPER.modifyAttack(weapon, 10, 6));
    }
}

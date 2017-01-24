package equipment;

import creature.Creature;
import creature.CreatureTemplate;
import inventory.EquipmentSlot;
import inventory.Material;
import inventory.shield.Shield;
import inventory.weapon.IWeapon;
import inventory.weapon.WeaponBase;
import inventory.weapon.WeaponTemplate;
import main.AbstractInjectionTest;
import org.junit.Test;

import static com.google.common.collect.Iterables.find;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests equip and un equip items.
 *
 * @author Blind
 */
public class EquipmentTest extends AbstractInjectionTest {

    @Test
    public void testEquip() {
        CreatureTemplate dummy = getDummy();
        Creature creature = new Creature(dummy);
        IWeapon weapon = builderFactory.createWeaponBuilder().setBase(WeaponTemplate.UNARMED).construct();
        creature.forceEquip(weapon);
        assertNotNull(creature.getEquiped(EquipmentSlot.MAIN_HAND));
        Shield shield = new Shield(entityLoader.findShield("BUCKLER"), Material.NATURAL);
        creature.forceEquip(shield);
        WeaponBase twoHanded = find(entityLoader.weaponBase(), WeaponBase::isTwoHanded);

        weapon = builderFactory.createWeaponBuilder().setBase(twoHanded).construct();
        System.out.println("Equip weapon: " + weapon);
        creature.forceEquip(weapon);
        assertNotNull(creature.getEquiped(EquipmentSlot.MAIN_HAND));
        assertNull(creature.getEquiped(EquipmentSlot.OFF_HAND));

        creature.forceEquip(shield);
        assertNull(creature.getEquiped(EquipmentSlot.MAIN_HAND));
        assertNotNull(creature.getEquiped(EquipmentSlot.OFF_HAND));
        creature.forceEquip(shield);
    }
}

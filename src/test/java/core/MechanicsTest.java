package core;

import creature.Attribute;
import creature.Creature;
import creature.HitContext;
import inventory.EquipmentSlot;
import inventory.Material;
import inventory.armor.ArmorType;
import inventory.weapon.IWeapon;
import inventory.weapon.Weapon;
import inventory.weapon.WeaponTemplate;
import main.AbstractInjectionTest;
import org.junit.Test;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test mechanics class.
 *
 * @author Blind
 */
public class MechanicsTest extends AbstractInjectionTest
{
      public static final int DAMAGE_DONE = 10;

      @Test
      public void testDamageCalculation() {
          Creature c = new Creature(getDummy());
          Creature target = new Creature(getDummy());
          c.equip(new Weapon(WeaponTemplate.UNARMED, Material.NATURAL), EquipmentSlot.MAIN_HAND);
          HitContext context = new HitContext((IWeapon) c.getEquiped(EquipmentSlot.MAIN_HAND), target);
          context.put(HitContext.Property.ATTACKER, c);
          context.put(HitContext.Property.ATTACK_PULSED, false);
          Mechanics.damage(context, c.getAttribute(Attribute.DAMAGE), 100);
          assertTrue(context.damageDone() > 0);

          context = new HitContext((IWeapon) c.getEquiped(EquipmentSlot.MAIN_HAND), target);
          Map<DamageType, Integer> damages = newHashMap();
          damages.put(DamageType.PHYSICAL, DAMAGE_DONE);
          context.put(HitContext.Property.DAMAGE_DONE, damages);
          ArmorType.COMMON.armorEffect(context);
          assertEquals(DAMAGE_DONE - DAMAGE_DONE / ArmorType.PER_CENT_REDUCE, context.damageDone());
      }
}

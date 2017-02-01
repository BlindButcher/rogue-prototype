package creature;

import inventory.ItemFactory;
import inventory.Material;
import inventory.weapon.IWeapon;
import inventory.weapon.WeaponBase;
import inventory.weapon.WeaponProperty;
import inventory.weapon.WeaponPropertyEnhance;
import loader.EntityLoader;
import main.AbstractInjectionTest;
import main.EventHolder;
import org.junit.Before;
import org.junit.Test;
import util.Point;

import static com.google.common.collect.Iterables.find;
import static effect.EffectType.BLEED;
import static org.junit.Assert.assertTrue;

/**
 * Tests effect applying to creature.
 *
 * @author Blind
 */
public class EffectTest extends AbstractInjectionTest
{
    private static final int TRIES = 10;


    private EventHolder eventHolder;
    private ItemFactory itemFactory;

    @Before
    public void setUp()
    {
        this.eventHolder = injector.getInstance(EventHolder.class);
        itemFactory = injector.getInstance(ItemFactory.class);

    }

    @Test
    public void testSlash()
    {
        final WeaponBase slashWeapon = find(injector.getInstance(EntityLoader.class).weaponBase(), weaponBase ->
                weaponBase.hasProperty(WeaponProperty.SLASH));

        Material justSome = itemFactory.chooseMaterial(slashWeapon.getMaterialType(), 1);

        Creature attacker = new Creature(getDummy());
        attacker.setXY(Point.point(0, 0));
        attacker.addAttribute(Attribute.MELEE, 50);
        attacker.addAttribute(Attribute.DAMAGE, 50);

        Creature defender = new Creature(getDummy());
        defender.setXY(Point.point(1, 0));
        attacker.setEventHolder(eventHolder);
        defender.setEventHolder(eventHolder);

        boolean found = false;
        for (int i = 0; i < TRIES && !found; i++)
        {
            IWeapon weapon = builderFactory.createWeaponBuilder().setBase(slashWeapon)
                .setMaterial(justSome).setPropertyEnhance(WeaponPropertyEnhance.SLASHING).construct();
            attacker.hitTarget(defender, weapon);

            found = defender.getActiveEffects().stream().allMatch(iEffect -> iEffect.getType() == BLEED);
        }

        assertTrue(found);
    }

}

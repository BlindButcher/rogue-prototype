package core;

import creature.Creature;
import inventory.weapon.Weapon;
import inventory.weapon.WeaponProperty;
import main.AbstractInjectionTest;
import main.EventHolder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static effect.EffectType.HEAT;
import static org.junit.Assert.assertTrue;

/**
 * Tests different damage types.
 *
 * @author Blind
 */
@Ignore     // TODO modify due to weapon modifications
public class DamageTypeTest extends AbstractInjectionTest
{
    private EventHolder eventHolder;

    @Before
    public void setUp()
    {
        this.eventHolder = injector.getInstance(EventHolder.class);
    }

    @Test
    public void testHeat()
    {
        Creature creature = new Creature(getDummy());
        Creature target = new Creature(getDummy());
        creature.setEventHolder(eventHolder);
        target.setEventHolder(eventHolder);
        Weapon weapon = new Weapon(entityLoader.findWeapon("LASER_RIFLE"), entityLoader.findMaterial("ABS"));
        weapon.getProperties().put(WeaponProperty.AUTO_HIT, 1);
        weapon.getDamage().put(DamageType.HEAT, 1000);
        creature.hitTarget(target, weapon);
        assertTrue(target.getActiveEffects().stream().filter(e -> e.getType() == HEAT).findFirst().isPresent());
    }
}

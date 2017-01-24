package loader;

import creature.CreatureTemplate;
import inventory.weapon.WeaponBase;
import junit.framework.Assert;
import main.AbstractInjectionTest;
import org.junit.Test;

import java.util.Set;

import static com.google.common.collect.Iterables.all;
import static com.google.common.collect.Iterables.find;
import static core.RangeInfluence.SNIPER;
import static org.junit.Assert.assertTrue;

/**
 * Tests creature template class.
 * 
 * @author Blind
 */
public class EntityLoaderTest extends AbstractInjectionTest
{

	@Test
	public void testCreatureTemplate()
	{
		Set<CreatureTemplate> templateSet = entityLoader.templates();

		CreatureTemplate rat = find(templateSet, creatureTemplate -> creatureTemplate.getId().equals("RAT"));

		Assert.assertNotNull(rat);
		Assert.assertNotNull(rat.getId());
		Assert.assertNotNull(rat.getName());
		Assert.assertNotNull(rat.getGlyph());

		CreatureTemplate goblin = find(templateSet, creatureTemplate -> creatureTemplate.getId().equals("GOBLIN"));

		Assert.assertFalse(goblin.getArmors().isEmpty());
		Assert.assertNotNull(goblin.getPredefinedWeapon());
		Assert.assertFalse(goblin.getUnifiedAttributes().isEmpty());

		CreatureTemplate uruk = find(templateSet, creatureTemplate -> creatureTemplate.getId().equals("URUK"));

		Assert.assertTrue(uruk.isBoss());

		CreatureTemplate shielded = find(templateSet, creatureTemplate -> creatureTemplate.getId().equals("ORC_SERGENT"));

		Assert.assertFalse(shielded.getShields().isEmpty());
	}

	@Test
	public void testItems()
	{
		Set<WeaponBase> weaponBase = entityLoader.weaponBase();
		Assert.assertFalse(weaponBase.isEmpty());

		WeaponBase bow = find(weaponBase, w -> w.getId().equals("MACHINE_GUN"));

		Assert.assertTrue(bow.isRanged());

		Assert.assertFalse(entityLoader.armorTemplates().isEmpty());

        assertTrue(all(entityLoader.shieldBases(), shieldBase -> shieldBase.getBlockValue() > 0)) ;

        assertTrue(weaponBase.stream().filter(b -> b.getId().equals("SNIPER_RIFLE")).
            findAny().get().getRangeInfluence() == SNIPER);
	}

	@Test
	public void testMaps()
	{
		Assert.assertFalse(entityLoader.mapBase().isEmpty());
	}

    @Test
    public void testArmorQualities()
    {
        Assert.assertFalse(entityLoader.armorQualitySet().isEmpty());
    }
}

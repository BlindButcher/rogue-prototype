package inventory.weapon;

import core.CoverageArea;
import core.DamageType;
import core.RangeInfluence;
import inventory.Equipable;

import java.util.Map;
import java.util.SortedMap;

public interface IWeapon extends Equipable
{
	String getName();

	SortedMap<DamageType, Integer> getDamage();

	int getDelay();

	double getBonusFactor();

	boolean isRanged();

	int getRange();

	int getProperty(WeaponProperty property);

	boolean hasProperty(WeaponProperty property);

	WeaponBase getBase();

    Map<WeaponProperty, Integer> getProperties();

    CoverageArea getCoverageArea();

    RangeInfluence getRangeInfluence();
}

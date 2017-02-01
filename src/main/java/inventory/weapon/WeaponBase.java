package inventory.weapon;

import core.CoverageArea;
import core.DamageType;
import core.RangeInfluence;
import inventory.ItemBase;
import inventory.MaterialType;

import java.util.Map;

public interface WeaponBase extends ItemBase
{
	int getDelay();

	double getBonusFactor();

	boolean isTwoHanded();

	int getRange();

	boolean isRanged();

	MaterialType getMaterialType();

	Map<WeaponProperty, Integer> getProperties();

	boolean hasProperty(WeaponProperty property);

	CoverageArea getCoverageArea();

    Map<DamageType, Integer> getDamageMap();

    RangeInfluence getRangeInfluence();
}

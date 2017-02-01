package inventory.armor;

import inventory.ItemBase;
import inventory.MaterialType;

public interface ArmorBase extends ItemBase
{
	int getArmor();

	double getSpeedFactor();
	
	int getCombatPenalty();

	MaterialType getMaterialType();

    ArmorType getArmorType();

}

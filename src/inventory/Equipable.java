package inventory;

import ability.Ability;

import java.util.List;

public interface Equipable extends Item
{
	List<Ability> getAbilities();

	EquipStrategy getEquipmentStrategy();

    void setName(String name);

    Material getMaterial();
}

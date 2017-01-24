package inventory;

import com.google.common.base.Preconditions;
import creature.Creature;
import inventory.weapon.Weapon;

public enum EquipStrategy
{
	ONE_HANDED
	{
		@Override
		public void equip(Creature creature, Equipable item)
		{
			equip(creature, item, EquipmentSlot.MAIN_HAND);
		}

		@Override
		public void equip(Creature creature, Equipable item, EquipmentSlot slot)
		{
			Preconditions.checkArgument(EquipmentSlot.MAIN_HAND.equals(slot)
					|| EquipmentSlot.OFF_HAND.equals(slot));

			if (EquipmentSlot.OFF_HAND.equals(slot) && checkTwoHanded(creature))
			{
				creature.unequip(EquipmentSlot.MAIN_HAND);
			}
			else
			{
				creature.unequip(slot);
			}

			creature.equip(item, slot);
		}
	},
	TWO_HANDED
	{
		@Override
		public void equip(Creature creature, Equipable item)
		{
			creature.unequip(EquipmentSlot.MAIN_HAND);
			creature.unequip(EquipmentSlot.OFF_HAND);

			creature.equip(item, EquipmentSlot.MAIN_HAND);
		}

		@Override
		public void equip(Creature creature, Equipable item, EquipmentSlot slot)
		{
			throw new RuntimeException("Should be called.");
		}
	},
	SHIELD
	{
		@Override
		public void equip(Creature creature, Equipable item)
		{
			if (checkTwoHanded(creature))
			{
				creature.unequip(EquipmentSlot.MAIN_HAND);
			}
			else
			{
				creature.unequip(EquipmentSlot.OFF_HAND);
			}

			creature.equip(item, EquipmentSlot.OFF_HAND);
		}

		@Override
		public void equip(Creature creature, Equipable item, EquipmentSlot slot)
		{
			throw new RuntimeException("Should be called.");
		}
	},
    MISC {
        public void equip(Creature creature, Equipable item)
        {
            creature.equip(item, EquipmentSlot.MISC);
        }

        public void equip(Creature creature, Equipable item, EquipmentSlot slot)
        {
        }
    },
	ARMOR
	{
		@Override
		public void equip(Creature creature, Equipable item)
		{
			creature.unequip(EquipmentSlot.TORSO);

			creature.equip(item, EquipmentSlot.TORSO);
		}

		@Override
		public void equip(Creature creature, Equipable item, EquipmentSlot slot)
		{
			throw new RuntimeException("Should be called.");
		}
	};

	abstract public void equip(Creature creature, Equipable item);

	abstract public void equip(Creature creature, Equipable item, EquipmentSlot slot);

	static protected boolean checkTwoHanded(Creature creature)
	{
		Weapon item = (Weapon) creature.getEquiped(EquipmentSlot.MAIN_HAND);
		return item != null && item.isTwoHanded();
	}
}

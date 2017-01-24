package view;

import creature.Attribute;
import creature.Creature;
import creature.HitResolver;
import effect.AbstractTimedEffect;
import inventory.Equipable;
import inventory.EquipmentSlot;
import inventory.weapon.IWeapon;

import java.util.List;

import static ability.AbilityType.MELEE_ATTACK;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

/**
 * Creature presenter, helps rendering creature on screen.
 * 
 * @author Blind
 */
public class CreaturePresenter
{
	private Creature creature;

	public CreaturePresenter(Creature creature)
	{
		this.creature = creature;
	}

	public List<String> creatureInfo()
	{
		List<String> creatureInfo = newArrayList();

		creatureInfo.add(creature.getName());
		creatureInfo.add("Rank: " + creature.getRank());
        creatureInfo.add("Health: " + getHealth());
		Equipable mainHand = creature.getEquiped(EquipmentSlot.MAIN_HAND);
		if (mainHand != null)
		{
			creatureInfo.add("Main hand: " + mainHand.getName());
		}

		Equipable offHand = creature.getEquiped(EquipmentSlot.OFF_HAND);
		if (offHand != null)
		{
			creatureInfo.add("Off hand: " + offHand.getName());
		}

		Equipable torso = creature.getEquiped(EquipmentSlot.TORSO);
		if (torso != null)
		{
			creatureInfo.add("Torso: " + creature.getEquiped(EquipmentSlot.TORSO).getName());
		}

		creatureInfo.add("Melee: " + creature.getMelee());
		creatureInfo.add("Ranged: " + creature.getRanged());
		creatureInfo.add("Defence: " + creature.getDefence());
		creatureInfo.add("Damage: " + ((IWeapon) creature.getAbilitySource(MELEE_ATTACK)).getDamage().entrySet().iterator().next().getValue());
		creatureInfo.add("Armor: " + creature.getArmor());
		creatureInfo.add("Speed: " + (int) (creature.getSpeedFactor() * 100) + "%");
		creatureInfo.add("Att.delay: " + HitResolver.getDelay(creature, (IWeapon) creature.getAbilitySource(MELEE_ATTACK)));
		creatureInfo.add("Mov.delay: " + creature.getMoveDelay());
		return creatureInfo;
	}

    public List<String> effects() {
        return creature.getActiveEffects().stream().
                map(e -> format("Effect: %s, Duration: %s", e.getType().name(), e instanceof AbstractTimedEffect ? ((AbstractTimedEffect) e).getDuration() : "N/A")).collect(toList());
    }

    public List<String> creatureStats() {
        return creature.getStat().getMap().keySet().stream().
                map(type -> format("%s: %s", type.desc, creature.getStat().getMap().get(type))).collect(toList());
    }

	public String baseAttributes()
	{
		StringBuilder sb = new StringBuilder();
		for (Attribute a : Attribute.values())
		{
			sb.append(a.name()).append(":").append(creature.getAttribute(a)).append(";");
		}
		return sb.toString();
	}

    public String getHealth()
    {
        return  creature.getCurHp() + "/" + creature.getMaxHp();
    }

	public Creature creature()
	{
		return creature;
	}
}

package inventory.weapon;

import core.DamageType;

import static core.DamageType.PHYSICAL;

public enum WeaponDamageEnhance
{
	SEVERE("Severe", PHYSICAL),
	FLAMING("Flaming", DamageType.FIRE),
	FREEZING("Freezing", DamageType.COLD),
	ELECTRIC("Electric", DamageType.ELECTRIC);

	private final String name;
	private final DamageType damageType;

	private WeaponDamageEnhance(String name, DamageType damageType)
	{
		this.name = name;
		this.damageType = damageType;
	}

    public String getName() {
        return name;
    }

    public DamageType getDamageType() {
        return damageType;
    }

	public boolean fits(WeaponBase weapon)
	{
		return weapon.getDamageMap().containsKey(PHYSICAL) && !weapon.getMaterialType().isNatural();
	}

}

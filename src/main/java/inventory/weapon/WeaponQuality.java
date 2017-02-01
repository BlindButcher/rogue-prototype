package inventory.weapon;

public enum WeaponQuality
{
	OLD("Old", -1, -1, -1),
	MASTERWORK("Masterwork", 1, 0, 2),
	BALANCED("Balanced", 0, 1, 2),
	LIGHT("Light", -1, 1, 1),
	HEAVY("Heavy", 1, -1, 1);

	private final String namePrefix;
	private final int damageBonus, delayBonus;

	private WeaponQuality(String namePrefix, int damageBonus, int delayBonus, int levelModifier)
	{
		this.namePrefix = namePrefix;
		this.damageBonus = damageBonus;
		this.delayBonus = delayBonus;
	}

    public String getNamePrefix() {
        return namePrefix;
    }

    public int getDamageBonus() {
        return damageBonus;
    }

    public int getDelayBonus() {
        return delayBonus;
    }

    public boolean fits(WeaponBase weapon)
    {
        return !weapon.getMaterialType().isNatural();
    }
}

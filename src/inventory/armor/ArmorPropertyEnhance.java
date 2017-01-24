package inventory.armor;

import ability.AbilityType;

/**
 * Armor property enhancer.
 *
 * @author Blind
 */
public enum ArmorPropertyEnhance
{
    SPIKED(ArmorProperty.SPIKED, "Spiked", 4),
    PULSE(ArmorProperty.PULSE, "Pulsing", 0, AbilityType.PULSE);

    public ArmorProperty armorProperty;
    public AbilityType abilityType;
    public String namePrefix;
    public int value;

    private ArmorPropertyEnhance(ArmorProperty property, String namePrefix, int value)
    {
        this.namePrefix = namePrefix;
        this.armorProperty = property;
        this.value = value;
    }

    private ArmorPropertyEnhance(ArmorProperty property, String namePrefix, int value, AbilityType abilityType)
    {
        this.namePrefix = namePrefix;
        this.armorProperty = property;
        this.value = value;
        this.abilityType = abilityType;
    }

}

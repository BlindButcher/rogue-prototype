package inventory.armor;

import ability.Ability;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import core.Mechanics;
import inventory.Material;
import loader.EntityLoader;

/**
 * Constructs armor from template.
 *
 * @author Blind
 */
public class ArmorModificationService
{
    private EntityLoader loader;

    public IArmor create(ArmorBase armorBase) {
        Material material = loader.lowTierForType(armorBase.getMaterialType());
        IArmor armor = new Armor(armorBase, material);
        Preconditions.checkState(armorBase.getMaterialType().equals(material.getMaterialType()));
        return withMaterial(armor, material);
    }

    public IArmor create(String armorBase) {
        final ArmorTemplate armor = loader.findArmor(armorBase);
        return new Armor(armor, loader.lowTierForType(armor.getMaterialType()));
    }

    public IArmor create(ArmorBase base, Material material, ArmorQuality quality)
    {
        IArmor armor = create(base);
        armor = withMaterial(armor, material);
        if (quality != null) armor = withQuality(armor, quality);
        return armor;
    }

    public IArmor withMaterial(IArmor armor, Material material)
    {
        int tier = material.getTier();
        int armorValue = Mechanics.DEFAULT_ARMOR / 4 * (tier - 1) + armor.getArmor();
        int combatPenalty = (tier - 1) + armor.getCombatPenalty();
        Armor result = new Armor(armor.getBase(), material, armorValue, armor.getSpeedFactor(), combatPenalty);
        result.setName(armor.getName());
        return result;
    }

    public IArmor withQuality(IArmor armor, ArmorQuality quality)
    {
        int tier = armor.getMaterial().getTier();
        int armorValue = quality.armorBonus * tier + armor.getArmor();
        double speedFactoryBonus = quality.speedFactorBonus * tier + armor.getSpeedFactor();
        int penalty =quality.combatPenalty * tier + armor.getCombatPenalty();
        Armor result = new Armor(armor.getBase(), armor.getMaterial(), armorValue, speedFactoryBonus, penalty);
        result.setName(quality.namePrefix + " " + armor.getName());
        return result;
    }

    public IArmor addProperty(IArmor armor, ArmorPropertyEnhance property)
    {
        int tier = armor.getMaterial().getTier();

        int value = armor.getPropertyPower(property.armorProperty) + tier + property.value;

        Armor result = new Armor(armor, property.armorProperty, value);
        result.setName(property.namePrefix + " " + armor.getName());

        if (property.abilityType != null)
            result.getAbilities().add(new Ability(property.abilityType, result));
        return result;
    }

    @Inject
    public void setLoader(EntityLoader loader)
    {
        this.loader = loader;
    }
}

package inventory.armor;

import inventory.BasedEquipable;
import inventory.EquipStrategy;
import inventory.Material;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

public class Armor extends BasedEquipable<ArmorBase> implements IArmor {
    private final Material material;

    private final int armor;
    private final double speedFactor;
    private final int combatPenalty;
    private Map<ArmorProperty, Integer> properties = newHashMap();

    public Armor(ArmorBase base, Material material) {
        super(base);
        checkNotNull(material);
        this.material = material;
        this.name = getBase().getName();
        this.armor = getBase().getArmor();
        this.speedFactor = getBase().getSpeedFactor();
        this.combatPenalty = getBase().getCombatPenalty();
    }

    public Armor(ArmorBase base, Material material, int armor, double speedFactor, int combatPenalty) {
        super(base);
        this.material = material;
        this.armor = armor;
        this.speedFactor = speedFactor;
        this.combatPenalty = combatPenalty;
    }

    public Armor(IArmor armor, ArmorProperty property, Integer value) {
        this(armor.getBase(), armor.getMaterial(), armor.getArmor(), armor.getSpeedFactor(), armor.getCombatPenalty());
        properties.put(property, value);
    }

    public int getArmor() {
        return armor;
    }

    public double getSpeedFactor() {
        return speedFactor;
    }

    public int getCombatPenalty() {
        return combatPenalty;
    }

    public EquipStrategy getEquipmentStrategy() {
        return EquipStrategy.ARMOR;
    }

    public String getImagePath() {
        return getBase().getImagePath();
    }

    public String getId() {
        return getBase().getId();
    }

    public Material getMaterial() {
        return material;
    }

    public boolean hasProperty(ArmorProperty property) {
        return properties.containsKey(property);
    }

    public int getPropertyPower(ArmorProperty property) {
        return !properties.containsKey(property) ? 0 : properties.get(property);
    }
}

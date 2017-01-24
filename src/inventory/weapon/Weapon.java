package inventory.weapon;

import ability.Ability;
import ability.AbilityType;
import core.CoverageArea;
import core.DamageType;
import core.RangeInfluence;
import inventory.BasedEquipable;
import inventory.EquipStrategy;
import inventory.Material;

import java.util.*;

import static ability.AbilityType.MELEE_ATTACK;
import static ability.AbilityType.RANGE_ATTACK;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newTreeMap;
import static inventory.weapon.WeaponProperty.PARRY;
import static java.util.Collections.unmodifiableSortedMap;

public class Weapon extends BasedEquipable<WeaponBase> implements IWeapon {
    private final Material material;
    private final CoverageArea coverageArea;
    private final RangeInfluence rangeInfluence;
    private final SortedMap<DamageType, Integer> damage;
    private int delay;

    private final Map<WeaponProperty, Integer> properties;

    public Weapon(WeaponBase base, Material material) {
        super(base);
        Objects.requireNonNull(material);

        this.name = getBase().getName();

        SortedMap<DamageType, Integer> damageMap = new TreeMap<>();
        for (DamageType type : base.getDamageMap().keySet())
            damageMap.put(type, base.getDamageMap().get(type));
        this.damage = unmodifiableSortedMap(damageMap);

        this.delay = getBase().getDelay();
        this.rangeInfluence = getBase().getRangeInfluence();
        this.properties = newHashMap(base.getProperties());
        this.material = material;
        this.coverageArea = base.getCoverageArea();
    }

    public Weapon(WeaponBase base, Material material, String name, SortedMap<DamageType, Integer> damage, int delay, Map<WeaponProperty, Integer> properties) {
        super(base);
        this.material = material;
        this.coverageArea = base.getCoverageArea();
        this.rangeInfluence = base.getRangeInfluence();
        this.damage = unmodifiableSortedMap(damage);
        this.delay = delay;
        this.properties = newHashMap(properties);
        this.name = name;
    }

    @Override public List<Ability> getAbilities() {
        List<Ability> abilities = newArrayList();
        if (hasProperty(PARRY)) abilities.add(new Ability(AbilityType.PARRY, this));
        abilities.add(new Ability(getBase().isRanged() ? RANGE_ATTACK : MELEE_ATTACK, this));
        return abilities;
    }

    @Override
    public SortedMap<DamageType, Integer> getDamage() {
        return newTreeMap(damage);
    }

    @Override
    public int getDelay() {
        return delay;
    }

    @Override
    public double getBonusFactor() {
        return getBase().getBonusFactor();
    }

    public boolean isTwoHanded() {
        return getBase().isTwoHanded();
    }

    @Override
    public boolean isRanged() {
        return getBase().isRanged();
    }

    @Override
    public int getRange() {
        return getBase().getRange();
    }

    @Override
    public int getProperty(WeaponProperty property) {
        return hasProperty(property) ? getProperties().get(property) : 0;
    }

    @Override
    public boolean hasProperty(WeaponProperty property) {
        return getProperties().containsKey(property);
    }

    @Override
    public WeaponBase getBase() {
        return super.getBase();
    }

    @Override public Map<WeaponProperty, Integer> getProperties() {
        return newHashMap(properties);
    }

    @Override
    public EquipStrategy getEquipmentStrategy() {
        return isTwoHanded() ? EquipStrategy.TWO_HANDED : EquipStrategy.ONE_HANDED;
    }

    @Override public Material getMaterial() {
        return material;
    }

    @Override
    public CoverageArea getCoverageArea() {
        return coverageArea;
    }

    @Override
    public RangeInfluence getRangeInfluence() {
        return rangeInfluence;
    }

    @Override
    public String getImagePath() {
        return getBase().getImagePath();
    }


    @Override
    public String getId() {
        return getBase().getId();
    }
}

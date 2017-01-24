package inventory.weapon;

import core.DamageType;
import core.Mechanics;
import inventory.Material;
import loader.EntityLoader;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static core.DamageType.PHYSICAL;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * Constructs weapon base on criteria set.
 *
 * @author Blind
 */
public class WeaponBuilder {
    private EntityLoader loader;

    private WeaponBase base;
    private Optional<Material> material = empty();
    private Optional<WeaponQuality> quality = empty();
    private Optional<WeaponPropertyEnhance> propertyEnhance = empty();
    private Optional<WeaponDamageEnhance> damageEnhance = empty();

    public IWeapon construct() {
        Objects.requireNonNull(base);

        SortedMap<DamageType, Integer> damageMap = new TreeMap<>();
        base.getDamageMap().forEach(damageMap::put);
        checkArgument(base.getDamageMap().size() == damageMap.size());

        int delay = base.getDelay();
        Map<WeaponProperty, Integer> properties = base.getProperties();

        if (!material.isPresent()) {
            material = of(loader.lowTierForType(base.getMaterialType()));
        }

        materialMod(damageMap);

        if (quality.isPresent() && quality.get().fits(base)) {
            delay = qualityMod(damageMap, delay);
        }

        if (propertyEnhance.isPresent() && propertyEnhance.get().fits(base))
            properties.computeIfPresent(propertyEnhance.get().getProperty(), (t, u) -> u + material.get().getTier());

        if (damageEnhance.isPresent() && damageEnhance.get().fits(base)) {
            damageMod(damageMap);
        }
        return new Weapon(base, material.get(), createName(), damageMap, delay, properties);
    }

    private String createName() {
        String name = material.get().getName() + " " + base.getName();
        if (quality.isPresent()) name = quality.get().getNamePrefix() + " " + name;
        if (propertyEnhance.isPresent()) name = propertyEnhance.get().isSuffix() ? name + " " +
            propertyEnhance.get().getName() : propertyEnhance.get().getName() + " " + name;
        if (damageEnhance.isPresent()) name =  damageEnhance.get().getName() + " " + name;
        return name;
    }

    private void damageMod(SortedMap<DamageType, Integer> damageMap) {
        damageMap.put(PHYSICAL, damageMap.get(PHYSICAL) + Mechanics.DEFAULT_DAMAGE * material.get().getTier() / 10);
        if (!damageEnhance.get().getDamageType().physical) {
            int dam = damageMap.get(PHYSICAL);
            int old = damageMap.getOrDefault(damageEnhance.get().getDamageType(), 0);
            damageMap.put(PHYSICAL, dam - dam / 2);
            damageMap.put(damageEnhance.get().getDamageType(), old + dam / 2);
        }
    }

    private int qualityMod(SortedMap<DamageType, Integer> damageMap, int delay) {
        Integer oldDamage = damageMap.get(DamageType.PHYSICAL);
        if (oldDamage != null)
        {
            damageMap.put(DamageType.PHYSICAL, oldDamage + quality.get().getDamageBonus() * material.get().getTier());
        }

        delay += quality.get().getDelayBonus() * material.get().getTier();
        return delay;
    }

    private void materialMod(SortedMap<DamageType, Integer> damageMap) {
        int tier = material.get().getTier();
        Integer oldDamage = damageMap.get(DamageType.PHYSICAL);
        if (oldDamage != null) {
            damageMap.put(DamageType.PHYSICAL, Mechanics.DEFAULT_DAMAGE / 4 * (tier - 1) + oldDamage);
        }
    }

    public WeaponBuilder setBase(WeaponBase base) {
        this.base = base;
        return this;
    }

    public WeaponBuilder setMaterial(Material material) {
        this.material = of(material);
        return this;
    }

    public WeaponBuilder setQuality(WeaponQuality quality) {
        this.quality = of(quality);
        return this;
    }

    public WeaponBuilder setPropertyEnhance(WeaponPropertyEnhance propertyEnhance) {
        this.propertyEnhance = of(propertyEnhance);
        return this;
    }

    public WeaponBuilder setDamageEnhance(WeaponDamageEnhance damageEnhance) {
        this.damageEnhance = of(damageEnhance);
        return this;
    }

    public WeaponBuilder setQuality(Optional<WeaponQuality> quality) {
        this.quality = quality;
        return this;
    }

    public WeaponBuilder setPropertyEnhance(Optional<WeaponPropertyEnhance> propertyEnhance) {
        this.propertyEnhance = propertyEnhance;
        return this;
    }

    public WeaponBuilder setDamageEnhance(Optional<WeaponDamageEnhance> damageEnhance) {
        this.damageEnhance = damageEnhance;
        return this;
    }

    public void setLoader(EntityLoader loader) {
        this.loader = loader;
    }
}

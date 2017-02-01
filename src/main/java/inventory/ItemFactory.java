package inventory;

import com.google.inject.Inject;
import core.Mechanics;
import inventory.armor.*;
import inventory.shield.Shield;
import inventory.shield.ShieldBase;
import inventory.weapon.*;
import loader.EntityLoader;
import util.RandomList;
import util.Utils;

import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static util.RandomList.randomItem;
import static util.Utils.roll;

public class ItemFactory {
    private EntityLoader entityLoader;
    private RandomList<WeaponPropertyEnhance> weaponEnhances = new RandomList<>(asList(WeaponPropertyEnhance.values()));
    private RandomList<WeaponDamageEnhance> weaponDamage = new RandomList<>(asList(WeaponDamageEnhance.values()));
    private ArmorModificationService armorModificationService;
    private BuilderFactory builderFactory;
    private RandomList<ArmorPropertyEnhance> armorPropertyEnhances = new RandomList<>(asList(ArmorPropertyEnhance.values()));

    public ItemFactory() {
    }

    private int getTier(int level) {
        int tier = (roll(Mechanics.TIER_LEVEL + 1) + level - (Mechanics.TIER_LEVEL + 1) / 2) / Mechanics.TIER_LEVEL + 1;
        if (tier < 1) tier = 1;
        if (tier > Material.MAX_TIER) tier = Material.MAX_TIER;
        return tier;
    }

    public IWeapon generateWeapon(Set<WeaponBase> filter, int level) {
        RandomList<WeaponBase> list = new RandomList<>();
        if (filter != null && filter.size() > 0) {
            for (WeaponBase base : filter) {
                list.add(base, 1);
            }
        } else {
            for (WeaponBase base : entityLoader.weaponBase()) {
                if (base.getMaterialType().isNatural()) continue;
                list.add(base, 1);
            }
        }

        WeaponBase base = list.getItem();
        return generateWeapon(base, level);
    }

    public IWeapon generateWeapon(WeaponBase base, int level) {
        int tier = getTier(level);
        Material material = chooseMaterial(base.getMaterialType(), tier);

        Optional<WeaponQuality> quality = empty();
        if (Utils.chance(50)) quality = of(WeaponQuality.values()[roll(WeaponQuality.values().length)]);
        if (quality.isPresent() && !quality.get().fits(base)) quality = empty();

        Optional<WeaponPropertyEnhance> enhance = empty();
        if (Utils.chance(25)) enhance = of(weaponEnhances.getItem());
        if (enhance.isPresent() && !enhance.get().fits(base)) enhance = empty();

        Optional<WeaponDamageEnhance> damageEnhance = empty();
        if (Utils.chance(25)) damageEnhance = of(weaponDamage.getItem());

        WeaponBuilder builder = builderFactory.createWeaponBuilder();
        return builder.setBase(base).setMaterial(material).setQuality(quality).setPropertyEnhance(enhance).
            setDamageEnhance(damageEnhance).construct();

    }

    public IArmor generateArmor(Set<ArmorTemplate> filter, int level) {
        IArmor result;

        RandomList<ArmorBase> list = new RandomList<>();
        for (ArmorBase template : entityLoader.armorTemplates()) {
            if (filter != null && filter.size() > 0 && !filter.contains(template)) continue;
            list.add(template, 1);
        }

        ArmorBase template = list.getItem();
        int tier = getTier(level);
        Material material = chooseMaterial(template.getMaterialType(), tier);

        ArmorQuality quality = null;
        if (Utils.chance(50)) quality = randomItem(entityLoader.armorQualitySet());

        result = armorModificationService.create(template, material, quality);

        if (Utils.chance(25)) result = armorModificationService.addProperty(result, armorPropertyEnhances.getItem());

        return result;
    }

    public Shield generateShield(Set<ShieldBase> filter, int level) {
        Shield result;

        RandomList<ShieldBase> list = new RandomList<>();
        for (ShieldBase base : entityLoader.shieldBases()) {
            if (filter != null && filter.size() > 0 && !filter.contains(base)) continue;
            list.add(base, 1);
        }

        ShieldBase base = list.getItem();
        int tier = getTier(level);
        Material material = chooseMaterial(base.getMaterialType(), tier);

        result = new Shield(base, material);

        return result;
    }

    public Material chooseMaterial(MaterialType type, int tier) {

        RandomList<Material> list = new RandomList<>();
        for (Material material : entityLoader.getMaterials()) {
            if (material.getMaterialType().equals(type) && (material.getTier() == tier)) {
                list.add(material, 1);
            }
        }
        return list.getItem();
    }

    @Inject
    public void setEntityLoader(EntityLoader entityLoader) {
        this.entityLoader = entityLoader;
    }

    @Inject
    public void setArmorModificationService(ArmorModificationService armorModificationService) {
        this.armorModificationService = armorModificationService;
    }

    @Inject
    public void setBuilderFactory(BuilderFactory builderFactory) { this.builderFactory = builderFactory; }
}

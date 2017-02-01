package inventory.weapon;

import core.RangeInfluence;

import static inventory.weapon.WeaponProperty.PIERCE;

public enum WeaponPropertyEnhance {
    PIERCING("of Piercing", PIERCE, true),
    CRUSHING("of Crushing", WeaponProperty.CRUSH, true),
    SLASHING("of Slashing", WeaponProperty.SLASH, true),
    STUNNING("of Stunning", WeaponProperty.STUN, true),
    PARRYING("of Parrying", WeaponProperty.PARRY, true),
    POISONED("Poisoned", WeaponProperty.POISON, false) {
        public boolean fits(WeaponBase weapon) {
            return super.fits(weapon) || weapon.hasProperty(PIERCE) || weapon.hasProperty(WeaponProperty.SLASH);
        }
    },
    CRUEL("Cruel", WeaponProperty.CRITICAL_DAMAGE, false) {
        public boolean fits(WeaponBase weapon) {
            return super.fits(weapon) || !weapon.isRanged();
        }
    },
    KEEN("Keen", WeaponProperty.CRITICAL_CHANCE, false) {
        public boolean fits(WeaponBase weapon) {
            return super.fits(weapon) || !weapon.hasProperty(WeaponProperty.CRUSH);
        }
    },
    VAMPIRIC("Vampiric", WeaponProperty.VAMPIRIC, false) {
        public boolean fits(WeaponBase weapon) {
            return super.fits(weapon) && !weapon.getMaterialType().isNatural() && !weapon.isRanged();
        }
    },
    AUTOMATIC("Automatic", WeaponProperty.AUTO_SHOT, false) {
        public boolean fits(WeaponBase weapon) {
            return weapon.isRanged() && weapon.getRangeInfluence() != RangeInfluence.SNIPER;
        }
    };

    private final String name;
    private final WeaponProperty property;
    private final boolean suffix;

    public String getName() {
        return name;
    }

    public WeaponProperty getProperty() {
        return property;
    }

    public boolean isSuffix() {
        return suffix;
    }

    private WeaponPropertyEnhance(String name, WeaponProperty property, boolean suffix) {
        this.name = name;
        this.property = property;
        this.suffix = suffix;
    }

    public boolean fits(WeaponBase weapon) {
        return weapon.hasProperty(property);
    }
}

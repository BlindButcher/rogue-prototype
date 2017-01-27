package inventory.weapon;

import creature.Creature;
import effect.EffectType;

import java.util.Collection;

import static core.Mechanics.tryEffect;
import static effect.EffectType.EMPTY_EFFECT;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * Contains possible weapon properties.
 */
public enum WeaponProperty {
    PIERCE,
    CRUSH(EffectType.CRUSH),
    SLASH(EffectType.BLEED),
    STUN(EffectType.STUN),
    POISON(EffectType.POISON),
    CRITICAL_DAMAGE,
    CRITICAL_CHANCE,
    PIN(EffectType.PIN),
    DISABLED(EffectType.DISABLED),
    PARRY,
    VAMPIRIC,
    AUTO_SHOT,
    ACCURATE,
    CLOSE_RANGE_SHOT,
    AUTO_HIT;

    public EffectType effect;

    private WeaponProperty(EffectType effect) {
        this.effect = effect;
    }

    WeaponProperty() {
        this.effect = EMPTY_EFFECT;
    }

    public static Collection<WeaponProperty> withEffects() {
        return asList(values()).stream().filter(e -> e.effect != EMPTY_EFFECT).collect(toList());
    }

    public static void tryWeaponEffects(Creature target, IWeapon weapon, int damageDone, int totalDamage) {
        withEffects().stream().filter(p -> tryEffect(weapon.getProperty(p), totalDamage)).
                forEach(p -> p.effect.effectOn(target, weapon.getProperty(p), damageDone));
    }
}

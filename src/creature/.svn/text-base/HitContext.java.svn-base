package creature;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import core.DamageType;
import core.Mechanics;
import inventory.weapon.IWeapon;
import util.Utils;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class HitContext {
    Map<Property, Object> properties = Maps.newHashMap();

    public HitContext(IWeapon weapon, Creature target) {
        checkNotNull(weapon); checkNotNull(target);
        properties.put(Property.WEAPON, weapon);
        properties.put(Property.TARGET, target);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Property property) {
        Preconditions.checkArgument(properties.containsKey(property), format("Property not found %s", property));
        return (T) properties.get(property);
    }

    public void put(Property property, Object value) {
        properties.put(property, value);
    }

    public boolean hasProperty(Property property) {
        return properties.containsKey(property);
    }

    public void addFlag(Property property) {
        properties.put(property, true);
    }

    public IWeapon getWeapon() {
        return get(Property.WEAPON);
    }

    public Creature getTarget() {
        return get(Property.TARGET);
    }

    public int getDamage(DamageType damageType) {
        return (this.<Map<DamageType, Integer>>get(Property.DAMAGE_ROLLS)).get(damageType);
    }

    public void setDamage(DamageType damageType, int value) {
        (this.<Map<DamageType, Integer>>get(Property.DAMAGE_ROLLS)).put(damageType, value);
    }

    public int damageTotal() {
        return damageSum(get(Property.DAMAGE_ROLLS));
    }

    public int damageDone() {
        return damageSum(get(Property.DAMAGE_DONE));
    }

    private int damageSum(Map<DamageType, Integer> damage) {
        int sum = 0;
        for (Integer i : damage.values()) sum += i;
        return sum;
    }

    public String damageDoneMessage() {
        StringBuilder sb = new StringBuilder();
        Map<DamageType, Integer> damages = get(Property.DAMAGE_DONE);
        for (DamageType type : damages.keySet()) {
            sb.append(type).append(":").append(damages.get(type));
        }
        return sb.toString();
    }

    /**
     * Calculates and memorize attack value.
     *
     * @param creature attacking one.
     * @param weapon   used for attack.
     */
    public void prepareAttackVal(Creature creature, Creature target, IWeapon weapon) {
        int dist = weapon.isRanged() ? 1 : Utils.dist2(creature, target);
        put(Property.DIST_TO_TARGET, dist);
        put(Property.ATTACK, Mechanics.calculateAttack(creature, target, weapon));
    }

    static public enum Property {
        WEAPON,
        TARGET,
        ATTACKER,
        ATTACK,
        DEFENCE,
        DAMAGE_ROLLS,
        DAMAGE_DONE,
        ARMOR_ROLL,
        // Contains distance to target
        DIST_TO_TARGET,
        ATTACK_PULSED,
        ATTACK_PARRIED
    }
}

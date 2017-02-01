package inventory.armor;

import com.google.common.base.Predicate;
import core.DamageType;
import creature.HitContext;

import java.util.Map;

import static com.google.common.collect.Sets.filter;

/**
 * Contains different armor types, with their damage reduction logic.
 *
 * @author Blind
 */
public enum ArmorType
{
    NONE,
    COMMON() {
        public void armorEffect(HitContext context)
        {
            Map<DamageType, Integer> damages = context.get(HitContext.Property.DAMAGE_DONE);
            for (DamageType type : filter(damages.keySet(), new Predicate<DamageType>() {
                public boolean apply(DamageType d) { return d.physical; }
            })) {
                    damages.put(type, damages.get(type) - damages.get(type) / PER_CENT_REDUCE);
            }
        }
    },
    COMPOSITE() {
        public void armorEffect(HitContext context)
        {
            Map<DamageType, Integer> damages = context.get(HitContext.Property.DAMAGE_DONE);
            for (DamageType type : damages.keySet()) {
                damages.put(type, damages.get(type) - damages.get(type) / COMPOSITE_REDUCTION);
            }
        }
    };

    public static final int PER_CENT_REDUCE = 10;
    public static final int COMPOSITE_REDUCTION = 20;

    public void armorEffect(HitContext context) { }

}

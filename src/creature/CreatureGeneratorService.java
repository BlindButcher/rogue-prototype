package creature;

import com.google.inject.Inject;
import inventory.ItemFactory;
import loader.EntityLoader;
import main.LevelContext;
import util.Point;
import util.RandomList;
import util.Utils;

import java.util.Collection;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * Generates creatures for specific dungeon.
 *
 * @author Blind
 */
public class CreatureGeneratorService {
    private EntityLoader loader;
    private ItemFactory itemFactory;

    public Creature generateBoss(LevelContext level, Point point, final Set<String> bossUsed) {

        Collection<CreatureTemplate> bosses = loader.templates().stream().filter(o -> o.isBoss() &&
            !bossUsed.contains(o.getId())).collect(toList());

        return !bosses.isEmpty() ? generateCreature(new RandomList<>(bosses).getItem(), level, point) : null;
    }

    public Creature generateCreature(String id, LevelContext level, Point point) {
        return generateCreature(loader.findCreature(id), level, point);
    }

    public Creature generateCreature(CreatureTemplate template, LevelContext level, Point point) {
        Creature creature = new Creature(template).register(level, point, 1);

        int rank = Utils.roll(3) + level.getDepth() + 1;
        creature.setRank(0);

        for (int i = 0; i < rank; i++) {
            creature.raiseRank();
        }

        if (template.getPredefinedWeapon() != null) {
            creature.forceEquip(itemFactory.generateWeapon(template.getPredefinedWeapon(), creature.getRank()));
        }
        if (!template.getWeapons().isEmpty()) {
            creature.forceEquip(itemFactory.generateWeapon(template.getWeapons(), creature.getRank()));
        }
        if (!template.getArmors().isEmpty()) {
            creature.forceEquip(itemFactory.generateArmor(template.getArmors(), creature.getRank()));
        }
        if (!template.getShields().isEmpty()) {
            creature.forceEquip(itemFactory.generateShield(template.getShields(), creature.getRank()));
        }
        return creature;
    }

    public EntityLoader getLoader() {
        return loader;
    }

    @Inject
    public void setLoader(EntityLoader loader) {
        this.loader = loader;
    }

    @Inject
    public void setItemFactory(ItemFactory itemFactory) {
        this.itemFactory = itemFactory;
    }
}

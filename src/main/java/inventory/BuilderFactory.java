package inventory;

import com.google.inject.Inject;
import inventory.weapon.WeaponBuilder;
import loader.EntityLoader;

/**
 * Factory witch create builders.
 *
 * @author Blind
 */
public class BuilderFactory {
    private EntityLoader loader;

    public WeaponBuilder createWeaponBuilder() {
        WeaponBuilder builder = new WeaponBuilder();
        builder.setLoader(loader);
        return builder;
    }

    @Inject
    public void setLoader(EntityLoader loader) {
        this.loader = loader;
    }
}

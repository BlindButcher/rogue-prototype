package main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import creature.Attribute;
import creature.CreatureTemplate;
import inventory.BuilderFactory;
import loader.EntityLoader;
import loader.UnifiedAttribute;
import org.junit.Before;

/**
 * Abstract test, which loads TestModule .
 *
 * @author Blind
 */
public abstract class AbstractInjectionTest
{
    protected Injector injector;
    protected EntityLoader entityLoader;
    protected BuilderFactory builderFactory;

    @Before
    public void init()
    {
        injector = Guice.createInjector(new TestModule());
        entityLoader = injector.getInstance(EntityLoader.class);
        builderFactory = injector.getInstance(BuilderFactory.class);
    }

    public CreatureTemplate getDummy()
    {
        CreatureTemplate template = new CreatureTemplate();
        template.setName("DUMMY");
        template.setGlyph('@');
        template.getUnifiedAttributes().add(new UnifiedAttribute(Attribute.MELEE, 1, 10));
        template.getUnifiedAttributes().add(new UnifiedAttribute(Attribute.RANGED, 1, 10));
        template.getUnifiedAttributes().add(new UnifiedAttribute(Attribute.DEFENCE, 1, 10));
        template.getUnifiedAttributes().add(new UnifiedAttribute(Attribute.DAMAGE, 1, 10));
        template.getUnifiedAttributes().add(new UnifiedAttribute(Attribute.SPEED, 1, 10));
        template.getUnifiedAttributes().add(new UnifiedAttribute(Attribute.HEALTH, 1, 10));
        template.getUnifiedAttributes().add(new UnifiedAttribute(Attribute.ARMOR, 1, 10));
        return template;
    }
}

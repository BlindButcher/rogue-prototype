package main;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import creature.CreatureGeneratorService;
import dungeon.DungeonService;
import dungeon.IDungeonService;
import dungeon.Scenario;
import dungeon.generator.GenerationStrategy;
import inventory.ItemFactory;
import inventory.armor.ArmorModificationService;
import loader.EntityLoader;
import util.AppLogHandler;
import view.IRenderingService;
import view.RenderingService;

import java.util.logging.LogManager;

import static com.google.inject.name.Names.named;

/**
 * Main module, contains injections points for different services.
 *
 * @author Blind
 */
public class AppModule extends AbstractModule {
    protected void configure() {
        try {
            LogManager.getLogManager().readConfiguration(
                    getClass().getClassLoader().getResourceAsStream("logging.properties"));
        } catch (Exception e) {
            throw new IllegalStateException("Logging system is not initialized.", e);
        }
        bind(AppLogHandler.class).in(Scopes.SINGLETON);
        bind(Messages.class).in(Scopes.SINGLETON);
        bind(EntityLoader.class).in(Scopes.SINGLETON);
        bind(CreatureGeneratorService.class).in(Scopes.SINGLETON);
        bind(IDungeonService.class).to(DungeonService.class).in(Scopes.SINGLETON);
        bind(IRenderingService.class).to(RenderingService.class).in(Scopes.SINGLETON);
        bind(Scenario.class).in(Scopes.SINGLETON);
        bind(EventHolder.class).in(Scopes.SINGLETON);
        bind(ArmorModificationService.class).in(Scopes.SINGLETON);
        bind(ItemFactory.class).in(Scopes.SINGLETON);

        bindConstant().annotatedWith(named("creatureFile")).to("creature.xml");
        bindConstant().annotatedWith(named("itemFile")).to("items.xml");
        bindConstant().annotatedWith(named("mapFile")).to("maps.xml");
        bindConstant().annotatedWith(named("materialFile")).to("materials.xml");
        bindConstant().annotatedWith(named("scenarioFile")).to("scenario.xml");
        bindConstant().annotatedWith(named("dungeonGenerationStrategy")).to(GenerationStrategy.RANDOM);
    }

}

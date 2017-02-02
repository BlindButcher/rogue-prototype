package web;

import com.google.inject.Guice;
import com.google.inject.Injector;
import creature.Hero;
import inventory.Grenade;
import inventory.ItemFactory;
import inventory.armor.ArmorModificationService;
import inventory.armor.ArmorPropertyEnhance;
import inventory.armor.IArmor;
import inventory.weapon.Weapon;
import loader.EntityLoader;
import main.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.AppLogHandler;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static main.DungeonScreen.createLevel;
import static main.DungeonScreen.prepareHero;
import static util.Point.point;

/**
 * @author BlindButcher
 * @since 2/1/17
 */
@RestController
public class AppStateControler {
    private WorldState state;
    private Injector injector;


    @RequestMapping("/game-state")
    public Object currentState() {
        return new AtomicReference<>(state);
    }

    @PostConstruct
    public String initState() {
        injector = Guice.createInjector(new AppModule());

        LogManager logManager = LogManager.getLogManager();
        Logger logger = Logger.getLogger("MAIN");
        logManager.addLogger(logger);
        logger.addHandler(injector.getInstance(AppLogHandler.class));
        state = new WorldState(injector.getInstance(EventHolder.class));
        LevelContext level = createLevel(0, injector, state);
        state.getLevels().add(level);

        Hero hero = (Hero) new Hero(prepareHero()).register(level, point(1, 1), 0);

        hero.forceEquip(injector.getInstance(ItemFactory.class).generateShield(null, hero.getRank()));
        hero.forceEquip(new Weapon(injector.getInstance(EntityLoader.class).findWeapon("SNIPER_RIFLE"),
                injector.getInstance(EntityLoader.class).findMaterial("BRONZE")));
        IArmor armor = injector.getInstance(ItemFactory.class).generateArmor(null, hero.getRank());
        armor = injector.getInstance(ArmorModificationService.class).addProperty(armor, ArmorPropertyEnhance.PULSE);
        hero.forceEquip(armor);
        hero.forceEquip(new Grenade());
        hero.setXY(level.getRandomEmptyCell());
        hero.setEventHolder(state.eventHolder);

        state.eventHolder.addEvent(new TimeEvent(state.eventHolder.time, hero, EventType.MOVE));

        state.setHero(hero);
        state.currentLevel = level;
        state.heroMove = true;
        return "Success";
    }
}

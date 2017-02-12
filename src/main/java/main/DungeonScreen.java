package main;

import ability.AbilityType;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.inject.Injector;
import command.ActionCommand;
import command.EscapeCommand;
import command.MoveCommand;
import core.Mechanics;
import creature.Attribute;
import creature.Creature;
import creature.CreatureTemplate;
import creature.Hero;
import dungeon.Cell;
import dungeon.Cells;
import dungeon.Dwelling;
import dungeon.Scenario;
import effect.IEffect;
import inventory.*;
import inventory.armor.ArmorModificationService;
import inventory.armor.ArmorPropertyEnhance;
import inventory.armor.ArmorTemplate;
import inventory.armor.IArmor;
import inventory.shield.ShieldBase;
import inventory.weapon.IWeapon;
import inventory.weapon.Weapon;
import inventory.weapon.WeaponBase;
import loader.EntityLoader;
import loader.UnifiedAttribute;
import org.newdawn.slick.Color;
import org.newdawn.slick.*;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProvider;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.command.KeyControl;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import slick.TextField;
import util.Point;
import util.RandomList;
import util.Utils;
import view.CreaturePresenter;
import view.IRenderable;
import view.IRenderingService;

import java.awt.Font;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import static core.Mechanics.calculateAttack;
import static core.Mechanics.hitChance;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static main.Delays.DEFAULT_DELAY;
import static util.Point.point;

public class DungeonScreen extends BasicGameState implements InputProviderListener {
    private static final Logger MAIN = Logger.getLogger("MAIN");

    private IRenderable POTION_RENDER = new IRenderable() {
        public String getId() {
            return "POTION";
        }

        public String getImagePath() {
            return "graphic/misc/potion.png";
        }
    };

    private WorldState state;
    private GameEngine gameEngine = new GameEngine();

    private int bx, by;
    private TrueTypeFont font, logFont;


    private PlayerState playerState = PlayerState.EMPTY_STATE;

    private Injector injector;
    private IRenderingService renderingService;
    private TrueTypeFont effectFont;

    public DungeonScreen(Injector injector) {
        this.injector = injector;
        state = new WorldState(injector.getInstance(EventHolder.class));
        this.renderingService = injector.getInstance(IRenderingService.class);
    }

    private enum PlayerState {
        FIRE_STATE,
        THROW_STATE,
        EMPTY_STATE
    }

    private Creature over = null;
    private int overX = Main.NX / 2, overY = Main.NY / 2;

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        InputProvider provider = new InputProvider(container.getInput());
        provider.addListener(this);

        provider.bindCommand(new KeyControl(Input.KEY_LEFT), new MoveCommand(-1, 0));
        provider.bindCommand(new KeyControl(Input.KEY_RIGHT), new MoveCommand(1, 0));
        provider.bindCommand(new KeyControl(Input.KEY_UP), new MoveCommand(0, -1));
        provider.bindCommand(new KeyControl(Input.KEY_DOWN), new MoveCommand(0, 1));

        provider.bindCommand(new KeyControl(Input.KEY_NUMPAD1), new MoveCommand(-1, 1));
        provider.bindCommand(new KeyControl(Input.KEY_NUMPAD2), new MoveCommand(0, 1));
        provider.bindCommand(new KeyControl(Input.KEY_NUMPAD3), new MoveCommand(1, 1));
        provider.bindCommand(new KeyControl(Input.KEY_NUMPAD4), new MoveCommand(-1, 0));
        provider.bindCommand(new KeyControl(Input.KEY_SPACE), new MoveCommand(0, 0));
        provider.bindCommand(new KeyControl(Input.KEY_NUMPAD6), new MoveCommand(1, 0));
        provider.bindCommand(new KeyControl(Input.KEY_NUMPAD7), new MoveCommand(-1, -1));
        provider.bindCommand(new KeyControl(Input.KEY_NUMPAD8), new MoveCommand(0, -1));
        provider.bindCommand(new KeyControl(Input.KEY_NUMPAD9), new MoveCommand(1, -1));

        provider.bindCommand(new KeyControl(Input.KEY_END), new MoveCommand(-1, 1));
        provider.bindCommand(new KeyControl(Input.KEY_PRIOR), new MoveCommand(1, -1));
        provider.bindCommand(new KeyControl(Input.KEY_HOME), new MoveCommand(-1, -1));
        provider.bindCommand(new KeyControl(Input.KEY_NEXT), new MoveCommand(1, 1));

        provider.bindCommand(new KeyControl(Input.KEY_D), new ActionCommand("drink"));
        provider.bindCommand(new KeyControl(Input.KEY_E), new ActionCommand("equip"));
        provider.bindCommand(new KeyControl(Input.KEY_Z), new ActionCommand("up"));
        provider.bindCommand(new KeyControl(Input.KEY_X), new ActionCommand("down"));
        provider.bindCommand(new KeyControl(Input.KEY_F), new ActionCommand("fire"));
        provider.bindCommand(new KeyControl(Input.KEY_T), new ActionCommand("throw"));

        provider.bindCommand(new KeyControl(Input.KEY_ESCAPE), new EscapeCommand());

        font = new TrueTypeFont(new Font("Courier", Font.PLAIN, Main.H), true);
        logFont = new TrueTypeFont(new Font("Courier", Font.PLAIN, Main.HL), true);
        effectFont = new TrueTypeFont(new Font("Courier", Font.PLAIN, Main.HL / 2), true);

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

        bx = hero.getX() - Main.NX / 2;
        by = hero.getY() - Main.NY / 2;

        state.setHero(hero);
        state.currentLevel = level;
        state.heroMove = true;
    }

    public static LevelContext createLevel(int depth, Injector injector, WorldState state) {
        LevelContext level = injector.getInstance(Scenario.class).createLevel(depth);

        for (Creature creature : level.getCreatures()) {
            creature.setEventHolder(state.eventHolder);
            state.addEvent(new TimeEvent(state.eventHolder.time + 1, creature, EventType.MOVE));
        }

        for (int i = 0; i < 5; i++) {
            level.addPotion(level.getMap().getRandomPassableCell());
        }

        RandomList<Integer> list = new RandomList<>(asList(1, 2, 3));
        for (int i = 0; i < 8; i++) {
            int[] point = level.getMap().getRandomPassableCell();
            int x = point[0], y = point[1];

            Item item;
            int val = list.getItem();
            switch (val) {
                case 1:
                    item = injector.getInstance(ItemFactory.class).generateWeapon(Collections.<WeaponBase>emptySet(), depth);
                    break;
                case 2:
                    item = injector.getInstance(ItemFactory.class).generateShield(Collections.<ShieldBase>emptySet(), depth);
                    break;
                case 3:
                    item = injector.getInstance(ItemFactory.class).generateArmor(Collections.<ArmorTemplate>emptySet(), depth);
                    break;
                default:
                    throw new IllegalArgumentException(format("Not supported value %s", val));
            }

            level.addItem(item, x, y);
        }

        return level;
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        Hero hero = state.hero.get();
        LevelContext level = state.currentLevel;
        Cells map = state.currentLevel.getMap();
        Color grayFilter = new Color(0.3f, 0.3f, 0.3f);
        for (int i = bx; i < bx + Main.NX; i++) {
            for (int j = by; j < by + Main.NY; j++) {
                if (state.hero.get().sees(i, j)) {
                    // font.drawString((i - bx) * Main.H, (j - by) * Main.H,
                    // map.getCell(i, j).getC() + "", Color.white);
                    renderingService.render(g, map.getCell(i, j), (i - bx) * Main.H, (j - by) * Main.H);

                    List<Item> items = state.currentLevel.getItems(i, j);
                    if (items.size() > 0) {
                        // font.drawString((i - bx) * Main.H, (j - by) * Main.H,
                        // items.get(0).getC() + "", Color.magenta);
                        renderingService.render(g, items.get(0), (i - bx) * Main.H, (j - by) * Main.H);
                    }

                    map.setAware(i, j);
                } else if (map.isAware(i, j)) {
                    // font.drawString((i - bx) * Main.H, (j - by) * Main.H,
                    // map.getCell(i, j).getC() + "", new Color(0.3f, 0.3f,
                    // 0.3f));
                    renderingService.render(g, map.getCell(i, j), (i - bx) * Main.H, (j - by) * Main.H, grayFilter);
                }
            }
        }

        for (Dwelling c : state.currentLevel.getDwellings()) {
            int x = c.getLocation().x, y = c.getLocation().y;
            if (x < bx || x >= bx + Main.NX) continue;
            if (y < by || y >= by + Main.NY) continue;
            if (!hero.sees(x, y)) continue;
            renderingService.render(g, c, (x - bx) * Main.H, (y - by) * Main.H);
        }

        renderingService.render(g, hero, (hero.getX() - bx) * Main.H, (hero.getY() - by) * Main.H);


        for (Creature c : level.getCreatures()) {
            if (c == hero) continue;
            int x = c.getX(), y = c.getY();
            if (x < bx || x >= bx + Main.NX) continue;
            if (y < by || y >= by + Main.NY) continue;
            if (!hero.sees(c)) continue;
            renderingService.render(g, c, (x - bx) * Main.H, (y - by) * Main.H);
            if (c.equals(over)) {
                g.drawString(hitChance(calculateAttack(hero, c, (IWeapon) hero.getEquiped(EquipmentSlot.MAIN_HAND)), c.getDefence()) + "%",
                        (x - bx) * Main.H, (y - by) * Main.H);
            }
        }

        for (int[] p : level.getPotions()) {
            int x = p[0], y = p[1];
            if (x < bx || x >= bx + Main.NX) continue;
            if (y < by || y >= by + Main.NY) continue;
            if (!hero.sees(x, y)) continue;
            // font.drawString((x - bx) * Main.H, (y - by) * Main.H, "p",
            // Color.blue);

            renderingService.render(g, POTION_RENDER, (x - bx) * Main.H, (y - by) * Main.H);
            //g.drawImage(POTION, (x - bx) * Main.H, (y - by) * Main.H);
        }

        if (playerState == PlayerState.FIRE_STATE || playerState == PlayerState.THROW_STATE) {
            List<int[]> line = Utils.bresenham(hero.getX(), hero.getY(), overX, overY);
            line.remove(0);
            boolean sees = true;
            g.setColor(new Color(0f, 0f, 1f, .5f));
            for (int[] point : line) {
                int x = point[0], y = point[1];
                if (sees && (!map.isAware(x, y) || playerState == PlayerState.THROW_STATE ?
                        !hero.inThrowRange(point(x, y)) : !hero.inRange(x, y))) {
                    sees = false;
                    g.setColor(new Color(1f, 0f, 0f, .5f));
                }
                g.fillRect((x - bx) * Main.H, (y - by) * Main.H, Main.H, Main.H);
                if (sees && !map.getCell(x, y).isPassable()) {
                    sees = false;
                    g.setColor(new Color(1f, 0f, 0f, .5f));
                }
            }
        }

        int y = Main.NY * Main.H;
        for (String s : injector.getInstance(Messages.class).getMessages(Main.NL)) {
            logFont.drawString(0, y, " - " + s);
            y += Main.HL;
        }

        int max = hero.getMaxHp();
        int hp = max - hero.getWounds();
        if (hp < 0) hp = 0;
        if (hp * 100 > 70 * max)
            g.setColor(Color.green);
        else if (hp * 100 > 30 * max)
            g.setColor(Color.orange);
        else
            g.setColor(Color.red);


        logFont.drawString(Main.NX * Main.H + 5, 10, "Health:");
        g.drawRect(Main.NX * Main.H + 10, 10 + Main.HL + 10, Main.PW - 40, 20);
        g.fillRect(Main.NX * Main.H + 10, 10 + Main.HL + 10, 1f * (Main.PW - 40) * hp / hero.getMaxHp(), 20);

        logFont.drawString(Main.NX * Main.H + 5, Main.HL + 40, "Potions: " + hero.getPotions());

        drawMultiLine(logFont, new CreaturePresenter(hero).creatureInfo(), point(Main.NX * Main.H + 5, Main.HL + 65));

        if (over != null) {
            drawMultiLine(logFont, new CreaturePresenter(over).creatureInfo(), point(Main.NX * Main.H + 5, Main.HL + 350));
            drawMultiLine(logFont, new CreaturePresenter(over).effects(), point(Main.NX * Main.H + 5, Main.HL + 650));

        }

        logFont.drawString(Main.NX * Main.H + 5, Main.NY * Main.H - Main.H, "Level:" + level.getDepth());
        List<Item> onFloor = level.getItems(hero.getX(), hero.getY());
        logFont.drawString(Main.NX * Main.H + 5, Main.NY * Main.H, "On the floor:");
        if (onFloor.size() > 0)
            for (int i = 0; i < onFloor.size(); i++) {
                logFont.drawString(Main.NX * Main.H + 10, Main.NY * Main.H + Main.HL * (i + 1), onFloor.get(i).getName());
            }
        else {
            logFont.drawString(Main.NX * Main.H + 10, Main.NY * Main.H + Main.HL, "nothing here...");
        }

        if (!hero.isAlive()) {
            endGame(EndCondition.LOSE);
        }

        if (injector.getInstance(Scenario.class).checkWin(state.getLevels())) {
            endGame(EndCondition.WIN);
        }

        int increment = 0;
        g.setColor(Color.white);
        for (IEffect effect : hero.getActiveEffects()) {
            TextField field = new TextField(container, effectFont, Main.HL, Main.HL * 3
                    + (increment * Main.HL), Main.HL * 3, Main.HL);
            field.setText(effect.getType().name());
            field.render(container, g);
            increment++;
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        while (!state.heroMove) {
            if (makeTick()) break;
        }
    }

    boolean makeTick() {
        state = gameEngine.apply(state);
        return state.heroMove;
    }

    public void controlPressed(Command command) {
        Hero hero = state.hero.get();
        EventHolder eventHolder = state.eventHolder;
        LevelContext level = state.currentLevel;
        if (!state.heroMove) return;
        if (!state.hero.get().isAlive()) return;
        if (injector.getInstance(Scenario.class).checkWin(state.getLevels())) return;

        if (command instanceof EscapeCommand) {
            if (playerState == PlayerState.FIRE_STATE || playerState == PlayerState.THROW_STATE) {
                playerState = PlayerState.EMPTY_STATE;
                MAIN.fine("Shooting canceled");
            }
            return;
        }

        if (playerState == PlayerState.FIRE_STATE) return;

        if (command instanceof MoveCommand) {
            MoveCommand com = (MoveCommand) command;
            int tm = hero.goTo(point(hero.getX() + com.getDx(), hero.getY() + com.getDy()));
            if (tm != 0) {
                centerOnHero();
                eventHolder.addEvent(new TimeEvent(eventHolder.time + tm, hero, EventType.MOVE));
                state.heroMove = false;

                for (Iterator<int[]> i = level.getPotions().iterator(); i.hasNext(); ) {
                    int[] p = i.next();
                    int x = p[0], y = p[1];
                    if (hero.getX() == x && hero.getY() == y) {
                        hero.pickupPotion();
                        i.remove();
                    }
                }
            }
        } else if (command instanceof ActionCommand) {
            ActionCommand com = (ActionCommand) command;
            String action = com.getAction();
            if ("drink".equals(action)) {
                int tm = hero.drinkPotion();
                if (tm != 0) {
                    eventHolder.addEvent(new TimeEvent(eventHolder.time + tm, hero, EventType.MOVE));
                    state.heroMove = false;
                }
            } else if ("equip".equals(action)) {
                Collection<Item> items = level.getItems(hero.getX(), hero.getY());
                if (items.size() > 0) {
                    Iterator<Item> it = items.iterator();
                    hero.forceEquip((Equipable) it.next());
                    it.remove();
                    eventHolder.addEvent(new TimeEvent(eventHolder.time + DEFAULT_DELAY.amount, hero, EventType.MOVE));
                    state.heroMove = false;
                }
            } else if ("fire".equals(action)) {
                if (hero.getAbilities().get(AbilityType.RANGE_ATTACK) != null) {
                    playerState = PlayerState.FIRE_STATE;
                    MAIN.fine("Prepare to shoot");
                }
            } else if ("throw".equals(action)) {
                if (hero.getAbilities().containsKey(AbilityType.THROW)) {
                    playerState = PlayerState.THROW_STATE;
                    MAIN.fine("Prepare to throw");
                }
            } else if ("up".equals(action) && level.getMap().getCell(hero.getX(), hero.getY()) == Cell.UP_STAIRS) {

                if (level.getDepth() > 0) {
                    level = state.getLevels().get(level.getDepth() - 1);
                    hero.enterLevel(level);
                    hero.setX(level.getDownStairs()[0]);
                    hero.setY(level.getDownStairs()[1]);
                    centerOnHero();
                }
            } else if ("down".equals(action) && level.getMap().getCell(hero.getX(), hero.getY()) == Cell.DOWN_STAIRS) {
                if (level.getDepth() + 1 >= state.getLevels().size())
                    state.getLevels().add(createLevel(level.getDepth() + 1, injector, state));
                level = state.getLevels().get(level.getDepth() + 1);
                hero.enterLevel(level);
                hero.setX(level.getUpStairs()[0]);
                hero.setY(level.getUpStairs()[1]);
                centerOnHero();
            }
        }
    }

    private void centerOnHero() {
        Hero hero = state.hero.get();
        LevelContext level = state.currentLevel;
        int dox = hero.getX() - overX, doy = hero.getY() - overY;
        bx = hero.getX() - Main.NX / 2;
        by = hero.getY() - Main.NY / 2;
        overX = hero.getX() - dox;
        overY = hero.getY() - doy;
        over = level.getCreature(overX, overY);
        if (over != null && !hero.sees(over)) over = null;
    }

    public void controlReleased(Command command) {

    }

    @Override
    public void mousePressed(int button, int x, int y) {
        if (playerState == PlayerState.FIRE_STATE) {
            mouseMoved(x, y, x, y);
            Creature target = over;
            if (target != null) {
                if (performRangeAttack(target)) {
                    playerState = PlayerState.EMPTY_STATE;
                }
            } else {
                MAIN.fine("Target not found");
            }
        }
        if (playerState == PlayerState.THROW_STATE) {
            Optional<Point> pointExist = overPoint(point(x, y));
            if (pointExist.isPresent())
                performThrow(pointExist.get());
            playerState = PlayerState.EMPTY_STATE;
        }
    }

    private void performThrow(Point point) {
        Hero hero = state.hero.get();
        LevelContext level = state.currentLevel;
        EventHolder eventHolder = state.eventHolder;
        Preconditions.checkArgument(hero.inThrowRange(point));
        MAIN.fine("Throws bomb");

        Collection<Creature> creaturesInArea = level.locatedIn(point.neighboursAndSelf());

        for (Creature creature : creaturesInArea) {
            creature.applyDamage(hero, Creature.THROW_DAMAGE);
        }

        int delay = Delays.DEFAULT_DELAY.amount;
        eventHolder.addEvent(new TimeEvent(eventHolder.time + delay, hero, EventType.MOVE));
        state.heroMove = false;
    }

    private Optional<Point> overPoint(Point point) {
        Hero hero = state.hero.get();
        final int row = point.x / Main.H, col = point.y / Main.H;
        if (row < 0 || row >= Main.NX || col < 0 || col >= Main.NY) return Optional.absent();
        final Point over = point(row + bx, col + by);
        return !hero.sees(over) ? Optional.<Point>absent() : Optional.of(over);
    }


    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) {
        Hero hero = state.hero.get();
        LevelContext level = state.currentLevel;
        int row = newx / Main.H, col = newy / Main.H;
        if (row < 0 || row >= Main.NX) return;
        if (col < 0 || col >= Main.NY) return;
        row += bx;
        col += by;
        overX = row;
        overY = col;
        over = level.getCreature(overX, overY);
        if (over != null && !hero.sees(over)) over = null;
    }

    private boolean performRangeAttack(Creature target) {
        Hero hero = state.hero.get();
        EventHolder eventHolder = state.eventHolder;
        if (hero.inRange(target.getX(), target.getY())) {
            MAIN.fine(format("Shoots %s", target.getName()));
            int delay = hero.hit(target.getXY(), (IWeapon) hero.getAbilitySource(AbilityType.RANGE_ATTACK));
            eventHolder.addEvent(new TimeEvent(eventHolder.time + delay, hero, EventType.MOVE));
            state.heroMove = false;
            return true;
        } else {
            MAIN.fine(format("Target %s not in range", target.getName()));
            return false;
        }
    }


    @Override
    public int getID() {
        return Main.States.MAIN_SCREEN.ordinal();
    }

    private void endGame(EndCondition condition) {
        Hero hero = state.hero.get();
        switch (condition) {
            case LOSE:
                font.drawString(100, 100, "Your hero died!!!", Color.red);
                break;
            case WIN:
                font.drawString(100, 100, "You are the WINNER!!!", Color.green);
                break;
            default:
                throw new IllegalArgumentException(format("No handler for enum type %s", condition));
        }

        drawMultiLine(logFont, new CreaturePresenter(hero).creatureStats(), point(20, 140));
    }

    /**
     * Draws multi line text.
     *
     * @param font    draw component.
     * @param strings text to draw.
     * @param point   initial point.
     */
    private void drawMultiLine(TrueTypeFont font, List<String> strings, Point point) {
        for (int i = 0; i < strings.size(); i++) {
            font.drawString(point.x, point.y + Main.HL * i, strings.get(i));
        }
    }

    public static CreatureTemplate prepareHero() {
        CreatureTemplate template = new CreatureTemplate();
        template.setName("HERO");
        template.setGlyph('@');
        template.getUnifiedAttributes().add(new UnifiedAttribute(Attribute.MELEE, 10, 50));
        template.getUnifiedAttributes().add(new UnifiedAttribute(Attribute.RANGED, 8, 50));
        template.getUnifiedAttributes().add(new UnifiedAttribute(Attribute.DEFENCE, 10, 50));
        template.getUnifiedAttributes().add(new UnifiedAttribute(Attribute.DAMAGE, Mechanics.DEFAULT_DAMAGE / 4, 30));
        template.getUnifiedAttributes().add(new UnifiedAttribute(Attribute.SPEED, 20, 10));
        template.getUnifiedAttributes().add(new UnifiedAttribute(Attribute.HEALTH, Mechanics.DEFAULT_HEALTH, 250));
        template.getUnifiedAttributes().add(new UnifiedAttribute(Attribute.ARMOR, 0, 20));
        template.setImagePath("graphic/hero/hero2.png");
        return template;
    }
}

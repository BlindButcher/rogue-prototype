package main;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import creature.Creature;
import creature.CreatureGeneratorService;
import creature.CreatureTemplate;
import dungeon.DungeonService;
import inventory.EquipmentSlot;
import inventory.ItemFactory;
import inventory.shield.Shield;
import inventory.shield.ShieldBase;
import inventory.weapon.WeaponBase;
import loader.EntityLoader;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintWriter;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.function.Predicate;

import static com.google.common.collect.Lists.newArrayList;
import static inventory.EquipmentSlot.MAIN_HAND;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static util.Point.point;

/**
 * Tests simple battle between creatures.
 * 
 * @author Blind
 */
public class BattleTest extends AbstractInjectionTest
{

	public static final int NUMBER_OF_FIGHTS = 100;
	public static final int BATTLE_DURATION = 50;

	private EntityLoader entityLoader;
	private CreatureGeneratorService generatorService;
	private static final Predicate<CreatureTemplate> predicate = creatureTemplate -> !creatureTemplate.isBoss();
    private ItemFactory itemFactory;

    @Before
	public void init()
	{
		super.init();
		entityLoader = injector.getInstance(EntityLoader.class);
		generatorService = injector.getInstance(CreatureGeneratorService.class);
        itemFactory = injector.getInstance(ItemFactory.class);
	}

	@Test
	public void testOneOne() throws Exception
	{
		PrintWriter out = new PrintWriter(System.out);
		List<Stat> stats = newArrayList();
		for (CreatureTemplate template : entityLoader.templates().stream().filter(predicate).collect(toList()))
		{
			for (CreatureTemplate enemy : entityLoader.templates().stream().filter(predicate).collect(toList()))
			{
				if (template == enemy) continue;

				int wins = 0, losses = 0, draw = 0;
				Stat stat = new Stat(template.getName(), enemy.getName(), wins, losses, draw);
				for (int i = 0; i < NUMBER_OF_FIGHTS; i++)
				{
					DungeonScreen screen = new DungeonScreen(injector);
					LevelContext level = new LevelContext(DungeonService.TEST_MAP, 0);
					screen.setLevel(level);
					Creature c1 = generatorService.generateCreature(template, level, point(0, 0));
					c1.setEventHolder(injector.getInstance(EventHolder.class));
					Creature c2 = generatorService.generateCreature(enemy, level, point(4, 4));
					c2.setEventHolder(injector.getInstance(EventHolder.class));
					performBattle(c1, c2, stat, screen, level);
				}
				stats.add(stat);
			}
		}

		for (Stat s : stats)
		{
			out.println(s);
			out.println();
		}

		aggregateAndPrint(out, stats);

		out.flush();
	}

	private void aggregateAndPrint(PrintWriter out, List<Stat> stats)
	{
		Multimap<String, Stat> templates = HashMultimap.create();

		for (Stat s : stats)
		{
			templates.put(s.who, s);
		}

		for (String template : templates.keySet())
		{
			out.println(template);
			DoubleSummaryStatistics wins = templates.get(template).stream().mapToDouble(s -> s.wins).summaryStatistics();
            DoubleSummaryStatistics loses = templates.get(template).stream().mapToDouble(s -> s.losses).summaryStatistics();
            DoubleSummaryStatistics draws = templates.get(template).stream().mapToDouble(s -> s.draws).summaryStatistics();

            DoubleSummaryStatistics all = new DoubleSummaryStatistics();
            all.combine(wins);
            all.combine(loses);
            all.combine(draws);


			out.println(format("Wins:  %.0f Loses:  %.0f Draws: %.0f", wins.getSum() / all.getSum() * 100,
                    loses.getSum() / all.getSum() * 100, draws.getSum() / all.getSum() * 100));
		}
	}

	private void performBattle(Creature c1, Creature c2, Stat stat, DungeonScreen screen, LevelContext level)
	{

		injector.getInstance(EventHolder.class).addEvent(new TimeEvent(0, c1, EventType.MOVE));
		injector.getInstance(EventHolder.class).addEvent(new TimeEvent(0, c2, EventType.MOVE));

		c1.setFaction(0);
		c2.setFaction(1);

		for (int j = 0; j < BATTLE_DURATION; j++)
			screen.makeTick();

		if (level.getCreatures().size() == 2 || level.getCreatures().isEmpty())
			stat.draws++;
		else if (level.getCreatures().get(0).getFaction() == 0)
		{
			stat.wins++;
		}
		else
		{
			stat.losses++;
		}
	}

	@Test
	public void oneCreatureDifferentWeapons()
	{
		CreatureTemplate orc = entityLoader.findCreature("ORC");
		List<Stat> stats = newArrayList();
		for (WeaponBase w1 : entityLoader.weaponBase())
			for (WeaponBase w2 : entityLoader.weaponBase())
			{
				if (w1 == w2) continue;

				int wins = 0, losses = 0, draw = 0;
				Stat stat = new Stat(w1.getName(), w2.getName(), wins, losses, draw);
				for (int i = 0; i < NUMBER_OF_FIGHTS; i++)
				{
					DungeonScreen screen = new DungeonScreen(injector);
					LevelContext level = new LevelContext(DungeonService.TEST_MAP, 0);
					screen.setLevel(level);
					Creature c1 = generatorService.generateCreature(orc, level, point(0, 0));
					Creature c2 = generatorService.generateCreature(orc, level, point(4, 4));
					c1.setEventHolder(injector.getInstance(EventHolder.class));
					c2.setEventHolder(injector.getInstance(EventHolder.class));
					c1.unequip(MAIN_HAND);
					c2.unequip(MAIN_HAND);
                    c1.unequip(EquipmentSlot.OFF_HAND);
                    c2.unequip(EquipmentSlot.OFF_HAND);
					c1.equip(builderFactory.createWeaponBuilder().setBase(w1).construct(), MAIN_HAND);
					c2.equip(builderFactory.createWeaponBuilder().setBase(w2).construct(), MAIN_HAND);

					performBattle(c1, c2, stat, screen, level);
				}
				stats.add(stat);
			}
		PrintWriter out = new PrintWriter(System.out);
		aggregateAndPrint(out, stats);
		out.flush();

	}

	@Test
    public void testShielding()
    {
        CreatureTemplate orc = entityLoader.findCreature("ORC");
        List<Stat> stats = newArrayList();
        for (ShieldBase w1 : entityLoader.shieldBases())
            for (ShieldBase w2 : entityLoader.shieldBases())
            {
                if (w1 == w2) continue;

                int wins = 0, losses = 0, draw = 0;
                Stat stat = new Stat(w1.getName(), w2.getName(), wins, losses, draw);
                for (int i = 0; i < NUMBER_OF_FIGHTS; i++)
                {
                    DungeonScreen screen = new DungeonScreen(injector);
                    LevelContext level = new LevelContext(DungeonService.TEST_MAP, 0);
                    screen.setLevel(level);
                    Creature c1 = generatorService.generateCreature(orc, level, point(0, 0));
                    Creature c2 = generatorService.generateCreature(orc, level, point(4, 4));
                    c1.setEventHolder(injector.getInstance(EventHolder.class));
                    c2.setEventHolder(injector.getInstance(EventHolder.class));
                    c1.unequip(MAIN_HAND);
                    c2.unequip(MAIN_HAND);
                    c1.unequip(EquipmentSlot.OFF_HAND);
                    c2.unequip(EquipmentSlot.OFF_HAND);
                    WeaponBase base = entityLoader.findWeapon("METAL_SWORD");
                    c1.equip(builderFactory.createWeaponBuilder().setBase(base).construct(), MAIN_HAND);
                    c2.equip(builderFactory.createWeaponBuilder().setBase(base).construct(), MAIN_HAND);
                    c1.equip(new Shield(w1, itemFactory.chooseMaterial(base.getMaterialType(), 1)), EquipmentSlot.OFF_HAND);
                    c2.equip(new Shield(w2, itemFactory.chooseMaterial(base.getMaterialType(), 1)), EquipmentSlot.OFF_HAND);
                    performBattle(c1, c2, stat, screen, level);
                }
                stats.add(stat);
            }
        PrintWriter out = new PrintWriter(System.out);
        aggregateAndPrint(out, stats);
        out.flush();
    }

	private static class Stat
	{
		public String who, enemy;
		public int wins, losses, draws;

		private Stat(String who, String enemy, int wins, int losses, int draws)
		{
			this.who = who;
			this.enemy = enemy;
			this.wins = wins;
			this.losses = losses;
			this.draws = draws;
		}

		@Override
		public String toString()
		{
			return "Fight " + who + " v " + enemy + " Wins:" + wins + " Losses:" + losses + " Draws:" + draws;
		}
	}
}

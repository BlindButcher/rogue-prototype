package dungeon;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import creature.Creature;
import creature.CreatureGeneratorService;
import creature.CreatureTemplate;
import main.EventHolder;
import main.LevelContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import util.RandomList;
import util.Utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;
import java.util.logging.Logger;

import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static dungeon.WinCondition.WinConditionType.KILL_MONSTER;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static util.Utils.randomInRange;

/**
 * Contains map sequence, monster set and winning conditions
 * 
 * @author Blind
 */
public class Scenario
{
	private static final Logger MAIN = Logger.getLogger("MAIN");
	private static final Logger LOG = Logger.getLogger(Scenario.class.getName());

	private Map<String, RandomList<String>> generatorList = newHashMap();
	private Map<Integer, Choice> levelMap = newHashMap();
	private Map<Integer, LevelData> levelData = newHashMap();
	private Multimap<Integer, String> reservedBosses = ArrayListMultimap.create();
	private Set<String> bossUsed = newHashSet();
	private DungeonService dungeonService;
	private CreatureGeneratorService creatureGeneratorService;
	private String scenarioLocation;
	private WinCondition winCondition;
    private EventHolder eventHolder;

	@Inject
	public Scenario(DungeonService dungeonService, CreatureGeneratorService creatureGeneratorService, @Named("scenarioFile")
	String scenarioLocation)
	{
		this.scenarioLocation = scenarioLocation;
		this.dungeonService = dungeonService;
		this.creatureGeneratorService = creatureGeneratorService;
		try
		{
			loadScenario();
		} catch (Exception e)
		{
			throw new RuntimeException("Scenario is not loaded", e);
		}
	}

	public LevelContext createLevel(int level)
	{
		Preconditions.checkArgument(levelMap.containsKey(level));

		Choice choice = levelMap.get(level);

        Cells map = clearStairs(
                choice.any ? dungeonService.createDungeon(choice.rows, choice.cols) : dungeonService.createDungeon(choice.mapIds.getItem(), choice.rows,
                        choice.cols), choice);

		LevelContext levelContext = new LevelContext(map, level);

		inhabit(levelContext, level);

		return levelContext;
	}

	private void inhabit(LevelContext levelContext, int level)
	{

		for (String creatureId : reservedBosses.get(level))
		{
			CreatureTemplate creatureTemplate = creatureGeneratorService.getLoader().findCreature(creatureId);

			Preconditions.checkArgument(!bossUsed.contains(creatureTemplate.getId()));

			Preconditions.checkArgument(creatureTemplate.isBoss());

			bossUsed.add(creatureTemplate.getId());

			Creature creature = creatureGeneratorService.generateCreature(creatureTemplate, levelContext, levelContext.getRandomEmptyCell());

			logBorn(creature);
		}

		LevelData data = levelData.get(level);

		for (int i = 0; i < randomInRange(data.getMinCreature(), data.getMaxCreature()); i++)
		{
			Creature creature = creatureGeneratorService.generateCreature(data.getRandomList().getItem(), levelContext, levelContext.getRandomEmptyCell());
			logBorn(creature);
		}

		if (Utils.chance(25))
		{
			Creature creature = creatureGeneratorService.generateBoss(levelContext, levelContext.getRandomEmptyCell(),
					Sets.union(bossUsed, newHashSet(reservedBosses.values())));
			if (creature != null) bossUsed.add(creature.getName());
			logBorn(creature);
		}

        CreatureGenerator strategy = new PlainCreatureGenerator(asList(creatureGeneratorService.getLoader().
                findCreature(data.getRandomList().getItem())), eventHolder);
        levelContext.getDwellings().add(new Dwelling("DWELLING", "Random dwelling", strategy,
                levelContext.getRandomEmptyCell()));

        strategy = new NearCreatureGenerator(asList(creatureGeneratorService.getLoader().
                findCreature(data.getRandomList().getItem())), eventHolder, levelContext);
        levelContext.getDwellings().add(new Dwelling("DWELLING", "Random dwelling", strategy,
                levelContext.getRandomEmptyCell()));
	}

	private void logBorn(Creature creature)
	{
		if (creature != null) MAIN.fine(format("New %s emerges somewhere.", creature.getName()));
	}

	private Cells clearStairs(Cells cells, Choice choice)
	{
		if (choice.first) cells.remove(Cell.UP_STAIRS);
		if (choice.last) cells.remove(Cell.DOWN_STAIRS);
		return cells;
	}

	/**
	 * Checks levelContexts against win conditions.
	 * 
	 * @param levelContexts
	 *            - set of level contexts
	 * @return true in case of victory.
	 */
	public boolean checkWin(List<LevelContext> levelContexts)
	{
		if (winCondition.type == KILL_MONSTER)
		{
			return bossUsed.contains(winCondition.id) && !tryFind(levelContexts, new Predicate<LevelContext>()
			{
				public boolean apply(LevelContext context)
				{
					return context.inhabitByBoss(winCondition.id);
				}
			}).isPresent();
		}
		throw new RuntimeException("Not supported winning conditions");
	}

	public int maxDepth()
	{
		return levelMap.isEmpty() ? 0 : Collections.max(levelMap.keySet());
	}

	public Map<Integer, Choice> getLevelMap()
	{
		return levelMap;
	}

	public Set<String> getBossUsed()
	{
		return newHashSet(bossUsed);
	}

	public WinCondition getWinCondition()
	{
		return winCondition;
	}

	public Multimap<Integer, String> getReservedBosses()
	{
		return ArrayListMultimap.create(reservedBosses);
	}

	public Map<String, RandomList<String>> getGeneratorList()
	{
		return newHashMap(generatorList);
	}

	private void loadScenario() throws Exception
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(getClass().getClassLoader().getResourceAsStream(scenarioLocation));
		Node root = doc.getElementsByTagName("scenario").item(0);
		int rows = Integer.valueOf(root.getAttributes().getNamedItem("defaultRows").getNodeValue());
		int cols = Integer.valueOf(root.getAttributes().getNamedItem("defaultCols").getNodeValue());

		NodeList nodeList = doc.getElementsByTagName("creatureGenerator");
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Node cur = nodeList.item(i);
			Element curElement = (Element) cur;

			NodeList inhabitants = curElement.getElementsByTagName("creature");
			RandomList<String> list = new RandomList<>();
			for (int j = 0; j < inhabitants.getLength(); j++)
			{
				Node curInhabit = inhabitants.item(j);
				list.add(curInhabit.getAttributes().getNamedItem("id").getNodeValue(),
						Integer.valueOf(curInhabit.getAttributes().getNamedItem("weight").getNodeValue()));
			}
			generatorList.put(cur.getAttributes().getNamedItem("id").getNodeValue(), list);
		}

		nodeList = doc.getElementsByTagName("level");
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Node cur = nodeList.item(i);
			int level = Integer.valueOf(cur.getAttributes().getNamedItem("num").getNodeValue());
			boolean any = cur.getAttributes().getNamedItem("any") != null;
			boolean first = cur.getAttributes().getNamedItem("first") != null;
			boolean last = cur.getAttributes().getNamedItem("last") != null;
			String choice = any ? "" : cur.getAttributes().getNamedItem("choice").getNodeValue();
			levelMap.put(level, new Choice(any ? new RandomList<String>() : new RandomList<>(Arrays.asList(choice.split(","))), rows, cols, first, last));

			Element curElement = (Element) cur;

			NodeList inhabitants = curElement.getElementsByTagName("inhabitWithBoss");
			for (int j = 0; j < inhabitants.getLength(); j++)
			{
				reservedBosses.put(level, inhabitants.item(j).getAttributes().getNamedItem("id").getNodeValue());
			}

			NodeList generator = curElement.getElementsByTagName("generator");
			for (int j = 0; j < generator.getLength(); j++)
			{
				Node one = generator.item(j);
				int min = Integer.valueOf(one.getAttributes().getNamedItem("min").getNodeValue());
				int max = Integer.valueOf(one.getAttributes().getNamedItem("max").getNodeValue());
				levelData.put(level, new LevelData(min, max, generatorList.get(one.getAttributes().getNamedItem("ref").getNodeValue())));
			}

			NodeList conditions = curElement.getElementsByTagName("winCondition");
			for (int j = 0; j < conditions.getLength(); j++)
			{
				Preconditions.checkArgument(winCondition == null, "Not unique win condition.");
				winCondition = WinCondition.parse(conditions.item(0));
			}
		}
		LOG.info(format("Loaded: levels - %s, generator - %s", levelMap.size(), levelData.size()));
	}

    @Inject
    public void setEventHolder(EventHolder eventHolder)
    {
        this.eventHolder = eventHolder;
    }
}

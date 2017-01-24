package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import util.AppLogHandler;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main extends StateBasedGame
{

	public final static int NX = 26, NY = 26, H = 32;
	public final static int NL = 8, HL = 20;
	public final static int PW = 400;

	public static void main(String[] args) throws SlickException, FileNotFoundException, IOException
	{

		AppGameContainer app = new AppGameContainer(new Main("Rogue"));

		app.setDisplayMode(NX * H + PW, NY * H + NL * HL, false);
		app.start();
	}

	public Main(String title)
	{
		super(title);
	}

	@Override
	public void initStatesList(GameContainer arg0) throws SlickException
	{
		Injector injector = Guice.createInjector(new AppModule());

		LogManager logManager = LogManager.getLogManager();
		Logger logger = Logger.getLogger("MAIN");
		logManager.addLogger(logger);
		logger.addHandler(injector.getInstance(AppLogHandler.class));
		addState(new DungeonScreen(injector));
	}

	public static enum States
	{
		MAIN_SCREEN;
	}
}

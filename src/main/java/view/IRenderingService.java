package view;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public interface IRenderingService
{
	void render(Graphics g, IRenderable object, float x, float y, Color filter);

	void render(Graphics g, IRenderable object, float x, float y);
}

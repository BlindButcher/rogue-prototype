package view;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import java.util.concurrent.ExecutionException;

public class RenderingService implements IRenderingService
{
    LoadingCache<String, Image> imageCache = CacheBuilder.newBuilder().build(
            new CacheLoader<String, Image>()
            {
                public Image load(String key) throws SlickException
                {
                    return new Image(key);
                }
            });

	public void render(Graphics g, IRenderable object, float x, float y, Color filter)
	{
        Image image;
        try
        {
            image = imageCache.get(object.getImagePath());
        } catch (ExecutionException e)
        {
            throw new RuntimeException(e);
        }

        if (filter != null)
		{
			g.drawImage(image, x, y, filter);
		}
		else
		{
			g.drawImage(image, x, y);
		}
	}

	@Override
	public void render(Graphics g, IRenderable object, float x, float y)
	{
		render(g, object, x, y, null);
	}
}

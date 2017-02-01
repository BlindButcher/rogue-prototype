package inventory;

import view.IRenderable;

public interface Item extends IRenderable
{
	String getName();

	char getC();
}

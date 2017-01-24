package inventory;

import util.INamedEntity;

public interface ItemBase extends INamedEntity
{
	String getName();

    MaterialType getMaterialType();

    char getC();

	String getId();

	String getImagePath();
}

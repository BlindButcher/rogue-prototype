package inventory.shield;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import core.Mechanics;
import inventory.ItemBase;
import inventory.MaterialType;
import loader.EntityLoader;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Set;

import static java.lang.Integer.valueOf;

public class ShieldBase implements ItemBase {
    private final String id;
    private final String name;
    private final int blockFactor;
    private final MaterialType materialType;
    private final int level;
    private final String imagePath;

    private ShieldBase(String id, String name, int blockFactor, MaterialType materialType, int level, String imagePath) {
        this.id = id;
        this.name = name;
        this.blockFactor = blockFactor;
        this.materialType = materialType;
        this.level = level;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public int getBlockValue() {
        return blockFactor;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public char getC() {
        return name.toLowerCase().charAt(0);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getImagePath() {
        return imagePath;
    }

    public static ShieldBase fromNode(Node node, Set<MaterialType> types) {
        String id = node.getAttributes().getNamedItem("id").getNodeValue();
        String name = node.getAttributes().getNamedItem("name").getNodeValue();
        String imagePath = node.getAttributes().getNamedItem("graphic") == null ? null : EntityLoader.SHIELD_PATH + "/" + node.getAttributes().getNamedItem("graphic").getNodeValue();

        Element curElement = (Element) node;
        final String materialId = curElement.getElementsByTagName("materialType").item(0).getTextContent();
        MaterialType materialType = Iterables.find(types, new Predicate<MaterialType>() {
            public boolean apply(MaterialType materialType) {
                return materialType.getId().equals(materialId);
            }
        });

        String[] blockString = curElement.getElementsByTagName("block").item(0).getTextContent().split("/");
        int level = valueOf(curElement.getElementsByTagName("level").item(0).getTextContent());

        return new ShieldBase(id, name, (int) (Mechanics.DEFAULT_BLOCK * (Double.valueOf(blockString[0]) / Double.valueOf(blockString[1]))), materialType, level, imagePath);
    }
}

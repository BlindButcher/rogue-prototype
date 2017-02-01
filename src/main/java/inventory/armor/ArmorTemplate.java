package inventory.armor;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import inventory.EquipStrategy;
import inventory.MaterialType;
import loader.EntityLoader;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Set;

public class ArmorTemplate implements ArmorBase {
    private final String id;
    private final String name;
    private final int armor;
    private final double speedFactor;
    private final MaterialType materialType;
    private final int combatPenalty;
    private final String imagePath;
    private ArmorType type;

    private ArmorTemplate(String id, String name, int armor, double speedFactor, MaterialType materialType, int combatPenalty, String imagePath, ArmorType type) {
        this.name = name;
        this.armor = armor;
        this.speedFactor = speedFactor;
        this.materialType = materialType;
        this.combatPenalty = combatPenalty;
        this.id = id;
        this.imagePath = imagePath;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public char getC() {
        return name.toLowerCase().charAt(0);
    }

    public int getArmor() {
        return armor;
    }

    public double getSpeedFactor() {
        return speedFactor;
    }

    public int getCombatPenalty() {
        return combatPenalty;
    }

    public EquipStrategy getEquipStrategy() {
        return EquipStrategy.ARMOR;
    }

    @Override
    public String getId() {
        return id;
    }

    public static ArmorTemplate fromNode(Node node, Set<MaterialType> types) {
        String id = node.getAttributes().getNamedItem("id").getNodeValue();
        String name = node.getAttributes().getNamedItem("name").getNodeValue();
        Node armorTypeNode = node.getAttributes().getNamedItem("armorType");
        ArmorType type = armorTypeNode == null ? ArmorType.COMMON : ArmorType.valueOf(armorTypeNode.getNodeValue());
        String imagePath = node.getAttributes().getNamedItem("graphic") == null ? null : EntityLoader.ARMOR_PATH + "/" + node.getAttributes().getNamedItem("graphic").getNodeValue();

        Element curElement = (Element) node;
        int value = Integer.valueOf(curElement.getElementsByTagName("armorValue").item(0).getTextContent());
        double bonusFactor = Double.valueOf(curElement.getElementsByTagName("speedFactor").item(0).getTextContent());
        final String materialId = curElement.getElementsByTagName("materialType").item(0).getTextContent();
        MaterialType materialType = Iterables.find(types, new Predicate<MaterialType>() {
            public boolean apply(MaterialType materialType) {
                return materialType.getId().equals(materialId);
            }
        });
        int combatPenalty = Integer.valueOf(curElement.getElementsByTagName("combatPenalty").item(0).getTextContent());

        return new ArmorTemplate(id, name, value, bonusFactor, materialType, combatPenalty, imagePath, type);
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public ArmorType getArmorType() {
        return type;
    }

    public String getImagePath() {
        return imagePath;
    }
}

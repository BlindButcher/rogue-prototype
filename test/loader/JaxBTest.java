package loader;

import inventory.Material;
import inventory.MaterialType;
import org.junit.Test;

import javax.xml.bind.JAXB;
import java.io.StringReader;

import static org.junit.Assert.*;

/**
 * Simple JaxB Testing.
 *
 * @author Blind
 */
public class JaxBTest
{
    @Test
    public void testJaxb()
    {
        MaterialType type = JAXB.unmarshal(new StringReader("<materialType id=\"NATURAL\" name=\"Natural\"/>"),
                MaterialType.class);
        assertNotNull(type);
        assertEquals("NATURAL", type.getId());
        assertEquals("Natural", type.getName());

        MaterialContainer container = JAXB.unmarshal("C:\\Users\\Blind\\Projects\\rogue-prototype\\materials.xml", MaterialContainer.class);

        assertFalse(container.materialTypes.isEmpty());
        assertNotNull(type);
        assertFalse(container.material.isEmpty());

        Material material = JAXB.unmarshal(new StringReader(" <material id=\"ASH\" name=\"Ash\" type=\"WOOD\" tier=\"1\"/>"),
                Material.class);
        assertNotNull(material);
        assertEquals("ASH", material.getId());
        assertEquals("Ash", material.getName());
    }


}

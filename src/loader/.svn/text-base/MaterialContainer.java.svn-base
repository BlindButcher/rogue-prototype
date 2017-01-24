package loader;

import inventory.Material;
import inventory.MaterialType;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Just simple adapter for loading with help of JAXB.
 *
 * @author Blind
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "materials")
public class MaterialContainer
{
    @XmlElementWrapper(name = "materialTypes")
    @XmlElement(name = "materialType")
    public List<MaterialType> materialTypes;

    @XmlElement(name = "material")
    public List<Material> material;
}

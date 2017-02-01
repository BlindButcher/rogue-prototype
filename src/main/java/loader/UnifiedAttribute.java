package loader;

import static java.lang.String.format;

import java.util.Set;

import org.w3c.dom.Node;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import creature.Attribute;

/**
 * Contains unified attributes and their growth.
 * 
 * @author Blind
 */
public class UnifiedAttribute
{
	public static final String CUSTOM = "CUSTOM";

	public final Attribute attribute;
	public final int base;
	public final int growth;
	public final String name;

	public UnifiedAttribute(String name, Attribute attribute, int value, int growth)
	{
		this.attribute = attribute;
		this.base = value;
		this.growth = growth;
		this.name = name;
	}

	public UnifiedAttribute(Attribute attribute, int value, int growth)
	{
		this.attribute = attribute;
		this.base = value;
		this.growth = growth;
		this.name = CUSTOM;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UnifiedAttribute attribute1 = (UnifiedAttribute) o;

		if (attribute != attribute1.attribute) return false;
		if (name != null ? !name.equals(attribute1.name) : attribute1.name != null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = attribute != null ? attribute.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}

	static UnifiedAttribute fromNode(Node node)
	{
		Attribute attr = Attribute.getById(node.getAttributes().getNamedItem("type").getNodeValue());
		int base = Integer.valueOf(node.getAttributes().getNamedItem("base").getNodeValue());
		int growth = Integer.valueOf(node.getAttributes().getNamedItem("growth").getNodeValue());
		String name = node.getAttributes().getNamedItem("name").getNodeValue();
		return new UnifiedAttribute(name, attr, base, growth);
	}

	static UnifiedAttribute fromNode(Node node, Set<UnifiedAttribute> attributes)
	{
		Attribute attr = Attribute.getById(node.getAttributes().getNamedItem("type").getNodeValue());
		if (node.getAttributes().getNamedItem("unified") != null)
		{
			return getUnified(attributes, attr, node.getAttributes().getNamedItem("unified").getNodeValue());
		}

		int base = Integer.valueOf(node.getAttributes().getNamedItem("base").getNodeValue());
		int growth = Integer.valueOf(node.getAttributes().getNamedItem("growth").getNodeValue());
		return new UnifiedAttribute(attr, base, growth);
	}

	static private UnifiedAttribute getUnified(Set<UnifiedAttribute> attributes, final Attribute attr, final String name)
	{
		Optional<UnifiedAttribute> attribute = Iterables.tryFind(attributes, new Predicate<UnifiedAttribute>()
		{
			public boolean apply(UnifiedAttribute unifiedAttribute)
			{
				return unifiedAttribute.attribute == attr && unifiedAttribute.name.equals(name);
			}
		});

		if (!attribute.isPresent()) throw new IllegalArgumentException(format("Such attribute attr:%s, name:%s not found", attr, name));

		return attribute.get();
	}
}

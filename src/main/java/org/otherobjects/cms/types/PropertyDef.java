package org.otherobjects.cms.types;

/**
 * Defines a property of a type.
 * 
 * <p>Currently supported simple property types are:
 * 
 * <ul>
 * <li>string (String.class)
 * <li>text (String.class)
 * <li>date (Date.class)
 * <li>time (Date.class)
 * <li>timestamp (Date.class)
 * <li>boolean (Boolean.class)
 * <li>number (Long.class Integer.class)
 * <li>decimal (BigDecimal.class, Float.class, Double.class) -- specify decimal places in format -- stored as double
 * </ul>
 * 
 * <p>Currently supported bean property types are:
 * 
 * <ul>
 * <li>component
 * <li>reference
 * </ul>
 * 
 * <p>Components persist independently and can't be saved or deleted indepently of the CmsNode they belong to. 
 * Can be multiple levels deep.
 * 
 * <p>Currently supported collection property types (which can be used with any of the above) are:
 * 
 * <ul>
 * <li>list (must be ArrayList?)
 * </ul>
 * 
 * <p>Note that collection elements must always be of the same property type.
 * 
 * @author rich
 *
 */
public class PropertyDef
{
    /** Property name. */
    private String name;

    /** Defines type ofthis property. */
    private String type;

    /** Type of collecion if this property can have multipe values. */
    private String collectionType;

    /** Type of reference or component.  */
    private String relatedType;

    /** To specify format for decimals. TODO Maybe others? */
    private String format;

    /** Human friendly name for property. Can be inferred from name */
    private String label;

    /** Description for this property. */
    private String description;

    /** Help text to assist choosing value for this property. */
    private String help;

    public PropertyDef(String name, String propertyType, String relatedType, String collectionType)
    {
        setName(name);
        setType(propertyType);
        setRelatedType(relatedType);
        setCollectionType(collectionType);
    }

    @Override
    public String toString()
    {
        return getName();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getRelatedType()
    {
        return relatedType;
    }

    public void setRelatedType(String relatedType)
    {
        this.relatedType = relatedType;
    }

    public String getFormat()
    {
        return format;
    }

    public void setFormat(String format)
    {
        this.format = format;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getHelp()
    {
        return help;
    }

    public void setHelp(String help)
    {
        this.help = help;
    }

    public String getCollectionType()
    {
        return collectionType;
    }

    public void setCollectionType(String collectionType)
    {
        this.collectionType = collectionType;
    }

}
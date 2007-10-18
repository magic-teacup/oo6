package org.otherobjects.cms.model;

import java.util.List;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

@Type
public class Template extends BaseNode
{
    private String code;
    private String label;
    private TemplateLayout layout;
    private List<TemplateRegion> regions;

    @Property(order = 10)
    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    @Property(order = 20)
    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    
    @Property(order = 30)
    public TemplateLayout getLayout()
    {
        return layout;
    }

    public void setLayout(TemplateLayout layout)
    {
        this.layout = layout;
    }

    @Property(order = 40, collectionElementType=PropertyType.COMPONENT)
    public List<TemplateRegion> getRegions()
    {
        return regions;
    }

    public void setRegions(List<TemplateRegion> regions)
    {
        this.regions = regions;
    }


}
package org.otherobjects.cms.model;

import org.otherobjects.cms.Url;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.util.StringUtils;

import flexjson.JSON;

/**
 * FIXME Name should reflect abstract status
 * @author rich
 *
 */
public abstract class SitePage extends BaseNode implements Linkable
{
    // FIXME Move to PublishingOptions object?
    private Template template;

    @Override
    public String getCode()
    {
        return StringUtils.generateUrlCode(getLabel()) + ".html";
    }

    @Property(order = 100)
    public Template getTemplate()
    {
        return template;
    }

    public void setTemplate(Template template)
    {
        this.template = template;
    }

    @JSON(include = false)
    public Url getHref()
    {
        return new Url(getLinkPath());
    }

}

package org.otherobjects.cms.model;

import java.util.List;

import org.otherobjects.cms.jcr.GenericJcrDaoJackrabbit;
import org.otherobjects.cms.site.Tree;
import org.otherobjects.cms.site.TreeBuilder;

/**
 * TODO Merge with NavigationSrevice 
 * @author rich
 */
public class FolderDaoImpl extends GenericJcrDaoJackrabbit<SiteFolder> implements FolderDao
{
    public FolderDaoImpl()
    {
        super(SiteFolder.class);
    }

    public List<SiteFolder> getFolders()
    {
        // FIXME Need folder indicator
        return getAllByJcrExpression("/jcr:root//element(*) [jcr:like(@ooType,'%Folder')]");
    }
    
    @SuppressWarnings("unchecked")
    public Tree getFolderTree()
    {
        TreeBuilder tb = new TreeBuilder();
        List all = getFolders();
        return tb.buildTree(all);
    }
}

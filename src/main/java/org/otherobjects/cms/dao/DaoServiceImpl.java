package org.otherobjects.cms.dao;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.model.BaseNode;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

@SuppressWarnings("unchecked")
public class DaoServiceImpl implements DaoService, BeanFactoryAware
{
    private static final String UNIVERSAL_JCR_DAO_KEY = "universalJcrDao";
    private Map<String, GenericDao> daoMap;
    private BeanFactory beanFactory;

    public GenericDao getDao(Class clazz)
    {
        return getDao(clazz.getName());
    }

    public GenericDao getDao(String type)
    {
        GenericDao dao = daoMap.get(type);
        if (dao == null)
        {

            String daoBeanName = determineDaoBeanName(type);
            if (beanFactory.containsBean(daoBeanName))
                dao = (GenericDao) beanFactory.getBean(daoBeanName);
            else
                // If no specific dao found then use dynaNode Dao
                dao = (GenericDao) beanFactory.getBean(UNIVERSAL_JCR_DAO_KEY);
        }

        // FIXME Return UniversalJcrDao
        if (dao == null)
        {
            // If no specific dao found then use dynaNode Dao
            dao = (GenericDao) beanFactory.getBean(UNIVERSAL_JCR_DAO_KEY);
        }
        return dao;
    }

    /**
     * Determines the conventional bean name for this type's DAO based on the type name.
     * The bean name should be the non-qualified type name, starting lowercase and appended
     * with "Dao". 
     * <p>For example <code>org.example.ProductCategory</code> should have a DAO bean name
     * of <code>productCategoryDao</code>.  
     * @param type
     * @return
     */
    protected String determineDaoBeanName(String type)
    {
        String beanName = type;

        // Make non-qualified
        if (type.contains("."))
            beanName = StringUtils.substringAfterLast(beanName, ".");

        // Lower case first letter
        beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);

        // Append Dao
        beanName = beanName + "Dao";

        return beanName;
    }

    public Map<String, GenericDao> getDaoMap()
    {
        return daoMap;
    }

    public void setDaoMap(Map<String, GenericDao> daoMap)
    {
        this.daoMap = daoMap;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException
    {
        this.beanFactory = beanFactory;
    }

    public boolean hasDao(String type)
    {
        GenericDao dao = daoMap.get(type);

        Class typeClass = null;
        try
        {
            typeClass = Class.forName(type);
        }
        catch (ClassNotFoundException e)
        {
        }

        if (dao == null)
        {
            String daoBeanName = determineDaoBeanName(type);
            if (beanFactory.containsBean(daoBeanName))
                dao = (GenericDao) beanFactory.getBean(daoBeanName);
            else if (typeClass != null && BaseNode.class.isAssignableFrom(typeClass))
            {
                // If no specific dao found then use dynaNode Dao
                dao = (GenericDao) beanFactory.getBean(UNIVERSAL_JCR_DAO_KEY);
            }
        }

        return (dao != null);
    }
}

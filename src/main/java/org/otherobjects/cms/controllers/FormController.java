package org.otherobjects.cms.controllers;import java.text.DateFormat;import java.util.Iterator;import java.util.Map;import javax.servlet.http.HttpServletRequest;import javax.servlet.http.HttpServletResponse;import net.sf.cglib.beans.BeanGenerator;import org.apache.commons.beanutils.PropertyUtils;import org.otherobjects.cms.dao.DynaNodeDao;import org.otherobjects.cms.model.DynaNode;import org.otherobjects.cms.types.JcrTypeServiceImpl;import org.otherobjects.cms.types.PropertyDef;import org.otherobjects.cms.types.TypeDef;import org.otherobjects.cms.types.TypeService;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.beans.propertyeditors.CustomDateEditor;import org.springframework.util.Assert;import org.springframework.web.bind.ServletRequestDataBinder;import org.springframework.web.servlet.ModelAndView;import org.springframework.web.servlet.mvc.Controller;/** * Controller to process form submission. Only data for types registered in the TypeService is supported. * * @author rich */public class FormController implements Controller{    private Logger logger = LoggerFactory.getLogger(FormController.class);        private DynaNodeDao dynaNodeDao;    private TypeService typeService;    @SuppressWarnings("unchecked")    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception    {        try        {            // Get form info            String id = request.getParameter("id");            String typeName = request.getParameter("ooType");            Assert.notNull(typeName, "Type must be provided in form data.");            TypeDef type = typeService.getType(typeName);            Assert.notNull(type, "Type not found:" + typeName);                                                            // Load existing object            DynaNode dynaNode;            // create a proper bean from the type info            dynaNode = createCustomDynaNodeBean(type);            Assert.notNull(id, "We currently only support edits, so id need to be there");                        if(id != null)            {                logger.info("Populating existing object: {} ({})", id, typeName);                DynaNode persistentDynaNode = dynaNodeDao.get(id);                // copy the persistent data from the data map to the bean style properties                copyDynamicProperties(persistentDynaNode, dynaNode);                                bindParameters(dynaNode, request);                                //before saving we need to copy back bean style properties to dynamic data map                copyBeanProperties(dynaNode, persistentDynaNode, type);                // Save new object                dynaNodeDao.save(persistentDynaNode);            }                                                //                String name = (String) parameterNames.nextElement();//                String value = request.getParameter(name);//                name = name.replaceAll("data\\.", "");////                PropertyDef property = typeDef.getProperty(name);//                if (name.endsWith(":BOOLEAN"))//                {//                    name = name.substring(0, name.length() - 8);//                    if (!data.containsKey(name))//                        data.put(name, false);//                }//                else if (property != null)//                {//                    if (value == null || value.length() == 0)//                        data.put(name, null);//                    else//                    {//                        Object convert = ConvertUtils.convert(value, property.getDataClass());//                        data.put(name, convert);//                    }//                }//            }////            // Validate//            // TODO add in commons validator too?//            Errors errors = new MapBindingResult(new HashMap(), "data");////            if (typeDef.getValidators() != null)//            {//                ValangValidator v = new ValangValidator();//                v.setValang(typeDef.getValidators());//                v.afterPropertiesSet();////                for (Iterator iter = v.getRules().iterator(); iter.hasNext();)//                {//                    ValidationRule rule = (ValidationRule) iter.next();//                    rule.validate(data, errors);//                }//            }////            // We have errors so return error messages//            ModelAndView view = new ModelAndView("jsonView");//            if (errors.getErrorCount() > 0)//            {//                List<Object> jsonErrors = new ArrayList<Object>();//                for (FieldError e : (List<FieldError>) errors.getFieldErrors())//                {//                    Map<String, String> error = new HashMap<String, String>();//                    error.put("id", e.getField());//                    error.put("msg", e.getDefaultMessage());//                    jsonErrors.add(error);//                }//                view.getModel().put("success", false);//                view.getModel().put("errors", jsonErrors);//                return view;//            }////            // All good so save the data        //            getJcrService().getTemplate().execute(new JcrCallback()//            {//                public Object doInJcr(Session session) throws RepositoryException//                {//                    Node node = session.getNodeByUUID(id);////                    for (String name : data.keySet())//                    {//                        if (data.get(name) != null)//                            node.setProperty(name, JcrService.createValue(typeDef.getProperty(name).getType(), data.get(name)));//                    }////                    if (data.containsKey("name"))//                    {//                        String oldPath = node.getPath();//                        String newPath = node.getParent().getPath() + "/" + data.get("name");//                        if (!newPath.equals(oldPath))//                            session.move(oldPath, newPath);//                    }//                    else if (data.containsKey("title"))//                    {//                        String oldPath = node.getPath();//                        String newPath = node.getParent().getPath() + "/" + StringUtils.generateUrlCode("" + data.get("title")) + ".html";//                        if (!newPath.equals(oldPath))//                            session.move(oldPath, newPath);//                    }//                    session.save();//                    return null;//                }//            });////            view.getModel().put("success", true);////            // If type has changed re-register types//            if (type.equals("PropertyDef"))//                getJcrService().registerAllowedTypes("");////            if (type.equals("Script"))//                getJcrService().registerEvents("");            return null;        }        catch (Exception e)        {            ModelAndView view = new ModelAndView("jsonView");            view.getModel().put("success", false);            view.getModel().put("message", e.getMessage());            logger.error("Error saving form data.", e);            return view;        }    }    /**     * use Springs data binding infrastructure with a custom date editor initialised for the default locale     * @param dynaNode     * @param request     */    private void bindParameters(DynaNode dynaNode, HttpServletRequest request) {		ServletRequestDataBinder binder = new ServletRequestDataBinder(dynaNode);		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(DateFormat.getDateInstance(DateFormat.MEDIUM), true));		binder.bind(request);	}	private void copyBeanProperties(DynaNode fromNode, DynaNode toNode, TypeDef type) {		for(Iterator<PropertyDef> it = type.getProperties().iterator(); it.hasNext();)		{			PropertyDef propertyDef = it.next();						try {				toNode.set(propertyDef.getName(), PropertyUtils.getNestedProperty(fromNode, propertyDef.getName()));			} catch (Exception e) {				logger.warn("Couldn't copy property " + propertyDef.getName(), e);			} 		}	}	private void copyDynamicProperties(DynaNode fromNode, DynaNode toBean) {		for(Iterator<String> it = fromNode.getData().keySet().iterator(); it.hasNext();)		{			String property = it.next();			try {				PropertyUtils.setNestedProperty(toBean, property, fromNode.get(property));			} catch (Exception e) {				logger.warn("Couldn't copy property " + property, e);			} 		}		toBean.setId(fromNode.getId());		toBean.setPath(fromNode.getPath());		toBean.setCode(fromNode.getCode());	}	private DynaNode createCustomDynaNodeBean(TypeDef type) {		BeanGenerator beanGenerator = new BeanGenerator();		beanGenerator.setSuperclass(DynaNode.class);				Assert.isInstanceOf(JcrTypeServiceImpl.class, typeService, "Only works if injected typeService is a JcrTypeServiceImpl");				JcrTypeServiceImpl jcrTypeService = (JcrTypeServiceImpl) typeService;		Map<String, Class<?>> jcrClassMappings = jcrTypeService.getJcrClassMappings();		for(Iterator<PropertyDef> it = type.getProperties().iterator(); it.hasNext();)		{			PropertyDef propertyDef = it.next();			Assert.doesNotContain(propertyDef.getName(), ".", "There is currently no mechanism to create nested properties");			beanGenerator.addProperty(propertyDef.getName(), jcrClassMappings.get(propertyDef.getType()));		}		//TODO what do we do about nested properties?		DynaNode dynaNode = (DynaNode) beanGenerator.create();		dynaNode.setOoType(type.getName());				return dynaNode;	}	public DynaNodeDao getDynaNodeDao()    {        return dynaNodeDao;    }    public void setDynaNodeDao(DynaNodeDao dynaNodeDao)    {        this.dynaNodeDao = dynaNodeDao;    }    public TypeService getTypeService()    {        return typeService;    }    public void setTypeService(TypeService typeService)    {        this.typeService = typeService;    }}
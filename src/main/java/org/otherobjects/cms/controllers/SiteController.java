package org.otherobjects.cms.controllers;import java.io.IOException;import javax.servlet.ServletException;import javax.servlet.http.HttpServletRequest;import javax.servlet.http.HttpServletResponse;import javax.servlet.http.HttpSession;import org.otherobjects.cms.SingletonBeanLocator;import org.otherobjects.cms.dao.DynaNodeDao;import org.otherobjects.cms.dao.UserDao;import org.otherobjects.cms.model.DynaNode;import org.otherobjects.cms.tools.MessageTool;import org.springframework.context.MessageSource;import org.springframework.web.servlet.ModelAndView;import org.springframework.web.servlet.mvc.Controller;public class SiteController implements Controller{    //private final Logger logger = LoggerFactory.getLogger(SiteController.class);    private DynaNodeDao dynaNodeDao;    private UserDao userDao;    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException    {        // Clean up path        String path = request.getPathInfo();        if (path.length() > 1 && path.endsWith("/"))            path = path.substring(0, path.length() - 1);        // Set default page for directories        if (path.endsWith("/"))            path = path + "index.html";        MessageSource messageSource = (MessageSource) SingletonBeanLocator.getBean("messageSource");        MessageTool messageTool = new MessageTool();        messageTool.setMessageSource(messageSource);//        for (int i = 0; i < 5; i++)//        {//            DynaNode f1 = new DynaNode("Folder");//            f1.setCode("folder" + i);//            f1.setPath("/site/");//            f1.set("name", "folder" + i);//            f1.set("label", "Folder " + i);//            dynaNodeDao.save(f1);////            for (int j = 0; j < 100; j++)//            {//                DynaNode a1 = new DynaNode("Article");//                a1.setCode("article" + new Date().getTime());//                a1.setPath(f1.getJcrPath()+"/");//                a1.set("title", "Article" + new Date().getTime());//                dynaNodeDao.save(a1);//            }////            for (int k = 0; k < 5; k++)//            {//                DynaNode f2 = new DynaNode("Folder");//                f2.setCode("folder" + k);//                f2.setPath(f1.getJcrPath()+"/");//                f2.set("name", "folder" + k);//                f2.set("label", "Folder " + k);//                dynaNodeDao.save(f2);////                for (int j = 0; j < 10; j++)//                {//                    DynaNode a1 = new DynaNode("Article");//                    a1.setCode("article" + new Date().getTime());//                    a1.setPath(f2.getJcrPath()+"/");//                    a1.set("title", "Article" + new Date().getTime());//                    dynaNodeDao.save(a1);//                }////            }//        }        DynaNode resourceObject = dynaNodeDao.getByPath("/site" + path);        //        // Fetch corresponding site resource        //        Resource resource = (Resource) getWorkbenchService().getResource("/site" + path);        //        String type = resource.getType().toLowerCase();        //                //        Resource template = (Resource) getWorkbenchService().getResource("/designer/templates/" + type + ".html");        //        Resource layout = (Resource) template.getData().get("layout");        //                //        ModelAndView view = new ModelAndView("/templates/layouts/" + layout.getName());        //        view.addObject("jcr", jcrTool);        //        view.addObject("resourceObject", getWorkbenchService().getResource("/site" + path));        //        view.addObject("folders", getWorkbenchService().getAllFolders("/site"));        //        view.addObject("templateName", template.getName());        //        return view;        //                //        // Update session counter        HttpSession session = request.getSession();        Integer counter = (Integer) session.getAttribute("counter");        if (counter == null)            counter = 0;        //        session.setAttribute("counter", ++counter);        //                //        if (path.length() > 0 && path.endsWith("/"))        //            path = path.substring(0, path.length() - 1);        //logger.info("SpringappController: " + path);        //        Resource resource = (Resource) getWorkbenchService().getResource("/site" + path);        //        String type = resource.getType().toLowerCase();        //                //        Resource template = (Resource) getWorkbenchService().getResource("/designer/templates/" + type + ".html");        //        Resource layout = (Resource) template.getData().get("layout");        //                ModelAndView view = new ModelAndView("/site.resources/templates/layout.html");        view.addObject("counter", counter);        view.addObject("message", messageTool);        view.addObject("resourceObject", resourceObject);        view.addObject("userDao", userDao);        //        view.addObject("resourceObject", getWorkbenchService().getResource("/site" + path));        //        view.addObject("folders", getWorkbenchService().getAllFolders("/site"));        //        view.addObject("templateName", template.getName());        return view;    }    public UserDao getUserDao()    {        return userDao;    }    public void setUserDao(UserDao userDao)    {        this.userDao = userDao;    }    public DynaNodeDao getDynaNodeDao()    {        return dynaNodeDao;    }    public void setDynaNodeDao(DynaNodeDao dynaNodeDao)    {        this.dynaNodeDao = dynaNodeDao;    }}
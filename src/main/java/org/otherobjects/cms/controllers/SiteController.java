package org.otherobjects.cms.controllers;import java.io.IOException;import java.util.List;import javax.servlet.ServletException;import javax.servlet.http.HttpServletRequest;import javax.servlet.http.HttpServletResponse;import javax.servlet.http.HttpSession;import org.otherobjects.cms.dao.DynaNodeDao;import org.otherobjects.cms.dao.UserDao;import org.otherobjects.cms.model.DynaNode;import org.otherobjects.cms.workbench.NavigatorService;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.util.Assert;import org.springframework.web.servlet.ModelAndView;import org.springframework.web.servlet.mvc.Controller;public class SiteController implements Controller{    private final Logger logger = LoggerFactory.getLogger(SiteController.class);    private NavigatorService navigatorService;    private DynaNodeDao dynaNodeDao;    private UserDao userDao;    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException    {        // Make sure folders end with slash         String path = request.getPathInfo();        logger.info("Requested resource: {}", path);        if (path == null)            path = "/";        else if (path.length() > 1 && !path.contains(".") && !path.endsWith("/"))            path = path + "/";        DynaNode resourceObject = null;        if (path.contains("."))        {            // Page requested            resourceObject = dynaNodeDao.getByPath("/site" + path);        }        else        {            // Folder requested. Get first item that isn't a folder.            List<DynaNode> contents = dynaNodeDao.getAllByPath("/site" + path);            for (DynaNode n : contents)            {                if (!n.getOoType().equals("Folder"))                {                    resourceObject = contents.get(0);                    break;                }            }            Assert.notNull(resourceObject, "No resources in this folder.");        }        // Update session counter        HttpSession session = request.getSession();        Integer counter = (Integer) session.getAttribute("counter");        if (counter == null)            counter = 0;        session.setAttribute("counter", ++counter);        // Return page and context        ModelAndView view = new ModelAndView("/site.resources/templates/layout.html");        view.addObject("counter", counter);        view.addObject("resourceObject", resourceObject);        view.addObject("sessionId", session.getId());        view.addObject("navigatorService", navigatorService);        view.addObject("userDao", userDao);        return view;    }    public UserDao getUserDao()    {        return userDao;    }    public void setUserDao(UserDao userDao)    {        this.userDao = userDao;    }    public DynaNodeDao getDynaNodeDao()    {        return dynaNodeDao;    }    public void setDynaNodeDao(DynaNodeDao dynaNodeDao)    {        this.dynaNodeDao = dynaNodeDao;    }    public NavigatorService getNavigatorService()    {        return navigatorService;    }    public void setNavigatorService(NavigatorService navigatorService)    {        this.navigatorService = navigatorService;    }}
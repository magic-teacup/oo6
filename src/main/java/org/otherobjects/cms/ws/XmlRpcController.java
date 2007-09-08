package org.otherobjects.cms.ws;import java.util.HashMap;import javax.servlet.http.HttpServletRequest;import javax.servlet.http.HttpServletResponse;import org.springframework.beans.factory.InitializingBean;import org.springframework.web.servlet.ModelAndView;import org.springframework.web.servlet.mvc.AbstractController;import redstone.xmlrpc.XmlRpcServer;import redstone.xmlrpc.interceptors.DebugInvocationInterceptor;public class XmlRpcController extends AbstractController implements InitializingBean{    XmlRpcServer server = new XmlRpcServer();    HashMap<String, Object> handlers = new HashMap<String, Object>();    @Override    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception    {        this.server.execute(request.getInputStream(), response.getWriter());        return null;    }    public void setHandlers(HashMap<String, Object> handlers)    {        this.handlers = handlers;    }    public void afterPropertiesSet() throws Exception    {        for (String key : this.handlers.keySet())        {            Object value = this.handlers.get(key);            this.server.addInvocationHandler(key, value);            this.server.addInvocationInterceptor(new DebugInvocationInterceptor());        }    }}
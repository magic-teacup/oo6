package org.otherobjects.cms.jcr;

import javax.jcr.Credentials;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.otherobjects.cms.security.SecurityUtil;
import org.springmodules.jcr.jackrabbit.ocm.JackrabbitSessionFactory;

/**
 * This class overrides {@link JackrabbitSessionFactory} / {@link org.springmodules.jcr.JcrSessionFactory} respectively just to allow for 
 * creating sessions for different workspaces depending on the role(s) the current user has.
 * It also adds a getSession(String workspaceName) method to allow for explicitly obtaining sessions for specific workspace. 
 * @author joerg
 *
 */
public class OtherObjectsJackrabbitSessionFactory extends JackrabbitSessionFactory
{

    public static final String LIVE_WORKSPACE_NAME = "live";
    public static final String EDIT_WORKSPACE_NAME = "default";
    
    private String workspaceName;

    private Credentials credentials;

    @Override
    public Session getSession() throws RepositoryException
    {
        return getSession(SecurityUtil.isEditor() ? EDIT_WORKSPACE_NAME : LIVE_WORKSPACE_NAME);
    }

    public Session getSession(String workspaceName) throws RepositoryException
    {
        return addListeners(getRepository().login(credentials, workspaceName));
    }


    @Override
    public String getWorkspaceName()
    {
        return workspaceName;
    }

    @Override
    public void setWorkspaceName(String workspaceName)
    {
        super.setWorkspaceName(workspaceName);
        this.workspaceName = workspaceName;
    }

    @Override
    public Credentials getCredentials()
    {
        return credentials;
    }

    @Override
    public void setCredentials(Credentials credentials)
    {
        super.setWorkspaceName(workspaceName);
        this.credentials = credentials;
    }

}

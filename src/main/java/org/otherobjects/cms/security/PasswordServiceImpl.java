package org.otherobjects.cms.security;

import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.dao.UserDao;
import org.otherobjects.cms.model.PasswordChangeRequest;
import org.otherobjects.cms.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

public class PasswordServiceImpl extends HibernateDaoSupport implements PasswordService
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    // Set max age to 24 hours
    public static final long MAXIMUM_TOKEN_AGE = 24 * 60 * 60 * 1000;

    @Resource
    private UserDao userDao;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private SaltSource saltSource;

    public String generatePasswordChangeRequestCode(String username) throws Exception
    {
        User user = (User) userDao.loadUserByUsername(username);
        return generatePasswordChangeRequestCode(user);
    }

    @Transactional
    public String generatePasswordChangeRequestCode(User user) throws Exception
    {
        //cleanExpiredPasswordChangeRequests();
        PasswordChangeRequest pcr = new PasswordChangeRequest();
        pcr.setUser(user);
        pcr.setRequestDate(new Date());
        pcr.setToken(generateToken());
        getHibernateTemplate().save(pcr);
        return pcr.getChangeRequestCode();
    }

    /**
     * Generates a hopefully sufficiently random token by generating a random number merging it with the current date, 
     * then hashing and base64 encoding it for easy transport via e.g email
     * @return
     * @throws Exception 
     */
    protected String generateToken() throws Exception
    {
        Random random = new Random();
        Double rdbl = random.nextDouble();
        // create 10 digit int from that random double
        long rlong = (long) (rdbl * 10000000000L);

        String rawtoken = "" + rlong + new Date().getTime();

        MessageDigest md = MessageDigest.getInstance("SHA");

        byte[] digest = md.digest(rawtoken.getBytes("UTF-8"));

        return new String(Base64.encodeBase64(digest));

    }

    @SuppressWarnings("unchecked")
    @Transactional
    public PasswordChangeRequest getPasswordChangeRequest(String changeRequestCode)
    {
        Assert.notNull(changeRequestCode, "No changeRequestCode provided");

        Object[] identifier = PasswordChangeRequest.splitPasswordChangeRequestIdentifier(changeRequestCode);

        // Get most recent requests
        List pcrs = getHibernateTemplate().find("FROM PasswordChangeRequest WHERE id=? AND token=? ORDER BY id DESC", identifier);

        PasswordChangeRequest passwordChangeRequest = null;

        if (pcrs.size() == 0)
            return null;
        else
            passwordChangeRequest = (PasswordChangeRequest) pcrs.get(0);

        Date now = new Date();

        // If the token is too old we can't change pwd
        if ((now.getTime() - passwordChangeRequest.getRequestDate().getTime()) > MAXIMUM_TOKEN_AGE)
            return null;

        return passwordChangeRequest;

    }

    public boolean validateChangeRequestCode(String changeRequestCode)
    {
        try
        {
            PasswordChangeRequest changeRequest = getPasswordChangeRequest(changeRequestCode);
            return (changeRequest != null);
        }
        catch (IllegalArgumentException e)
        {
            // If any assertions fail then non-valid
            return false;
        }
    }
    
    @Transactional
    public boolean changePassword(User user, String oldPassword, String newPassword)
    {
        try
        {
            user.setPassword(passwordEncoder.encodePassword(newPassword, saltSource.getSalt(user)));
            userDao.save(user);
            return true;
        }
        catch (Exception e)
        {
            throw new OtherObjectsException("Error occured whilst changing password.", e);
        }
    }

    @Transactional
    public boolean changePassword(PasswordChanger passwordChanger)
    {
        try
        {
            // TODO Revalidate password here
            PasswordChangeRequest passwordChangeRequest = getPasswordChangeRequest(passwordChanger.getChangeRequestCode());

            if (passwordChangeRequest == null)
                throw new OtherObjectsException("No valid password change request.");

            // Encode and store new password
            User user = passwordChangeRequest.getUser();
            user.setPassword(hashPassword(passwordChanger.getNewPassword(), user));
            userDao.save(user);

            // If we've gotten this far then we can safely delete all PCRs for this user
            getHibernateTemplate().bulkUpdate("DELETE PasswordChangeRequest WHERE user = ?", user);
            return true;
        }
        catch (Exception e)
        {
            throw new OtherObjectsException("Error occured whilst changing password.", e);
        }
    }

    private String hashPassword(String password, User user)
    {
        return passwordEncoder.encodePassword(password, saltSource.getSalt(user));
    }

    /**
     * TODO Run this nightly via scheduled task.
     */
    @Transactional
    public void cleanExpiredPasswordChangeRequests()
    {
        final Date maximumValidAge = new Date(new Date().getTime() - MAXIMUM_TOKEN_AGE);
        getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException, SQLException
            {
                Query q = session.createQuery("delete from PasswordChangeRequest where requestDate < :requestDate");
                q.setDate("requestDate", maximumValidAge);
                q.executeUpdate();
                return null;
            }
        });

    }

    public boolean isPasswordMatch(User user, String password)
    {
        String hash = hashPassword(password, user);
        return user.getPassword().equals(hash);
    }
}
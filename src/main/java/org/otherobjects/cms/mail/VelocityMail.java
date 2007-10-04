package org.otherobjects.cms.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.OtherObjectsException;

public class VelocityMail {
	
	/** A TO recipient. */
    private final static int TO = 0;

    /** A CC recipient. */
    private final static int CC = 1;

    /** A BCC recipient. */
    private final static int BCC = 2;

	
	/**
     * My thoughts on a valid email address:
     * - doesn't start with a dot or an @
     * - than any word character including dot and hyphen (at least one)
     * - than exactly one @
     * - than any number of word characters (at least one) 
     * - exactly one dot
     * - than any number of word characters including dot and hyphen (at least one) 
     */
    private static String emailRegex = "^[\\w&&[^.@]]{1}[\\w.\\-]+@{1}[\\w\\-]+\\.{1}[\\w.\\-]+";
    private static Pattern pattern = Pattern.compile(emailRegex);
    
	/** Email TO recipients. */
    private List<EmailAddress> toRecipients = new ArrayList<EmailAddress>();

    /** Email CC recipients. */
    private List<EmailAddress> ccRecipients = new ArrayList<EmailAddress>();

    /** Email BCC recipients. */
    private List<EmailAddress> bccRecipients = new ArrayList<EmailAddress>();
    
    private EmailAddress fromAddress;
    
    private EmailAddress replyToAddress;
    
    /** The subject of the message. */
    private String subject;

    /** The body of the message. */
    private String body;
    
    /** resource path of velocity template to be used for plain text body */
    private String bodyTemplateResourcePath;

    /** html version of the body */
    private String htmlBody;
    
    /** resource path of velocity template to be used for html body */
    private String htmlBodyTemplateResourcePath;
    
    /** Map to be used as the model/context to render out velocity templates */
    private Map<String, Object> model;
    
    /** number of columns to have in plain text body */
    private int wrapColumn = 0;
    
    /** A List of MimeBodyParts to send as attachments */
    private List<String> attachments = new ArrayList<String>();
    
    
    public boolean hasAttachments()
    {
    	return attachments.size() > 0;
    }
    
    public boolean hasBody()
    {
    	return StringUtils.isNotBlank(body) || (StringUtils.isNotBlank(bodyTemplateResourcePath) && model != null);
    }
    
    public boolean hasHtmlBody()
    {
    	return StringUtils.isNotBlank(htmlBody) || (StringUtils.isNotBlank(htmlBodyTemplateResourcePath) && model != null);
    }
    
    /**
     * Checks, if VelocityEmail is initialised correctly.
     * 
     * Values that have to be set at minimum:
     * - to-recipient
     * - from-address
     * - attachment or body or enough data to construct a body
     */
    public boolean isValid()
    {
    	if(toRecipients.isEmpty())
    		return false;
    	
    	if(fromAddress == null)
    		return false;
    	
    	if(attachments.isEmpty() && 
    			(StringUtils.isBlank(body) && (model == null || StringUtils.isBlank(bodyTemplateResourcePath))) &&
    			(StringUtils.isBlank(htmlBody) && (model == null || StringUtils.isBlank(htmlBodyTemplateResourcePath))))
    		return false;
    	
    	return true;
    }
    
    private void validateEmailAddress(EmailAddress emailAddress)
    {
    	if(StringUtils.isBlank(emailAddress.getEmail()))
    		throw new OtherObjectsException("Email: \"" + emailAddress.getName() + "\" <" + emailAddress.getEmail() + "> is not a valid email address!");
    	
    	if(!pattern.matcher(emailAddress.getEmail()).matches())
    		throw new OtherObjectsException("Email: \"" + emailAddress.getName() + "\" <" + emailAddress.getEmail() + "> is not a valid email address!");
    		
    	try {
			InternetAddress address;
			if(StringUtils.isBlank(emailAddress.getName()))
				address = new InternetAddress(emailAddress.getEmail());
			else
				address = new InternetAddress(emailAddress.getEmail(), emailAddress.getName());
		} catch (Exception e) {
			throw new OtherObjectsException("Email: \"" + emailAddress.getName() + "\" <" + emailAddress.getEmail() + "> is not a valid email address!");
		} 
    }
    
    
    public void addToRecipient(EmailAddress emailAddress)
    {
    	addRecipient(emailAddress, VelocityMail.TO);
    }
    
    public void addCcRecipient(EmailAddress emailAddress)
    {
    	addRecipient(emailAddress, VelocityMail.CC);
    }
    
    public void addBccRecipient(EmailAddress emailAddress)
    {
    	addRecipient(emailAddress, VelocityMail.BCC);
    }

    
    private void addRecipient(EmailAddress emailAddress, int type)
    {
    	validateEmailAddress(emailAddress);
    	
    	switch(type){
    		case VelocityMail.BCC : 
    			bccRecipients.add(emailAddress);
    			break;
    		case VelocityMail.CC :
    			ccRecipients.add(emailAddress);
    			break;
    		default :
    			toRecipients.add(emailAddress);
    	}
    }

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getBodyTemplateResourcePath() {
		return bodyTemplateResourcePath;
	}

	public void setBodyTemplateResourcePath(String bodyTemplateResourcePath) {
		this.bodyTemplateResourcePath = bodyTemplateResourcePath;
	}

	public String getHtmlBody() {
		return htmlBody;
	}

	public void setHtmlBody(String htmlBody) {
		this.htmlBody = htmlBody;
	}

	public String getHtmlBodyTemplateResourcePath() {
		return htmlBodyTemplateResourcePath;
	}

	public void setHtmlBodyTemplateResourcePath(String htmlBodyTemplateResourcePath) {
		this.htmlBodyTemplateResourcePath = htmlBodyTemplateResourcePath;
	}

	public Map<String, Object> getModel() {
		return model;
	}

	public void setModel(Map<String, Object> model) {
		this.model = model;
	}

	public int getWrapColumn() {
		return wrapColumn;
	}

	public void setWrapColumn(int wrapColumn) {
		this.wrapColumn = wrapColumn;
	}

	public List<EmailAddress> getToRecipients() {
		return toRecipients;
	}

	public List<EmailAddress> getCcRecipients() {
		return ccRecipients;
	}

	public List<EmailAddress> getBccRecipients() {
		return bccRecipients;
	}

	public List<String> getAttachments() {
		return attachments;
	}
	
	public void addAttachment(String resourcePath)
	{
		attachments.add(resourcePath);
	}

	public EmailAddress getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(EmailAddress fromAddress) {
		validateEmailAddress(fromAddress);
			this.fromAddress = fromAddress;
	}

	public EmailAddress getReplyToAddress() {
		return replyToAddress;
	}

	public void setReplyToAddress(EmailAddress replyToAddress) {
		validateEmailAddress(replyToAddress);
			this.replyToAddress = replyToAddress;
	}
}

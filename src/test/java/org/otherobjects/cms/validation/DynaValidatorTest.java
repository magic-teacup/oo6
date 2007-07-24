package org.otherobjects.cms.validation;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.beans.BaseBeanServiceTest;
import org.otherobjects.cms.binding.BindServiceImpl;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class DynaValidatorTest extends BaseBeanServiceTest {

	private String dateFormat = "dd/MM/yy";
	Date testDate;
	private BindServiceImpl bindService;
	private DynaNodeValidator validator;
	
	@Override
	protected void onSetUp() throws Exception {
		super.onSetUp();
		
		bindService = new BindServiceImpl();
		bindService.setDateFormat(dateFormat);
		testDate = new SimpleDateFormat(dateFormat).parse("01/01/99");
		
		// modify the test type to include some stuff that needs to be validated
		
		TypeDef typeDef = typeService.getType("org.otherobjects.Dyna.jcr.TestObject");
		PropertyDef testString = typeDef.getProperty("testString");
		testString.setRequired(true);
		
		PropertyDef testText = typeDef.getProperty("testText");
		testText.setSize(10);
		
		PropertyDef testNumber = typeDef.getProperty("testNumber");
		testNumber.setValang("{ ? : ? between 0 and 10 : 'out of range' : 'out.of.specified.range' : 0, 10 }");
		
		validator = new DynaNodeValidator();
		validator.setTypeService(typeService);
	}
	
	public void testValidate() throws Exception
	{
		PropertyUtils.setSimpleProperty(dynaNode, "testString", null);
		
		BindingResult errors = bindService.bind(dynaNode, getRequest());
		assertTrue(errors.getErrorCount() == 0);
		
		validator.validate(dynaNode, errors);
		
		System.out.println(errors.getErrorCount());
		
		assertTrue(errors.getErrorCount() == 3);
		
		FieldError testStringField = errors.getFieldError("testString");
		assertTrue(testStringField.getCode().equals("field.required"));
		
		FieldError testTextField = errors.getFieldError("testText");
		assertTrue(testTextField.getCode().equals("field.valueTooLong"));
		
		FieldError testNumberField = errors.getFieldError("testNumber");
		assertTrue(testNumberField.getCode().equals("out.of.specified.range"));
		
	}
	
	public HttpServletRequest getRequest()
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		//request.addParameter("testString", "testString1");
		request.addParameter("testText", "tooLongTestStringtooLoongTestStringtooLongTestString");
		request.addParameter("testDate", "01/01/99");
		request.addParameter("testTime", "01/01/99");
		request.addParameter("testTimestamp", "01/01/99");
		request.addParameter("testNumber", "12");
		request.addParameter("testDecimal", "2.7");
		request.addParameter("testBoolean", "false");
		return request;
	}
	
}

package org.otherobjects.cms.types;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.CodeVisitor;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;
import org.springframework.core.OrderComparator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;

/**
 * TODO Update class scanning on uppgade to Spring 2.1
 * TODO Allow method annotations as well an field annotations (see Hibernate)
 * 
 * @author rich
 */
public class AnnotationBasedTypeDefBuilder implements TypeDefBuilder
{
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    public TypeDef getTypeDef(String type) throws Exception
    {
        return getTypeDef(Class.forName(type));
    }

    @SuppressWarnings("unchecked")
    public TypeDef getTypeDef(Class<?> clazz) throws Exception
    {
        Assert.isTrue(clazz.isAnnotationPresent(Type.class), "TypeDef can't be built as there are no Type annotations present: " + clazz.getName());

        Type typeDefAnnotation = clazz.getAnnotation(Type.class);
        TypeDef typeDef = new TypeDef();
        typeDef.setName(clazz.getName());
        typeDef.setClassName(clazz.getName());
        typeDef.setSuperClassName(typeDefAnnotation.superClassName());
        typeDef.setLabel(typeDefAnnotation.label());
        typeDef.setDescription(typeDefAnnotation.description());
        typeDef.setLabelProperty(typeDefAnnotation.labelProperty());

        // Create a list as a container to allow for ordered addition of found PropertyDefs
        List<PropertyDef> propDefs = new ArrayList<PropertyDef>();

        // Iterate all fields
        Method[] methods = clazz.getMethods();
        for (Method method : methods)
        {
            // Property propertyDefAnnotation = AnnotationUtils.getAnnotation(field, Property.class);
            Property propertyDefAnnotation = method.getAnnotation(Property.class);
            if (propertyDefAnnotation != null)
            {
                PropertyDef propertyDef = new PropertyDef();
                propertyDef.setName(getPropertyNameFromGetterOrSetter(method.getName()));
                propertyDef.setDescription(propertyDefAnnotation.description());
                propertyDef.setHelp(propertyDefAnnotation.help());

                // Infer type if not set
                if (propertyDefAnnotation.type().equals(PropertyType.STRING) && !method.getReturnType().equals(String.class))
                {
                    PropertyType pt = getDefaultTypeForClass(method.getReturnType());
                    Assert.notNull(pt, "No default type can be inferred for property: " + method.getName());
                    propertyDef.setType(pt.value());
                }
                else
                {
                    propertyDef.setType(propertyDefAnnotation.type().value());
                }

                // Infer label if not set
                if (StringUtils.isEmpty(propertyDefAnnotation.label()))
                {
                    propertyDef.setLabel(org.otherobjects.cms.util.StringUtils.generateLabel(method.getName().substring(3)));
                }
                else
                {
                    propertyDef.setLabel(propertyDefAnnotation.label());
                }

                propertyDef.setParentTypeDef(typeDef);
                propertyDef.setRequired(propertyDefAnnotation.required());
                propertyDef.setSize(propertyDefAnnotation.size());
                propertyDef.setValang(propertyDefAnnotation.valang());
                propertyDef.setOrder(propertyDefAnnotation.order());
                // TODO Reference and component support
                if (propertyDefAnnotation.type().equals(PropertyType.LIST))
                {
                    propertyDef.setRelatedType(propertyDefAnnotation.relatedType());
                    propertyDef.setCollectionElementType(propertyDefAnnotation.collectionElementType().value());
                }
                propDefs.add(propertyDef);
            }
        }

        Collections.sort(propDefs, new OrderComparator());

        for (PropertyDef orderedPropertyDef : propDefs)
        {
            typeDef.addProperty(orderedPropertyDef);
        }

        return typeDef;
    }

    private PropertyType getDefaultTypeForClass(Class<?> type)
    {
        if (type.equals(Boolean.class))
            return PropertyType.BOOLEAN;

        else if (type.equals(Date.class))
            return PropertyType.DATE;

        else if (type.equals(Long.class))
            return PropertyType.NUMBER;

        else if (type.equals(BigDecimal.class))
            return PropertyType.DECIMAL;

        else if (DynaNode.class.isAssignableFrom(type))
            return PropertyType.REFERENCE;

        // No suitable default available
        return null;
    }

    static Pattern pattern = Pattern.compile("(?:(?:s|get)|(?:is))(\\w{1})(.*)");

    //FIXME clearly somebody else must have done this - and maybe in a cleverer way
    private String getPropertyNameFromGetterOrSetter(String methodName)
    {
        Matcher matcher = pattern.matcher(methodName);
        if (matcher.matches())
            return matcher.group(1).toLowerCase() + matcher.group(2);
        else
            throw new OtherObjectsException("The annotated method " + methodName + " doesn't seem to follow bean style conventions");
    }

    protected Set<Class<?>> findAnnotatedClasses(String packageName) throws IOException, ClassNotFoundException
    {
        // Inspired by: org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
        Set<Class<?>> matches = new HashSet<Class<?>>();
        String packageSearchPath = "classpath*:" + packageName.replaceAll("\\.", "/") + "/**/*.class";
        Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
        for (Resource resource : resources)
        {
            Class<?> c = getClassFromResource(resource);
            if (c.isAnnotationPresent(Type.class)){
                matches.add(c);
            }
        }
        return matches;
    }

    private Class<?> getClassFromResource(Resource resource) throws IOException, ClassNotFoundException
    {
        final List<String> names = new ArrayList<String>();;
        ClassReader cr = new ClassReader(resource.getInputStream());

        cr.accept(new ClassVisitor()
        {
            public void visitInnerClass(String name, String outerName, String innerName, int access)
            {
            }

            public void visitEnd()
            {
            }

            public void visit(int version, int access, String name, String superName, String[] interfaces, String sourceFile)
            {
                // name is fully-qualified
                names.add(name.replace('/', '.'));
            }

            public void visitAttribute(Attribute arg0)
            {
            }

            public void visitField(int arg0, String arg1, String arg2, Object arg3, Attribute arg4)
            {
            }

            public CodeVisitor visitMethod(int arg0, String arg1, String arg2, String[] arg3, Attribute arg4)
            {
                return null;
            }
        }, true);

        return Class.forName(names.get(0));
    }

}

package org.grails.plugin.emailTemplates;


import org.codehaus.groovy.grails.commons.AbstractInjectableGrailsClass;
import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;
import org.codehaus.groovy.grails.commons.InjectableGrailsClass;


public final class EmailTemplateArtefactHandler extends ArtefactHandlerAdapter {
  
    public static final String TYPE = "EmailTemplate";
    public static final String SUFFIX = "EmailTemplate";

    public EmailTemplateArtefactHandler() {
      super(TYPE, EmailTemplateArtefactHandler.class, DefaultEmailTemplateGrailsClass.class, null);
    }

    public boolean isArtefactClass(@SuppressWarnings("rawtypes") Class clazz) {
      return clazz != null && clazz.getName().endsWith(TYPE);
    }

    public static interface EmailTemplateGrailsClass extends InjectableGrailsClass {
    }

    public static final class DefaultEmailTemplateGrailsClass extends AbstractInjectableGrailsClass implements EmailTemplateGrailsClass {
      public DefaultEmailTemplateGrailsClass(Class<?> wrappedClass) {
        super(wrappedClass, EmailTemplateArtefactHandler.TYPE);
      }
    }
}


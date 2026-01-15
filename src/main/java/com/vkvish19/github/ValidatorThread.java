package com.vkvish19.github;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ValidatorThread implements Runnable
{
    private final String xml;
    private final int threadId;
//    private static final SAXBuilder myBuilder = getBuilder();
    public ValidatorThread(String xml, int threadId)
    {
        this.xml = xml;
        this.threadId = threadId;
    }

    @Override
    public void run()
    {
//        if(threadId == 1)
//        {
//            try {
//                TimeUnit.SECONDS.sleep(10);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
        long startTime = System.currentTimeMillis();
        simpleValidation(xml);
        long endTime = System.currentTimeMillis();
        System.out.println("Thread-Id = " + threadId + ", time taken = " + (endTime-startTime));
    }

    public boolean schemaValidation(String xmlString, String xsdString)
    {
        // Validate without XSD. Useful for well-formedness checks.
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(new StringReader(xsdString)));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xmlString)));
            return true;
        }
        catch(SAXException | IOException e) {
            System.err.println("XML validation failed: " + e.getMessage());
            return false;
        }
    }

    public boolean validateXml(String xmlString)
    {
        try
        {
            SAXBuilder saxbuilder = getBuilder();
            Element settingsData = null;
            Document document = null;
            document = saxbuilder.build(new StringReader(xmlString));
            if(document != null)
            {
                settingsData = document.getRootElement();

                // always add version for our primary agent modules
                if(settingsData != null)
                {
                    Namespace ns = settingsData.getNamespace();
                    String agentVer = settingsData.getChildText(VERSION_TAG, ns);
                    Element modules = settingsData.getChild(MODULES_TAG, ns);
                    if(modules != null)
                    {
                        List children = modules.getChildren(MODULE_DESCRIPTOR_TAG, ns);
                        for(Object child : children)
                        {
                            String moduleName = ((Element)child).getChildText(NAME_TAG, ns);
                            String moduleVersion = ((Element)child).getChildText(VERSION_TAG, ns);

                            //for inventory only agents, update the module version of 'Inventory' module, as the agentversion.
                            if( (agentVer == null) && (moduleName.equalsIgnoreCase(INVENTORY_MODULE_NAME)) )
                            {
                                agentVer = moduleVersion;
                            }
                        }
                    }
                    return true;
                }
            }
            return false;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    protected boolean simpleValidation(String xml)
    {
        try(StringReader sr = new StringReader(xml))
        {
            long startTime = System.currentTimeMillis();
            SAXBuilder builder = getBuilder();
            long endTime = System.currentTimeMillis();
            System.out.println("Thread-Id = " + threadId + ", time taken to get builder = " + (endTime-startTime));
            startTime = System.currentTimeMillis();
            builder.build(sr);
            endTime = System.currentTimeMillis();
            System.out.println("Thread-Id = " + threadId + ", build time = " + (endTime-startTime));
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    public static SAXBuilder getBuilder()
    {
        SAXBuilder builder = new SAXBuilder();
        try
        {
            builder.setExpandEntities(false);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return builder;
    }

    private static final String MODULES_TAG = "Modules";
    private static final String NAME_TAG = "Name";
    private static final String VERSION_TAG = "Version";
    private static final String MODULE_DESCRIPTOR_TAG = "ModuleDescriptor";
    public static final String INVENTORY_MODULE_NAME = "Inventory";

}

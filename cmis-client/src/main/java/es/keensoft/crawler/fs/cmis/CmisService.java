package es.keensoft.crawler.fs.cmis;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.RelationshipDirection;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CmisService
{

    @Value("${alfresco.repository.url}")
    String alfrescoUrl;
    @Value("${alfresco.repository.user}")
    String alfrescoUser;
    @Value("${alfresco.repository.pass}")
    String alfrescoPass;

    private Session session;

    public Session getSession()
    {

        if (session == null)
        {

            String alfrescoBrowserUrl = alfrescoUrl + "/api/-default-/public/cmis/versions/1.1/browser";

            Map<String, String> parameter = new HashMap<String, String>();

            parameter.put(SessionParameter.USER, alfrescoUser);
            parameter.put(SessionParameter.PASSWORD, alfrescoPass);

            parameter.put(SessionParameter.BROWSER_URL, alfrescoBrowserUrl);
            parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());

            SessionFactory factory = SessionFactoryImpl.newInstance();
            session = factory.getRepositories(parameter).get(0).createSession();

        }

        return session;

    }
    
    public Document createDocument(Folder folder, String documentName)
    {
        
        getSession();
        
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, documentName);

        byte[] content = "Hello World!".getBytes();
        InputStream stream = new ByteArrayInputStream(content);
        ContentStream contentStream = new ContentStreamImpl(documentName, BigInteger.valueOf(content.length), "text/plain", stream);

        return folder.createDocument(properties, contentStream, VersioningState.MAJOR);
    }
    
    public ObjectId createRelationship(CmisObject sourceObject, CmisObject targetObject, String relationshipName) 
    {
        
        getSession();
        
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, "a new relationship");
        properties.put(PropertyIds.OBJECT_TYPE_ID, relationshipName);
        properties.put(PropertyIds.SOURCE_ID, sourceObject.getId());
        properties.put(PropertyIds.TARGET_ID, targetObject.getId());

        return session.createRelationship(properties);

    }
    
    public void addAspect(CmisObject cmisObject, String aspect)
    {
        
        getSession();
        
        List<Object> aspects = cmisObject.getProperty("cmis:secondaryObjectTypeIds").getValues();
        if (!aspects.contains(aspect))
        {
            aspects.add(aspect);
            Map<String, Object> aspectListProps = new HashMap<String, Object>();
            aspectListProps.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, aspects);
            cmisObject.updateProperties(aspectListProps);
        }
        
    }
    
    public ItemIterable<Relationship> getRelationships(ObjectId objectId, String relationshipName)
    {
        
        getSession();
        
        ObjectType typeDefinition = session.getTypeDefinition(relationshipName);
        OperationContext operationContext = session.createOperationContext();
        return session.getRelationships(objectId, true, RelationshipDirection.EITHER, typeDefinition, operationContext);
        
    }
    
    public void remove(CmisObject object)
    {
        
        getSession();
        
        if (BaseTypeId.CMIS_FOLDER.equals(object.getBaseTypeId()))
        {
            Folder folder = (Folder) object;
            ItemIterable<CmisObject> children = folder.getChildren();
            for (CmisObject child : children)
            {
                remove(child);
            }
        }
        session.delete(object);
    }
    
}

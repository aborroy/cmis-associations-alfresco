package org.alfresco.cmis.assoc;

import org.alfresco.cmis.assoc.service.CmisService;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *  Add relationships with CMIS using following custom model:
 *  
 *      <aspect name="cmisassoc:relationable">
 *          <associations>
 *                <association name="cmisassoc:related">
 *                    <title>Related Documents</title>
 *                    <source>
 *                        <mandatory>false</mandatory>
 *                        <many>true</many>
 *                    </source>
 *                    <target>
 *                        <class>cm:content</class>
 *                        <mandatory>false</mandatory>
 *                        <many>true</many>
 *                    </target>
 *                </association>
 *            </associations>
 *        </aspect>
 *
 */
@SpringBootApplication
public class App implements CommandLineRunner
{

    private static Logger log = LoggerFactory.getLogger(App.class);

    @Autowired
    CmisService cmisService;

    public static void main(String[] args)
    {
        SpringApplication.run(App.class, args);
    }

    public void run(String... args) throws Exception
    {

        Session session = cmisService.getSession();
        
        log.info("--Creating documents...");
        Document docA = cmisService.createDocument(session.getRootFolder(), "document-a.txt");
        Document docB = cmisService.createDocument(session.getRootFolder(), "document-b.txt");
        
        log.info("--Setting aspects...");
        cmisService.addAspect(docA, "P:cmisassoc:relationable");
        cmisService.addAspect(docB, "P:cmisassoc:relationable");
        
        log.info("--Setting relationships...");
        ObjectId relationship = cmisService.createRelationship(docA, docB, "R:cmisassoc:related");
        log.info("\tcreated relationship " + relationship);
        
        log.info("--Relationships retrieval are not available in CMIS QL...");
        log.info("... but you can get them using CMIS Services.");
        ItemIterable<Relationship> relationships = cmisService.getRelationships(docA, "R:cmisassoc:related");
        relationships.forEach((r) -> {
                log.info("\trelationship for docA: " + r.getId());
            });
        
        
        log.info("Removing created documents...");
        cmisService.remove(docA);        
        cmisService.remove(docB);

        System.exit(0);

    }

}
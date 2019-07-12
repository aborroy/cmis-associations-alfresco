# Alfresco CMIS Relationships (associations)

Sample project to handle Alfresco CMIS Relationships (named *associations* in Alfresco Content Model).

## Description

This project includes following folders:

* `cmis-client` is an standalone CMIS Client based in Spring Boot
* `docker` is a Docker Compose template using Alfresco Community 6.1 with a sample content model including associations

The sample content model describes and aspect `cmisassoc:relationable` including an association `cmisassoc:related` and a multi-valued property `cmisassoc:relatedRef` that stores a copy of the associated nodes to make it visible from CMIS QL.

```xml
<aspect name="cmisassoc:relationable">
  <properties>
    <property name="cmisassoc:relatedRef">
      <type>d:noderef</type>
      <multiple>true</multiple>
    </property>
  </properties>
  <associations>
    <association name="cmisassoc:related">
      <title>Related Documents</title>
      <source>
        <mandatory>false</mandatory>
        <many>true</many>
      </source>
      <target>
        <class>cm:content</class>
        <mandatory>false</mandatory>
        <many>true</many>
      </target>
    </association>
  </associations>
</aspect>
```

## Using the project

In order to have a working Alfresco Repository with the custom model deployed, starting **Docker** is required.

```sh
$ cd docker
$ docker-compose up --build
```

Once Docker is ready, Alfresco will be available at:

http://localhost:8080/alfresco

http://localhost:8080/share

After that, **Maven** project can be built.

```sh
$ cd cmis-client
$ mvn clean package
```

Running the sample project will provide an output similar to following lines.

```sh
$ java -jar target/cmis-client-1.0.0.jar
Started App in 1.276 seconds (JVM running for 1.646)
--Creating documents...
	created document A 51e29133-e071-4719-bcff-e979b687ee14;1.0
	created document B 54a7b8de-9867-4840-bf62-7850b0f67d4b;1.0
--Setting aspects...
--Setting relationships...
	created relationship Object Id: assoc:18
--Relationships retrieval are not available in CMIS QL...
... but you can get them using CMIS Services.
	relationship for docA: assoc:18
--Alternative nodeRef field to get relationships using CMIS QL...
	relationship for docA: 54a7b8de-9867-4840-bf62-7850b0f67d4b
Removing created documents...
```

The program is creating `document-a.txt` and `document-b.txt` documents and setting a `cmisassoc:related` association between both. After that, these relationships are obtained using CMIS Services. Additionally, the program is adding a copy of the association in the multi-valued property `cmisassoc:relatedRef` and this association is obtained using CMIS QL. Finally, both documents are removed.

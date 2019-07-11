ARG ALFRESCO_TAG
FROM alfresco/alfresco-content-repository-community:${ALFRESCO_TAG}

ARG TOMCAT_DIR=/usr/local/tomcat
ARG ALF_DATA_DIR=${TOMCAT_DIR}/alf_data

### Sample Content Model
COPY cmis-assoc-content-model.xml $TOMCAT_DIR/shared/classes/alfresco/extension/
COPY cmis-assoc-content-model-context.xml $TOMCAT_DIR/shared/classes/alfresco/extension/

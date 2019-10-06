FROM sonarqube

ENV ARTIFACT_NAME "sonar-java-academic-plugin"
ENV ARTIFACT_VERSION "0.1"

ENV ARTIFACT_FILE_NAME "${ARTIFACT_NAME}-${ARTIFACT_VERSION}.jar"

COPY "target/scala-2.13/${ARTIFACT_FILE_NAME}" "/opt/sonarqube/extensions/plugins/${ARTIFACT_FILE_NAME}"

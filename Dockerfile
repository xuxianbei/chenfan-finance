FROM java:8
VOLUME /tmp
ADD target/*.jar  app.jar
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/Shanghai" > /etc/timezone
RUN sh -c 'touch /app.jar'
RUN echo $(date) > /image_built_at
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

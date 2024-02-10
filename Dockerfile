FROM openjdk:21-slim
WORKDIR usr/app/
COPY /target/*.jar cooking_bot.jar
RUN useradd -u 1001 tech_user
RUN chown -R tech_user /usr/app
ENTRYPOINT ["java", "-jar", "cooking_bot.jar"]
FROM openjdk:17
COPY .. /myapp/
WORKDIR /myapp/
RUN javac -cp src/antlr-4.9.2-complete.jar:src/ src/lab4_1.java -d dst/
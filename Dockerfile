FROM ubuntu:20.04
RUN apt update && apt install gcc
WORKDIR /app/
COPY main.c ./
RUN gcc main.c -o main
RUN chmod +x main


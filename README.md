# A Manager Server for Ansible

[![Build Status](https://travis-ci.com/JuKu/ansible-manager.svg?branch=master)](https://travis-ci.com/JuKu/ansible-manager)
[![CircleCI](https://circleci.com/gh/JuKu/ansible-manager/tree/master.svg?style=svg)](https://circleci.com/gh/JuKu/ansible-manager/tree/master)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=com.jukusoft%3Aansible-manager-backend&metric=ncloc)](https://sonarcloud.io/dashboard/index/com.jukusoft%3Aansible-manager-backend)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.jukusoft%3Aansible-manager-backend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=com.jukusoft%3Aansible-manager-backend)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.jukusoft%3Aansible-manager-backend&metric=coverage)](https://sonarcloud.io/dashboard/index/com.jukusoft%3Aansible-manager-backend)
[![Technical Debt Rating](https://sonarcloud.io/api/project_badges/measure?project=com.jukusoft%3Aansible-manager-backend&metric=sqale_index)](https://sonarcloud.io/dashboard/index/com.jukusoft%3Aansible-manager-backend)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=com.jukusoft%3Aansible-manager-backend&metric=code_smells)](https://sonarcloud.io/dashboard/index/com.jukusoft%3Aansible-manager-backend)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=com.jukusoft%3Aansible-manager-backend&metric=bugs)](https://sonarcloud.io/dashboard/index/com.jukusoft%3Aansible-manager-backend)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=com.jukusoft%3Aansible-manager-backend&metric=vulnerabilities)](https://sonarcloud.io/dashboard/index/com.jukusoft%3Aansible-manager-backend)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=com.jukusoft%3Aansible-manager-backend&metric=security_rating)](https://sonarcloud.io/dashboard/index/com.jukusoft%3Aansible-manager-backend)

[![Sonarcloud](https://sonarcloud.io/api/project_badges/quality_gate?project=com.jukusoft%3Aansible-manager-backend)](https://sonarcloud.io/dashboard?id=com.jukusoft%3Aansible-manager-backend)


A ansible manager / server as an alternative to Ansible semaphore and ansible tower.
frontend: https://github.com/JuKu/ansible-manager-frontend

## the problem

Ansible is a very nice tool to automate your infrastructure.
But if you have much servers to adminsitrate, it become to be difficult and expensive.
The cause for this is, that you have to edit your inventory files regulary, you need to deal with things like host key verification, execution of the scripts, get the ssh private keys and so on.

# the solution

This solution tries to solve this problems in a form of a centralized angular manager-server with multiple worker nodes.
The manager-server provides a nice UI to administrate your servers, your playbooks and roles, create templates and execution plans (e.q. "execute a playbook every night at 4.00 a clock") and so on.
Its also for CI / CD approaches and quick deployments of complex systems or clusters of servers.

# Why?

At my job as a software engineer and network & server adminstrator at a startup in Dresden i try to make admistrative things faster and more efficient.
We don't have the man power to spend much time in many recurring tasks, which are so linear, that they don't need a person who did this.
But currently we did, because there is no solution which fits our needs. So we execute ansible playbooks manually, from the console, wait a much of time, edit many files instead of generating them and so on.
So the thought was: This can be done better! So this is the idea, why is started this project.

# HowTo use

**Requirements**:
  - A **mysql** / **mariadb** or a **postgresql** database
  - actual optional: RabbitMQ Server for messaging

The CI Server automatically builds a current docker image, if the build was successful.
You have to start 2 docker containers with the same database:
  - jukusoft/anman-manager
    * https://hub.docker.com/r/jukusoft/anman-manager
  - jukusoft/anman-worker
    * https://hub.docker.com/r/jukusoft/anman-worker

# the roadmap

## August - December 2021

  - initialize project &#9745;
  - authentication &#9745;
  - LDAP integration (test with FreeIPA) &#9745;
  - permission system
  - Create admin user on first start &#9745;
  - inventory management
  - generation of inventory files

//&#9744;

See also: https://github.com/JuKu/ansible-manager/projects/1?add_cards_query=is%3Aopen
ZenHub: https://app.zenhub.com/workspaces/anman-61afc4e126f6d300107f6a88/board

# Deployment

## example docker-compose.yml

```text
version: "3.1"

networks:
  ansible-manager:
    external: true


services:

  mariadb:
    image: bitnami/mariadb:latest
    restart: always
    environment:
      - ALLOW_EMPTY_PASSWORD=no
#      - MARIADB_EXTRA_FLAGS='--max-connect-errors=1000 --max_connections=155'
      - MARIADB_ROOT_USER=root
      - MARIADB_ROOT_PASSWORD=<YOUR DB ROOT PASSWORD>
      - MARIADB_DATABASE=ansible-manager
      - MARIADB_USER=ansible-manager
      - MARIADB_PASSWORD=<YOUR DB PASSWORD>
    networks:
      - ansible-manager
    ports:
      - 3306:3306
    volumes:
      - /opt/docker/volumes/mariadb:/bitnami/mariadb

  ansible-manager:
    image: jukusoft/anman-manager:master-latest
    restart: always
    environment:
      - DATABASE_TYPE=mysql
      - DATABASE_HOST=mariadb
      - DATABASE_PORT=3306
      - DATABASE_NAME=ansible-manager
      - DATABASE_USER=ansible-manager
      - DATABASE_PASSWORD=<YOUR DB PASSWORD>
    depends_on:
      - mariadb
    networks:
      - ansible-manager
    ports:
      - 8081:8080
    volumes:
      - /opt/docker/run/ansible-manager/application.yml:/var/lib/jetty/application.yml

  ansible-worker:
    image: jukusoft/anman-worker:master-latest
    restart: always
    environment:
      - DATABASE_TYPE=mysql
      - DATABASE_HOST=mariadb
      - DATABASE_PORT=3306
      - DATABASE_NAME=ansible-manager
      - DATABASE_USER=ansible-manager
      - DATABASE_PASSWORD=<YOUR DB PASSWORD>
    depends_on:
      - mariadb
    networks:
      - ansible-manager
    ports:
      - 8082:8080
    volumes:
      - /opt/docker/run/ansible-manager/application.yml:/var/lib/jetty/application.yml

  anman-frontend:
    image: jukusoft/anman-frontend:master-latest
    restart: always
    environment:
      - BACKEND_API=http://<Your public Backend IP or Domain>:8081/
      - DEFAULT_LANGUAGE=de
    depends_on:
      - ansible-manager
    networks:
      - ansible-manager
    ports:
      - 80:80

  # if required
  phpmyadmin:
    image: phpmyadmin
    restart: always
    ports:
      - 8080:80
    environment:
      - PMA_ARBITRARY=1
      - PMA_ABSOLUTE_URI=http://<Your public Backend IP or Domain>:8080/
    depends_on:
      - mariadb
    networks:
      - ansible-manager
```

# Changelogs

## V0.1.0 (WIP)

**New Features**:

  - Added example docker-compose.yml in README, see #5
  - Added global settings, see #10

**Developer Improvements**:

  - Upgrade spring version, see #15
  - Fix a security leak in log4j, see #13
  - Upgrade springfox swagger to Springdocs, see #16
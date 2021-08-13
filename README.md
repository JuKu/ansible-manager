# A Manager Server for Ansible

[![Build Status](https://travis-ci.com/JuKu/ansible-manager.svg?branch=master)](https://travis-ci.com/JuKu/ansible-manager)
[![CircleCI](https://circleci.com/gh/JuKu/ansible-manager/tree/master.svg?style=svg)](https://circleci.com/gh/JuKu/ansible-manager/tree/master)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=com.jukusoft%3Aansible-manager-backend&metric=ncloc)](https://sonarcloud.io/dashboard/index/com.jukusoft%3Aansible-manager-backend)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.jukusoft%3Aansible-manager-backend&metric=alert_status)](https://sonarcloud.io/dashboard/index/com.jukusoft%3Aansible-manager-backend)
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

## August 2021

  - initialize project
  - authentication
  - LDAP integration (test with FreeIPA)
  - permission system
  - inventory management
  - generation of inventory files
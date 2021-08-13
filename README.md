# A Manager Server for Ansible

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

# the roadmap

## August 2021

  - initialize project
  - authentication
  - LDAP integration (test with FreeIPA)
  - permission system
  - inventory management
  - generation of inventory files
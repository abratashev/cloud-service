-- — USER SQL
CREATE USER cloud_service
IDENTIFIED BY qwerty$4
DEFAULT TABLESPACE USERS
TEMPORARY TABLESPACE TEMP
ACCOUNT UNLOCK;

-- privileges
grant create procedure to cloud_service;
grant create sequence to cloud_service;
grant create view to cloud_service;
grant create session to cloud_service;
grant create synonym to cloud_service;
grant create table to cloud_service;

-- — QUOTAS
ALTER USER cloud_service QUOTA UNLIMITED ON USERS;

-- — ROLES
GRANT CONNECT TO cloud_service;
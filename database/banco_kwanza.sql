\set ON_ERROR_STOP on

CREATE ROLE interbank_kwanza LOGIN;
\password interbank_kwanza

CREATE DATABASE banco_kwanza OWNER interbank_kwanza;
REVOKE CONNECT ON DATABASE banco_kwanza FROM PUBLIC;

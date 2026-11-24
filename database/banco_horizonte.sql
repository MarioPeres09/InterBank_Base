\set ON_ERROR_STOP on

CREATE ROLE interbank_horizonte LOGIN;
\password interbank_horizonte

CREATE DATABASE banco_horizonte OWNER interbank_horizonte;
REVOKE CONNECT ON DATABASE banco_horizonte FROM PUBLIC;

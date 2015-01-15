CREATE SEQUENCE seq_appuser_id INCREMENT 1 START 21;

CREATE TABLE "appuser"
(
  id integer NOT NULL default nextval('seq_appuser_id'),
  name character varying,
  last_name character varying,
  CONSTRAINT pk_appuser_id PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "appuser"
  OWNER TO postgres;

ALTER SEQUENCE seq_appuser_id owned by appuser.id;

CREATE SEQUENCE seq_account_id INCREMENT 1 START 21;
  
CREATE TABLE account
(
  login character varying NOT NULL,
  last_logon_date timestamp with time zone,
  user_id integer NOT NULL,
  id integer NOT NULL default nextval('seq_account_id'),
  CONSTRAINT pk_account_id PRIMARY KEY (id),
  CONSTRAINT fk_account_to_appuser FOREIGN KEY (user_id)
      REFERENCES "appuser" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT unique_account_login UNIQUE (login)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE account
  OWNER TO postgres;

CREATE INDEX fki_account_to_appuser
  ON account
  USING btree
  (user_id);

 ALTER SEQUENCE seq_account_id owned by account.id;
  
CREATE SEQUENCE seq_message_id  INCREMENT 1 START 5;

CREATE TABLE message
(
  id integer NOT NULL default nextval('seq_message_id'),
  user_id integer NOT NULL,
  creation_date timestamp with time zone NOT NULL,
  text character varying,
  CONSTRAINT pk_message_id PRIMARY KEY (id),
  CONSTRAINT fk_message_to_appuser FOREIGN KEY (user_id)
      REFERENCES "appuser" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE message
  OWNER TO postgres;

CREATE INDEX fki_message_to_appuser
  ON message
  USING btree
  (user_id);

ALTER SEQUENCE seq_message_id owned by message.id;

CREATE SCHEMA IF NOT EXISTS userschema;

CREATE TABLE IF NOT EXISTS userschema.user
    (
        id serial,
        name character varying(255) COLLATE pg_catalog."default",
        email character varying(255) COLLATE pg_catalog."default",
        user_type smallint COLLATE pg_catalog."default",
        CONSTRAINT user_pkey PRIMARY KEY (id)
    )
TABLESPACE pg_default;
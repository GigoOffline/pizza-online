
DROP TABLE IF EXISTS ORDERS;

DROP SEQUENCE IF EXISTS global_seq;

CREATE SEQUENCE global_seq START 1000;

CREATE TABLE ORDERS
(
    id        INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    person_id INTEGER   NOT NULL,
    created   TIMESTAMP NOT NULL,
    completed TIMESTAMP,
    pdf       CHAR(255)
);




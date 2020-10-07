drop table if exists state_entity;

CREATE TABLE if not exists state_entity
(
    user_id varchar(30),
    git_name varchar(30),
    state_machine bytea,
    PRIMARY KEY (user_id)
);

delete state_entity;

INSERT into state_entity values ('Yura', 'testName', 1);

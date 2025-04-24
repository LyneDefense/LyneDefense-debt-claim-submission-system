create table claim
(
    id               varchar(255)          not null
        primary key,
    claim_number     varchar(255)          not null,
    registrar        varchar(255),
    claim_date       date,
    claim_types      text,
    auditor          varchar(255),
    claim_category   varchar(255),
    material_status  varchar(255),
    agent_name       varchar(255),
    agent_position   varchar(255),
    agent_phone      varchar(255),
    has_voting_right boolean,
    recipient        varchar(255),
    contact_phone    varchar(255),
    mailing_address  varchar(255),
    email            varchar(255),
    create_time      timestamp(6)          not null,
    update_time      timestamp(6)          not null,
    deleted          boolean default false not null
);


create table creditor
(
    id                    varchar(255)          not null
        primary key,
    claim_id              varchar(255)          not null,
    name                  varchar(255),
    identification_number varchar(255),
    phone                 varchar(255),
    address               varchar(255),
    id_type               varchar(255),
    create_time           timestamp(6)          not null,
    update_time           timestamp(6)          not null,
    deleted               boolean default false not null
);


create table claim_filling
(
    id                 varchar(255)          not null
        primary key,
    claim_id           varchar(255)          not null,
    claim_principal    numeric(19, 2),
    claim_interest     numeric(19, 2),
    claim_other        numeric(19, 2),
    claim_nature       varchar(255),
    collateral_details varchar,
    create_time        timestamp(6)          not null,
    update_time        timestamp(6)          not null,
    deleted            boolean default false not null
);


create table claim_confirm
(
    id                  varchar(255)          not null
        primary key,
    claim_filling_id    varchar(255)          not null,
    review_status       varchar               not null,
    confirmed_principal numeric(19, 2),
    confirmed_interest  numeric(19, 2),
    confirmed_other     numeric(19, 2),
    claim_nature        varchar(255),
    review_reason       text,
    create_time         timestamp(6)          not null,
    update_time         timestamp(6)          not null,
    deleted             boolean default false not null
);


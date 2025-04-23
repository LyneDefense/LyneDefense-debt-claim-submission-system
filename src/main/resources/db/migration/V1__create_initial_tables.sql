-- Create claim table
CREATE TABLE "public"."claim"
(
    "id"                varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "claim_number"      varchar(255)                                NOT NULL,
    "registrar"         varchar(255) COLLATE "pg_catalog"."default",
    "claim_date"        date,
    "claim_types" varchar[],
    "auditor"           varchar(255),
    "claim_category"    varchar(255),
    "material_status"   varchar(255),
    "agent_name"        varchar(255),
    "agent_position"    varchar(255),
    "agent_phone"       varchar(255),
    "has_voting_right"  boolean,
    "recipient"         varchar(255),
    "contact_phone"     varchar(255),
    "mailing_address"   varchar(255),
    "email"             varchar(255),
    "create_time"       timestamp(6)                                not null,
    "update_time"       timestamp(6)                                not null,
    "deleted"           boolean                                     not null default false,
    PRIMARY KEY ("id")
);

-- Create creditor table
CREATE TABLE "public"."creditor"
(
    "id"                    varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "claim_id"              varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "name"                  varchar(255) COLLATE,
    "identification_number" varchar(255) COLLATE,
    "phone"                 varchar(255) COLLATE,
    "address"               varchar(255) COLLATE,
    "create_time"           timestamp(6)                                not null,
    "update_time"           timestamp(6)                                not null,
    "deleted"               boolean                                     not null default false,
    PRIMARY KEY ("id")
);

-- Create claim_confirm table
CREATE TABLE "public"."claim_confirm"
(
    "id"                  varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "claim_filling_id"    varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "review_status"       int2                                        NOT NULL,
    "confirmed_principal" numeric(19, 2),
    "confirmed_interest"  numeric(19, 2),
    "confirmed_other"     numeric(19, 2),
    "claim_nature"        varchar(255) COLLATE "pg_catalog"."default",
    "review_reason"       text COLLATE "pg_catalog"."default",
    "create_time"         timestamp(6)                                not null,
    "update_time"         timestamp(6)                                not null,
    "deleted"             boolean                                     not null default false,
    PRIMARY KEY ("id")
);

-- Create claim_filling table
CREATE TABLE "public"."claim_filling"
(
    "id"                 varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "claim_id"           varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "claim_principal "   numeric(19, 2),
    "claim_interest"     numeric(19, 2),
    "claim_other"        numeric(19, 2),
    "claim_nature"       varchar(255) COLLATE "pg_catalog"."default",
    "collateral_details" varchar,
    "create_time"        timestamp(6)                                not null,
    "update_time"        timestamp(6)                                not null,
    "deleted"            boolean                                     not null default false,
    PRIMARY KEY ("id")
);

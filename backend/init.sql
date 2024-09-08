create table "user"
(
    id       bigserial
        primary key,
    email    varchar(255) not null
        constraint uk_ob8kqyqqgmefl0aco34akdtpe
            unique,
    enabled  boolean      not null,
    link     varchar(255) not null
        constraint uk_6fy93b0ikbx8gh32gr7230uns
            unique,
    password varchar(255) not null,
    username varchar(255) not null
);

alter table "user"
    owner to postgres;

create table calendar
(
    id   bigserial
        primary key,
    name varchar(255) not null
);

alter table calendar
    owner to postgres;

create table calendar_token
(
    id            bigserial
        primary key,
    access_token  varchar(255)                not null,
    expires_at    timestamp(6) with time zone not null,
    refresh_token varchar(255)                not null,
    calendar_id   bigint                      not null
        constraint fklxp0o68crcvgegqn61g76cww
            references calendar,
    user_id       bigint                      not null
        constraint fki34dyy22jjkfu6p23kyf0o6jl
            references "user"
);

alter table calendar_token
    owner to postgres;

create table confirmation_code
(
    id           bigserial
        primary key,
    code         varchar(255)                not null,
    confirmed_at timestamp(6) with time zone,
    created_at   timestamp(6) with time zone not null,
    expires_at   timestamp(6) with time zone not null,
    user_id      bigint                      not null
        constraint fkcuseoiobkh4vw182yyhwjnq58
            references "user"
);

alter table confirmation_code
    owner to postgres;

create table connected_calendar
(
    id          bigserial
        primary key,
    calendar_id bigint
        constraint fk210ynsx9s1snl3kplwd2h6m20
            references calendar,
    user_id     bigint
        constraint uk_9vp6x24flqdnpukhfnbv7ymn6
            unique
        constraint fkrkxp9uid74w43si1woe94ke7k
            references "user"
);

alter table connected_calendar
    owner to postgres;

create table location
(
    id   bigserial
        primary key,
    name varchar(255) not null
);

alter table location
    owner to postgres;

create table refresh_token
(
    id         bigserial
        primary key,
    created_at timestamp(6) with time zone not null,
    expires_at timestamp(6) with time zone not null,
    revoked    boolean                     not null,
    token      varchar(255)                not null
        constraint uk_r4k4edos30bx9neoq81mdvwph
            unique,
    user_id    bigint                      not null
        constraint fk172n6374f2cgiei2vtvjw2fak
            references "user"
);

alter table refresh_token
    owner to postgres;

create table meeting_poll
(
    id          bigserial
        primary key,
    active      boolean                     not null,
    created_at  timestamp(6) with time zone not null,
    description varchar(255),
    duration    integer                     not null,
    title       varchar(255)                not null,
    location_id bigint
        constraint fks930jj7jwes9c12ooerdbv31m
            references location,
    creator_id  bigint
        constraint fklo17vtbija5np5hoftvo6ye1a
            references "user",
    address     varchar(255)
);

alter table meeting_poll
    owner to postgres;

create table meeting_poll_participant
(
    id                bigserial
        primary key,
    participant_email varchar(255) not null,
    participant_name  varchar(255) not null,
    meeting_poll_id   bigint       not null
        constraint fkqg1r78f0i7aslpty7pqflmbka
            references meeting_poll
);

alter table meeting_poll_participant
    owner to postgres;

create table meeting_poll_time_slot
(
    id              bigserial
        primary key,
    begin_at        timestamp(6) with time zone not null,
    end_at          timestamp(6) with time zone not null,
    meeting_poll_id bigint                      not null
        constraint fkap08ohs3qgajswifhdc7d35h7
            references meeting_poll
);

alter table meeting_poll_time_slot
    owner to postgres;

create table participant_time_slots
(
    participant_id bigint not null
        constraint fkohht07bi0qmwsk5goxqroug5v
            references meeting_poll_participant,
    time_slot_id   bigint not null
        constraint fko29b28ose9jxd2mw8u6reg2g6
            references meeting_poll_time_slot
);

alter table participant_time_slots
    owner to postgres;

create table meeting
(
    id               bigserial
        primary key,
    begin_at         timestamp(6) with time zone,
    description      varchar(255),
    end_at           timestamp(6) with time zone,
    event_id         varchar(255),
    title            varchar(255),
    calendar_id      bigint
        constraint fkklgp8ue2im03ne9imiwa4r47w
            references calendar,
    location_id      bigint not null
        constraint fk8bqft0294qmh1tj203qp4hmrt
            references location,
    user_id          bigint not null
        constraint fk8x37at3orhj3st6dtxtew82cp
            references "user",
    conference_link  varchar(255),
    physical_address varchar(255)
);

alter table meeting
    owner to postgres;

create table meeting_participant
(
    id                bigserial
        primary key,
    participant_email varchar(255) not null,
    participant_name  varchar(255) not null,
    meeting_id        bigint       not null
        constraint fk26jnnkay5w0sko7x7kqu17xbr
            references meeting
);

alter table meeting_participant
    owner to postgres;

create table meeting_type
(
    id                  bigserial
        primary key,
    description         varchar(255),
    duration_minutes    integer not null,
    max_days_in_advance integer not null,
    title               varchar(255),
    user_id             bigint
        constraint fkjca6fjucnb9dpr1x7mp1qe3sg
            references "user"
);

alter table meeting_type
    owner to postgres;

create table available_slot
(
    id              bigserial
        primary key,
    email           varchar(255),
    end_date_time   timestamp(6) with time zone,
    name            varchar(255),
    reserved        boolean not null,
    start_date_time timestamp(6) with time zone,
    meeting_type_id bigint  not null
        constraint fkaitjh7gbx00kjl6y64oqwrm5h
            references meeting_type
);

alter table available_slot
    owner to postgres;

create table meeting_type_location
(
    id              bigserial
        primary key,
    address         varchar(255),
    location_id     bigint
        constraint fka2aaso76ftw03pntvhcxpha0w
            references location,
    meeting_type_id bigint
        constraint fkos3a9xqoutd49e32dr9bi1abq
            references meeting_type
);

alter table meeting_type_location
    owner to postgres;

create table meeting_type_time_range
(
    id              bigserial
        primary key,
    day_of_week     varchar(255) not null
        constraint meeting_type_time_range_day_of_week_check
            check ((day_of_week)::text = ANY
                   ((ARRAY ['MONDAY'::character varying, 'TUESDAY'::character varying, 'WEDNESDAY'::character varying, 'THURSDAY'::character varying, 'FRIDAY'::character varying, 'SATURDAY'::character varying, 'SUNDAY'::character varying])::text[])),
    end_time        time(6)      not null,
    start_time      time(6)      not null,
    meeting_type_id bigint       not null
        constraint fkndcpg3e5klfeaokjdk4ehjhxv
            references meeting_type
);

alter table meeting_type_time_range
    owner to postgres;



INSERT INTO Location (name)
VALUES ('Google Meet'),
       ('On-Site');

INSERT INTO calendar (name)
VALUES ('Google');
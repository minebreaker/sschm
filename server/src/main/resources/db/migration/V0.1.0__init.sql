create table car (
    id         uuid primary key,
    name       text                     not null,
    photo_id   uuid,
    note       text                     not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

-- Sets of maintenances.
-- "maintenance_set" itself does not refer "car" directly.
-- When a user chooses a maintenance set, relation of "car" and "maintenance" would be created.
create table maintenance_set (
    id         uuid primary key,
    name       text                     not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table maintenance (
    id         uuid primary key,
    name       text                     not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

-- n:n mapping table
create table maintenance_set_to_maintenance (
    maintenance_set_id uuid not null references maintenance_set(id),
    maintenance_id     uuid not null references maintenance(id),
    primary key (maintenance_set_id, maintenance_id)
);

-- n:n mapping table
create table car_to_maintenance (
    car_id         uuid not null references maintenance_set(id),
    maintenance_id uuid not null references maintenance(id),
    primary key (car_id, maintenance_id)
);

create table event (
    id         uuid primary key,
    car_id     uuid                     not null references car(id),
    odo        int, -- km
    price      int, -- no currency
    note       text                     not null,
    event_date timestamp with time zone not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

-- Uses a separate table for refueling to make calculation easy
create table refuel (
    id                 uuid primary key,
    car_id             uuid                     not null references car(id),
    odo                int,
    price              int,                               -- no currency
    note               text                     not null,
    amount             int                      not null, -- dL (we want to store 1/10L)
    no_previous_refuel bool                     not null, -- forgot to register previous refueling?
    event_date         timestamp with time zone not null,
    created_at         timestamp with time zone not null,
    updated_at         timestamp with time zone not null
);

-- Logs for each maintenance
create table maintenance_event (
    id             uuid primary key,
    car_id         uuid                     not null references car(id),
    odo            int,
    note           text                     not null,
    price          int,                                                          -- no currency
    maintenance_id uuid                     not null references maintenance(id), -- what maintenance did you do?
    event_date     timestamp with time zone not null,
    created_at     timestamp with time zone not null,
    updated_at     timestamp with time zone not null
);

create table photo (
    id           uuid primary key,
    content_type text  not null,
    data         bytea not null
);

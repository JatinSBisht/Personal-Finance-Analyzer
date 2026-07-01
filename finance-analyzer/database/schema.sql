create table if not exists users (
    id bigserial primary key,
    full_name varchar(255) not null,
    email varchar(255) not null unique,
    password varchar(255) not null,
    role varchar(50) not null default 'ROLE_USER',
    created_at timestamp not null default current_timestamp
);

create table if not exists categories (
    id bigserial primary key,
    name varchar(120) not null,
    type varchar(20) not null check (type in ('INCOME', 'EXPENSE'))
);

create table if not exists transactions (
    id bigserial primary key,
    title varchar(255) not null,
    amount numeric(12, 2) not null check (amount > 0),
    type varchar(20) not null check (type in ('INCOME', 'EXPENSE')),
    transaction_date date not null,
    note varchar(500),
    created_at timestamp not null default current_timestamp,
    user_id bigint not null references users(id) on delete cascade,
    category_id bigint not null references categories(id)
);

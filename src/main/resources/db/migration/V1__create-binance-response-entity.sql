create table binance_history (
    id bigserial primary key,
    symbol varchar(20) not null,
    price numeric(18,8) not null,
    close_time timestamptz not null,
    fetched_at timestamptz not null default now()
);

create index on binance_history(symbol);
create index on binance_history(fetched_at);
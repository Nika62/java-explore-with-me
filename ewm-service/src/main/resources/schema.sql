drop table if exists users, categories, events, requests;

create table if not exists users(
	id BIGINT generated by default as identity not null PRIMARY KEY,
	name VARCHAR NOT NULL,
	email VARCHAR NOT NULL unique
    );

create table if not exists categories(
	id BIGINT generated by default as identity not null PRIMARY KEY,
	name VARCHAR NOT null unique
);

create table if not exists events(
	id BIGINT generated by default as identity not null PRIMARY KEY,
	annotation text NOT null,
	category_id BIGINT NOT null,
	confirmed_requests BIGINT NOT null,
	created_on timestamp not null,
	description text,
	event_date timestamp not null,
	user_id BIGINT NOT null,
	location_lat float8 not null,
	location_lon float8 not null,
	paid boolean not null,
	participant_limit integer not null,
	request_moderation boolean not null,
	published_on timestamp,
	state varchar not null,
	title varchar not null,
	CONSTRAINT fk_events_user_id FOREIGN KEY(user_id)
    REFERENCES users(id) ON DELETE CASCADE,
	CONSTRAINT fk_events_category_id FOREIGN KEY(category_id)
    REFERENCES categories(id) ON DELETE CASCADE
);

create table if not exists requests(
    id BIGINT generated by default as identity not null PRIMARY KEY,
    created timestamp not null,
    event_id BIGINT NOT null,
    user_id BIGINT NOT null,
    status varchar not null,
    CONSTRAINT fk_requests_event_id FOREIGN KEY(event_id)
    REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_request_user_id FOREIGN KEY(user_id)
    REFERENCES users(id) ON DELETE CASCADE
);

CREATE DATABASE luckycalories;

CREATE TABLE users (
        user_id    BIGSERIAL PRIMARY KEY,
        user_name   VARCHAR(128),
        email       VARCHAR(128) UNIQUE,
        psw_hash  VARCHAR(128)             NOT NULL,
        user_type INTEGER NOT NULL,
        dailyCalories INTEGER NOT NULL,
        created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
        deleted    BOOLEAN                           DEFAULT FALSE
        );

CREATE TABLE calories (
        calorie_id BIGSERIAL PRIMARY KEY,
        user_id BIGINT NOT NULL REFERENCES users,
        meal VARCHAR(128) NOT NULL,
        note VARCHAR(512),
        calorie_num INTEGER NOT NULL,
        eat_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
        created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
        deleted    BOOLEAN                           DEFAULT FALSE
        );

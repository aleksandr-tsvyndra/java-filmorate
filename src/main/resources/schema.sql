DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users (
  user_id bigint PRIMARY KEY,
  email varchar NOT NULL UNIQUE,
  login varchar NOT NULL UNIQUE,
  name varchar DEFAULT login,
  birthday DATE,
  CONSTRAINT user_check CHECK (email <> '' AND login <> '')
);

DROP TABLE IF EXISTS films;
CREATE TABLE IF NOT EXISTS films (
  film_id bigint PRIMARY KEY,
  name varchar NOT NULL,
  description varchar(200),
  release_date DATE,
  duration integer,
  rating_id integer,
  CONSTRAINT name_check CHECK (name <> '')
  CONSTRAINT duration_check CHECK (duration > 0)
  CONSTRAINT release_date_check CHECK (release_date >= '1895-12-28')
);

DROP TABLE IF EXISTS ratings;
CREATE TABLE IF NOT EXISTS ratings (
  rating_id integer PRIMARY KEY,
  name varchar NOT NULL UNIQUE
);

DROP TABLE IF EXISTS genres;
CREATE TABLE IF NOT EXISTS genres (
  genre_id integer PRIMARY KEY,
  name varchar NOT NULL UNIQUE
);

DROP TABLE IF EXISTS film_genres;
CREATE TABLE IF NOT EXISTS film_genres (
  film_genre_id bigint PRIMARY KEY,
  film_id bigint,
  genre_id integer,
  UNIQUE (film_id, genre_id)
);

DROP TABLE IF EXISTS film_likes;
CREATE TABLE IF NOT EXISTS film_likes (
  like_id bigint PRIMARY KEY,
  film_id bigint,
  user_id bigint,
  UNIQUE (film_id, user_id)
);

DROP TABLE IF EXISTS friends;
CREATE TABLE IF NOT EXISTS friends (
  id bigint PRIMARY KEY,
  user_id bigint,
  friend_id bigint,
  UNIQUE (user_id, friend_id)
);

ALTER TABLE films ADD FOREIGN KEY (rating_id) REFERENCES ratings (rating_id);

ALTER TABLE film_genres ADD FOREIGN KEY (film_id) REFERENCES films (film_id);

ALTER TABLE film_genres ADD FOREIGN KEY (genre_id) REFERENCES genres (genre_id);

ALTER TABLE film_likes ADD FOREIGN KEY (film_id) REFERENCES films (film_id);

ALTER TABLE film_likes ADD FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE friends ADD FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE friends ADD FOREIGN KEY (friend_id) REFERENCES users (user_id);

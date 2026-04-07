-- Directors
INSERT INTO directors (name, birth_date, nationality) VALUES ('Steven Spielberg', '1946-12-18', 'USA');
INSERT INTO directors (name, birth_date, nationality) VALUES ('Christopher Nolan', '1970-07-30', 'UK');

-- Actors
INSERT INTO actors (name, birth_date, nationality) VALUES ('Tom Hanks', '1956-07-09', 'USA');
INSERT INTO actors (name, birth_date, nationality) VALUES ('Leonardo DiCaprio', '1974-11-11', 'USA');
INSERT INTO actors (name, birth_date, nationality) VALUES ('Joseph Gordon-Levitt', '1981-02-17', 'USA');

-- Genres
INSERT INTO genres (name) VALUES ('Action');
INSERT INTO genres (name) VALUES ('Drama');
INSERT INTO genres (name) VALUES ('Sci-Fi');

-- Movies
INSERT INTO movies (title, description, year, votes, rating, director_id, image) 
VALUES ('Saving Private Ryan', 'WWII drama', 1998, 0, 0, 1, 'saving_private_ryan.jpg');

INSERT INTO movies (title, description, year, votes, rating, director_id, image) 
VALUES ('Inception', 'Mind-bending sci-fi thriller', 2010, 0, 0, 2, 'inception.jpg');

-- Relationship Movies-Actors
INSERT INTO movie_actor (movie_id, actor_id) VALUES (1, 1);
INSERT INTO movie_actor (movie_id, actor_id) VALUES (2, 2);
INSERT INTO movie_actor (movie_id, actor_id) VALUES (2, 3);

-- Relationship Movies-Genres
INSERT INTO movie_genre (movie_id, genre_id) VALUES (1, 2); -- Drama
INSERT INTO movie_genre (movie_id, genre_id) VALUES (2, 1); -- Action
INSERT INTO movie_genre (movie_id, genre_id) VALUES (2, 3); -- Sci-Fi
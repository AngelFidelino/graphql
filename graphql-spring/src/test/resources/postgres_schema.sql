
DROP TABLE IF EXISTS authors;
DROP TABLE IF EXISTS books;

CREATE TABLE books(
	id SERIAL PRIMARY KEY,
	name VARCHAR(45),
	pages INT,
	published_at DATE,
	category VARCHAR(45),
	author_id INT
);

CREATE TABLE authors(
	id SERIAL PRIMARY KEY,
	first_name VARCHAR(255),
	last_name VARCHAR(255),
	age INT
);

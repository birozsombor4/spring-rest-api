CREATE TABLE users
(
    id       INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
    username VARCHAR(255)     NOT NULL,
    password VARCHAR(255)     NOT NULL,
    email    VARCHAR(255)     NOT NULL,
    avatar   VARCHAR(255)     NOT NULL,
    verified BIT              NULL,
    PRIMARY KEY (id)
);


CREATE TABLE verification_tokens
(
    id          INT UNSIGNED     NOT NULL AUTO_INCREMENT,
    expiry_date DATETIME,
    token       VARCHAR(255),
    user_id     INTEGER UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE ribac_user
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    external_id VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE ribac_group
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE ribac_group_membership
(
    group_id INT,
    user_id  INT,

    FOREIGN KEY (group_id) REFERENCES ribac_group (id)
        ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES ribac_user (id)
        ON DELETE CASCADE,

    PRIMARY KEY (group_id, user_id)
);



CREATE TABLE ribac_right
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE ribac_right_set
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE ribac_right_set_element
(
    right_set_id INT,
    right_id     INT,

    FOREIGN KEY (right_set_id) REFERENCES ribac_right_set (id)
        ON DELETE CASCADE,
    FOREIGN KEY (right_id) REFERENCES ribac_right (id)
        ON DELETE CASCADE,

    PRIMARY KEY (right_set_id, right_id)
);



CREATE TABLE ribac_user_right
(
    user_id  INT,
    right_id INT,

    FOREIGN KEY (user_id) REFERENCES ribac_user (id)
        ON DELETE CASCADE,
    FOREIGN KEY (right_id) REFERENCES ribac_right (id)
        ON DELETE CASCADE,

    PRIMARY KEY (user_id, right_id)
);

CREATE TABLE ribac_user_right_set
(
    user_id      INT,
    right_set_id INT,

    FOREIGN KEY (user_id) REFERENCES ribac_user (id)
        ON DELETE CASCADE,
    FOREIGN KEY (right_set_id) REFERENCES ribac_right_set (id)
        ON DELETE CASCADE,

    PRIMARY KEY (user_id, right_set_id)
);



CREATE TABLE ribac_group_right
(
    group_id INT,
    right_id INT,

    FOREIGN KEY (group_id) REFERENCES ribac_group (id)
        ON DELETE CASCADE,
    FOREIGN KEY (right_id) REFERENCES ribac_right (id)
        ON DELETE CASCADE,

    PRIMARY KEY (group_id, right_id)
);

CREATE TABLE ribac_group_right_set
(
    group_id     INT,
    right_set_id INT,

    FOREIGN KEY (group_id) REFERENCES ribac_group (id)
        ON DELETE CASCADE,
    FOREIGN KEY (right_set_id) REFERENCES ribac_right_set (id)
        ON DELETE CASCADE,


    PRIMARY KEY (group_id, right_set_id)
);
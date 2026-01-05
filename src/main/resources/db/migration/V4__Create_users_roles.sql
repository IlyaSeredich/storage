CREATE TABLE storage.users_roles(
    user_id BIGINT NOT NULL REFERENCES storage.users(id) ON DELETE CASCADE,
    role_id INT NOT NULL REFERENCES storage.roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id,role_id)
);
-- Insertar datos de prueba en la tabla git_repository

INSERT INTO git_repository (organization, application, microservice, uri, profile, label, enabled)
VALUES 
    ('org1', 'app1', 'ms1', 'https://github.com/samrogu/config-server-props-dev.git', 'dev', 'main', TRUE),
    ('org2', 'app2', 'ms2', 'https://github.com/samrogu/config-server-props-test.git', 'dev', 'master', TRUE);


INSERT INTO users (username, password) VALUES ('user', '$2a$10$7QJz1k5Q9b6e1J9x1z5e1u7QJz1k5Q9b6e1J9x1z5e1u7QJz1k5Q'); -- Contraseña: "password"
INSERT INTO user_roles (user_id, role) VALUES (1, 'ROLE_USER');

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    login TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    status TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cards (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    card_number TEXT NOT NULL UNIQUE,
    card_bin TEXT NOT NULL,
    owner_name TEXT NOT NULL,
    expiration_date DATE NOT NULL,
    status TEXT NOT NULL,
    balance NUMERIC(15, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    user_id UUID NOT NULL,
    CONSTRAINT fk_card_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO roles (id, role) VALUES
    (gen_random_uuid(), 'ROLE_ADMIN'),
    (gen_random_uuid(), 'ROLE_USER')
    ON CONFLICT (role) DO NOTHING;

INSERT INTO users (id, name, login, password, status, created_at, updated_at)
VALUES (
           gen_random_uuid(),
           'panda',
           'admin',
           '$2a$10$6ZnU.7GcorFRRDkecOyRqez19pzZZooska4TgDet7cF1Ep4afM3wW',
           'ACTIVE',
           NOW(),
           NOW()
       )
    ON CONFLICT (login) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.role = 'ROLE_ADMIN'
WHERE u.login = 'admin'
    ON CONFLICT (user_id, role_id) DO NOTHING;

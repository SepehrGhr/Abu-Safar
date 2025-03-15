CREATE TYPE user_type AS ENUM ('USER', 'ADMIN');
CREATE TYPE account_status AS ENUM ('ACTIVE', 'INACTIVE');

CREATE TABLE users (
    user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    user_role user_type NOT NULL DEFAULT 'USER',
    account_status account_status NOT NULL DEFAULT 'ACTIVE',
    city VARCHAR(100) NOT NULL,
    hashed_password VARCHAR(255) NOT NULL,
    sign_up_date TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    profile_picture VARCHAR(255) DEFAULT 'default.png',
    CONSTRAINT valid_first_name CHECK (first_name ~* '^[A-Za-z \-]{1,100}$'),
    CONSTRAINT valid_last_name CHECK (last_name ~* '^[A-Za-z \-]{1,100}$')
);


CREATE TYPE contact_type AS ENUM ('EMAIL', 'PHONE_NUMBER');

CREATE TABLE user_contact (
    user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED,
    contact_type contact_type NOT NULL,
    contact_info VARCHAR(255) UNIQUE NOT NULL,
    PRIMARY KEY (user_id, contact_type),
    CONSTRAINT valid_email_phone CHECK (
        contact_info ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,63}$' 
        OR 
        contact_info ~ '^\+?[0-9\s().-]{7,20}$'
    )
);

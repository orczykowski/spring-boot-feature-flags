CREATE TABLE IF NOT EXISTS feature_flags (
    name VARCHAR(120) PRIMARY KEY,
    enabled VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS feature_flag_assignments (
    flag_name VARCHAR(120) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (flag_name, user_id)
);

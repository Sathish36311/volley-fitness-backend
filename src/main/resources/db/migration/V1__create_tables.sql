CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL
);

CREATE TABLE profiles (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL UNIQUE,
  age INT NOT NULL,
  height INT NOT NULL,
  weight INT NOT NULL,
  gender VARCHAR(50) NOT NULL,
  goal VARCHAR(120) NOT NULL,
  skill_level VARCHAR(120) NOT NULL,
  position VARCHAR(120) NOT NULL,
  CONSTRAINT fk_profiles_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE plans (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  plan_date DATE NOT NULL,
  plan_json TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT fk_plans_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT uq_plans_user_date UNIQUE (user_id, plan_date)
);

CREATE INDEX idx_plans_user_date ON plans(user_id, plan_date);

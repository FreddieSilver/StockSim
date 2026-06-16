CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(20) UNIQUE NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_validation_info VARCHAR(255) NOT NULL,
                       balance DECIMAL(19, 4) NOT NULL
);

CREATE TABLE tokens (
                        id BIGSERIAL PRIMARY KEY,
                        token_validation_info VARCHAR(255) UNIQUE NOT NULL,
                        user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                        last_used_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE companies (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           ticker VARCHAR(10) UNIQUE NOT NULL,
                           description VARCHAR(1000) NOT NULL,
                           volatility DOUBLE PRECISION NOT NULL,
                           base_drift DOUBLE PRECISION NOT NULL
);

CREATE TABLE stocks (
                        id BIGSERIAL PRIMARY KEY,
                        company_id BIGINT NOT NULL UNIQUE REFERENCES companies(id) ON DELETE CASCADE,
                        price DECIMAL(19, 4) NOT NULL
);

CREATE TABLE holdings (
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                          stock_id BIGINT NOT NULL REFERENCES stocks(id) ON DELETE CASCADE,
                          quantity INT NOT NULL,
                          UNIQUE (user_id, stock_id) -- user can only have one holding record per stock
);

CREATE TABLE trade_orders (
                              id BIGSERIAL PRIMARY KEY,
                              user_id BIGINT NOT NULL REFERENCES users(id),
                              stock_id BIGINT NOT NULL REFERENCES stocks(id),
                              type VARCHAR(10) NOT NULL,
                              quantity INT NOT NULL,
                              price_at_order DECIMAL(19, 4) NOT NULL,
                              status VARCHAR(20) NOT NULL
);
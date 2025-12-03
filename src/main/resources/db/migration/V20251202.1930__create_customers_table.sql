CREATE TABLE customers (
    id UUID NOT NULL,
    document VARCHAR(32) NOT NULL,
    name VARCHAR(128) NOT NULL,
    email VARCHAR(128) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT pk_customer PRIMARY KEY (id),
    CONSTRAINT uq_customer_document UNIQUE (document),
    CONSTRAINT ch_customer_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

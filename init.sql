-- Create user and grant privileges
CREATE USER C##support_system IDENTIFIED BY support_password;
GRANT CONNECT, RESOURCE TO C##support_system;
GRANT CREATE SESSION TO C##support_system;
GRANT UNLIMITED TABLESPACE TO C##support_system;

-- Connect as the support_system user
ALTER SESSION SET CURRENT_SCHEMA = C##support_system;

-- Create sequences
CREATE SEQUENCE user_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ticket_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE comment_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE audit_log_seq START WITH 1 INCREMENT BY 1;

-- Create tables
CREATE TABLE users (
    id NUMBER PRIMARY KEY,
    username VARCHAR2(50) UNIQUE NOT NULL,
    password VARCHAR2(100) NOT NULL,
    full_name VARCHAR2(100) NOT NULL,
    role VARCHAR2(20) NOT NULL
);

CREATE TABLE tickets (
    id NUMBER PRIMARY KEY,
    title VARCHAR2(255) NOT NULL,
    description VARCHAR2(1000) NOT NULL,
    priority VARCHAR2(20) NOT NULL,
    category VARCHAR2(20) NOT NULL,
    status VARCHAR2(20) NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    created_by_id NUMBER NOT NULL,
    CONSTRAINT fk_tickets_user FOREIGN KEY (created_by_id) REFERENCES users(id)
);

CREATE TABLE comments (
    id NUMBER PRIMARY KEY,
    content VARCHAR2(1000) NOT NULL,
    ticket_id NUMBER NOT NULL,
    user_id NUMBER NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_comments_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id),
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE audit_logs (
    id NUMBER PRIMARY KEY,
    ticket_id NUMBER NOT NULL,
    user_id NUMBER NOT NULL,
    action VARCHAR2(50) NOT NULL,
    old_value VARCHAR2(255) NOT NULL,
    new_value VARCHAR2(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT fk_audit_logs_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id),
    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create initial users
INSERT INTO users (id, username, password, full_name, role)
VALUES (user_seq.nextval, 'admin', 
        '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', -- password: admin
        'System Administrator', 'IT_SUPPORT');

INSERT INTO users (id, username, password, full_name, role)
VALUES (user_seq.nextval, 'employee', 
        '$2a$10$hS.IbQJHJ9KW/HUkR1zA3.vtUh3HS4YQvGBz.RKX/YhXwzQkY4wDi', -- password: employee
        'Test Employee', 'EMPLOYEE');

COMMIT; 
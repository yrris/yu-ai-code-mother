-- =====================================================
-- PostgreSQL Database Migration
-- AI Code Generation Platform (Yu AI Code Mother)
-- Author: Refactored for Australian Tech Stack
-- =====================================================

-- Create database (run this separately as postgres superuser if needed)
-- CREATE DATABASE yu_ai_code_mother WITH ENCODING 'UTF8' LC_COLLATE='en_US.UTF-8' LC_CTYPE='en_US.UTF-8';

-- =====================================================
-- Enable extensions
-- =====================================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- User Table
-- =====================================================
CREATE TABLE IF NOT EXISTS "user" (
    id                  BIGSERIAL PRIMARY KEY,
    user_account        VARCHAR(256) NOT NULL,
    user_password       VARCHAR(512) NOT NULL,
    user_name           VARCHAR(256),
    user_avatar         VARCHAR(1024),
    user_profile        VARCHAR(512),
    user_role           VARCHAR(256) NOT NULL DEFAULT 'user',
    edit_time           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_delete           SMALLINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_user_account UNIQUE (user_account)
);

-- User table indexes
CREATE INDEX IF NOT EXISTS idx_user_name ON "user"(user_name);
CREATE INDEX IF NOT EXISTS idx_user_role ON "user"(user_role) WHERE is_delete = 0;

-- User table comments
COMMENT ON TABLE "user" IS 'User accounts and profiles';
COMMENT ON COLUMN "user".id IS 'Primary key - auto-generated';
COMMENT ON COLUMN "user".user_account IS 'Unique user account identifier';
COMMENT ON COLUMN "user".user_password IS 'Hashed password';
COMMENT ON COLUMN "user".user_name IS 'Display name';
COMMENT ON COLUMN "user".user_avatar IS 'Avatar URL';
COMMENT ON COLUMN "user".user_profile IS 'User bio/profile description';
COMMENT ON COLUMN "user".user_role IS 'User role: user/admin';
COMMENT ON COLUMN "user".edit_time IS 'Last edit time';
COMMENT ON COLUMN "user".create_time IS 'Creation timestamp';
COMMENT ON COLUMN "user".update_time IS 'Last update timestamp';
COMMENT ON COLUMN "user".is_delete IS 'Soft delete flag: 0=active, 1=deleted';

-- =====================================================
-- App Table
-- =====================================================
CREATE TABLE IF NOT EXISTS app (
    id                  BIGSERIAL PRIMARY KEY,
    app_name            VARCHAR(256),
    cover               VARCHAR(512),
    init_prompt         TEXT,
    code_gen_type       VARCHAR(64),
    deploy_key          VARCHAR(64),
    deployed_time       TIMESTAMP,
    priority            INTEGER NOT NULL DEFAULT 0,
    user_id             BIGINT NOT NULL,
    edit_time           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_delete           SMALLINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_deploy_key UNIQUE (deploy_key),
    CONSTRAINT fk_app_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE
);

-- App table indexes
CREATE INDEX IF NOT EXISTS idx_app_name ON app(app_name) WHERE is_delete = 0;
CREATE INDEX IF NOT EXISTS idx_app_user_id ON app(user_id) WHERE is_delete = 0;
CREATE INDEX IF NOT EXISTS idx_app_priority ON app(priority DESC) WHERE is_delete = 0;
CREATE INDEX IF NOT EXISTS idx_app_code_gen_type ON app(code_gen_type) WHERE is_delete = 0;
CREATE INDEX IF NOT EXISTS idx_app_create_time ON app(create_time DESC) WHERE is_delete = 0;

-- App table comments
COMMENT ON TABLE app IS 'AI-generated applications';
COMMENT ON COLUMN app.id IS 'Primary key - auto-generated';
COMMENT ON COLUMN app.app_name IS 'Application name';
COMMENT ON COLUMN app.cover IS 'Cover image URL (screenshot)';
COMMENT ON COLUMN app.init_prompt IS 'Initial prompt used to generate the app';
COMMENT ON COLUMN app.code_gen_type IS 'Code generation type: html, multi_file, vue_project';
COMMENT ON COLUMN app.deploy_key IS 'Unique deployment identifier';
COMMENT ON COLUMN app.deployed_time IS 'Deployment timestamp';
COMMENT ON COLUMN app.priority IS 'Priority for featured apps (higher = more prominent)';
COMMENT ON COLUMN app.user_id IS 'Creator user ID';
COMMENT ON COLUMN app.edit_time IS 'Last edit time';
COMMENT ON COLUMN app.create_time IS 'Creation timestamp';
COMMENT ON COLUMN app.update_time IS 'Last update timestamp';
COMMENT ON COLUMN app.is_delete IS 'Soft delete flag: 0=active, 1=deleted';

-- =====================================================
-- Chat History Table
-- =====================================================
CREATE TABLE IF NOT EXISTS chat_history (
    id                  BIGSERIAL PRIMARY KEY,
    message             TEXT NOT NULL,
    message_type        VARCHAR(32) NOT NULL,
    app_id              BIGINT NOT NULL,
    user_id             BIGINT NOT NULL,
    create_time         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_delete           SMALLINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_chat_app FOREIGN KEY (app_id) REFERENCES app(id) ON DELETE CASCADE,
    CONSTRAINT fk_chat_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    CONSTRAINT chk_message_type CHECK (message_type IN ('user', 'ai'))
);

-- Chat history indexes (optimized for cursor-based pagination)
CREATE INDEX IF NOT EXISTS idx_chat_app_id ON chat_history(app_id) WHERE is_delete = 0;
CREATE INDEX IF NOT EXISTS idx_chat_create_time ON chat_history(create_time DESC) WHERE is_delete = 0;
CREATE INDEX IF NOT EXISTS idx_chat_app_time ON chat_history(app_id, create_time DESC) WHERE is_delete = 0;
CREATE INDEX IF NOT EXISTS idx_chat_user_id ON chat_history(user_id) WHERE is_delete = 0;

-- Chat history comments
COMMENT ON TABLE chat_history IS 'Conversation history between users and AI';
COMMENT ON COLUMN chat_history.id IS 'Primary key - auto-generated';
COMMENT ON COLUMN chat_history.message IS 'Chat message content';
COMMENT ON COLUMN chat_history.message_type IS 'Message sender: user or ai';
COMMENT ON COLUMN chat_history.app_id IS 'Associated application ID';
COMMENT ON COLUMN chat_history.user_id IS 'User ID who owns this chat';
COMMENT ON COLUMN chat_history.create_time IS 'Creation timestamp';
COMMENT ON COLUMN chat_history.update_time IS 'Last update timestamp';
COMMENT ON COLUMN chat_history.is_delete IS 'Soft delete flag: 0=active, 1=deleted';

-- =====================================================
-- Trigger Functions for auto-updating update_time
-- =====================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply triggers to all tables
CREATE TRIGGER update_user_updated_at BEFORE UPDATE ON "user"
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_app_updated_at BEFORE UPDATE ON app
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_chat_history_updated_at BEFORE UPDATE ON chat_history
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Performance optimization: Partitioning for chat_history
-- =====================================================
-- Note: For large-scale deployments, consider partitioning chat_history by date
-- This is commented out for initial setup, uncomment for production use

-- ALTER TABLE chat_history RENAME TO chat_history_template;
-- CREATE TABLE chat_history (LIKE chat_history_template INCLUDING ALL) PARTITION BY RANGE (create_time);
-- CREATE TABLE chat_history_2025 PARTITION OF chat_history FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');

-- =====================================================
-- Grant permissions (adjust based on your setup)
-- =====================================================
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO yu_ai_code_user;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO yu_ai_code_user;

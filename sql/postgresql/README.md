# PostgreSQL Migration Guide

## Overview

This directory contains PostgreSQL migration scripts for the Yu AI Code Mother platform, refactored from MySQL to PostgreSQL as part of the Australian tech stack modernization.

## Key Changes from MySQL

### 1. **Data Type Conversions**

| MySQL | PostgreSQL | Reason |
|-------|------------|--------|
| `BIGINT AUTO_INCREMENT` | `BIGSERIAL` | Native PostgreSQL auto-increment |
| `TINYINT` | `SMALLINT` | PostgreSQL doesn't have TINYINT |
| `DATETIME` | `TIMESTAMP` | PostgreSQL standard timestamp type |
| `VARCHAR` | `VARCHAR` | Same (compatible) |
| `TEXT` | `TEXT` | Same (compatible) |

### 2. **Naming Conventions**

- **Snake_case for columns**: `user_account`, `create_time` (PostgreSQL best practice)
- **Quoted table names**: `"user"` is a reserved word in PostgreSQL
- **Lowercase table names**: `app`, `chat_history` (PostgreSQL convention)

### 3. **Enhanced Features**

#### Auto-Update Triggers
PostgreSQL triggers automatically update `update_time` on row modifications:

```sql
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

#### Foreign Key Constraints
Enforced referential integrity:
- `app.user_id` → `user.id` (CASCADE DELETE)
- `chat_history.app_id` → `app.id` (CASCADE DELETE)
- `chat_history.user_id` → `user.id` (CASCADE DELETE)

#### Check Constraints
Data validation at database level:
```sql
CONSTRAINT chk_message_type CHECK (message_type IN ('user', 'ai'))
```

#### Partial Indexes
Optimized indexes that exclude soft-deleted rows:
```sql
CREATE INDEX idx_app_name ON app(app_name) WHERE is_delete = 0;
```

### 4. **Performance Optimizations**

- **Partial indexes** on non-deleted rows reduce index size by 50%+
- **Composite indexes** for cursor-based pagination
- **Foreign key indexes** for join performance
- **Trigger-based timestamp updates** for consistency

## Migration Scripts

### V1__create_tables.sql
Creates all database tables with proper constraints and indexes:
- `user` - User accounts and authentication
- `app` - AI-generated applications
- `chat_history` - Conversation history

### V2__seed_data.sql
Inserts sample development data:
- Admin and demo users
- Sample applications
- Chat conversation examples

## Installation

### 1. Install PostgreSQL

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
```

**macOS (Homebrew):**
```bash
brew install postgresql@15
brew services start postgresql@15
```

**Docker:**
```bash
docker run --name postgres-yuai \
  -e POSTGRES_PASSWORD=yourpassword \
  -e POSTGRES_DB=yu_ai_code_mother \
  -p 5432:5432 \
  -d postgres:15-alpine
```

### 2. Create Database

```bash
# Connect as postgres user
sudo -u postgres psql

# Create database
CREATE DATABASE yu_ai_code_mother
  WITH ENCODING 'UTF8'
  LC_COLLATE='en_US.UTF-8'
  LC_CTYPE='en_US.UTF-8';

# Create application user
CREATE USER yu_ai_code_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE yu_ai_code_mother TO yu_ai_code_user;

\q
```

### 3. Run Migrations

```bash
# Connect to database
psql -U postgres -d yu_ai_code_mother

# Run schema creation
\i /path/to/sql/postgresql/V1__create_tables.sql

# Run seed data (optional, for development)
\i /path/to/sql/postgresql/V2__seed_data.sql

\q
```

### 4. Verify Installation

```bash
psql -U postgres -d yu_ai_code_mother -c "
SELECT
  'user' as table_name,
  COUNT(*) as row_count
FROM \"user\"
UNION ALL
SELECT 'app', COUNT(*) FROM app
UNION ALL
SELECT 'chat_history', COUNT(*) FROM chat_history;
"
```

## Migrating Existing MySQL Data

### Option 1: Using pgloader (Recommended)

```bash
# Install pgloader
sudo apt install pgloader  # Ubuntu/Debian
brew install pgloader      # macOS

# Create migration config
cat > migrate.load << EOF
LOAD DATABASE
  FROM mysql://user:password@localhost/yu_ai_code_mother
  INTO postgresql://yu_ai_code_user:password@localhost/yu_ai_code_mother

WITH include drop, create tables, create indexes, reset sequences

SET maintenance_work_mem to '256MB',
    work_mem to '64MB'

CAST type tinyint to smallint drop typemod,
     type datetime to timestamp drop timezone

BEFORE LOAD DO
  \$\$ DROP SCHEMA IF EXISTS public CASCADE; \$\$,
  \$\$ CREATE SCHEMA public; \$\$;
EOF

# Run migration
pgloader migrate.load
```

### Option 2: Manual Export/Import

```bash
# 1. Export from MySQL
mysqldump -u root -p \
  --no-create-info \
  --complete-insert \
  --compatible=postgresql \
  yu_ai_code_mother > mysql_data.sql

# 2. Convert data types (manual editing required)
# Edit mysql_data.sql to match PostgreSQL syntax

# 3. Import to PostgreSQL
psql -U postgres -d yu_ai_code_mother < mysql_data.sql
```

## Connection Configuration

### Spring Boot application.yml

```yaml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/yu_ai_code_mother
    username: yu_ai_code_user
    password: ${DB_PASSWORD:your_password}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate  # Use 'none' in production
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
    show-sql: false
```

### Environment Variables

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=yu_ai_code_mother
export DB_USER=yu_ai_code_user
export DB_PASSWORD=your_secure_password
```

## Development vs Production

### Development
- Use `seed_data.sql` for sample data
- Set `spring.jpa.hibernate.ddl-auto=validate`
- Enable SQL logging for debugging

### Production
- **Never** use seed data
- Set `spring.jpa.hibernate.ddl-auto=none`
- Use connection pooling (HikariCP configured above)
- Enable SSL/TLS for database connections
- Use prepared statements (automatic with JPA)
- Regular backups with `pg_dump`

## Backup and Restore

### Backup

```bash
# Full backup
pg_dump -U postgres -d yu_ai_code_mother -F c -f backup.dump

# Schema only
pg_dump -U postgres -d yu_ai_code_mother -s -f schema.sql

# Data only
pg_dump -U postgres -d yu_ai_code_mother -a -f data.sql
```

### Restore

```bash
# From custom format
pg_restore -U postgres -d yu_ai_code_mother backup.dump

# From SQL file
psql -U postgres -d yu_ai_code_mother < backup.sql
```

## Performance Tuning

### Analyze Tables

```sql
ANALYZE "user";
ANALYZE app;
ANALYZE chat_history;
```

### Vacuum (Clean up dead rows)

```sql
VACUUM ANALYZE;
```

### Monitor Query Performance

```sql
-- Enable query statistics
CREATE EXTENSION IF NOT EXISTS pg_stat_statements;

-- Find slow queries
SELECT
  query,
  calls,
  total_time,
  mean_time
FROM pg_stat_statements
ORDER BY mean_time DESC
LIMIT 10;
```

## Troubleshooting

### Issue: "relation does not exist"
```sql
-- Check if table exists
\dt

-- Check search path
SHOW search_path;

-- Set search path if needed
SET search_path TO public;
```

### Issue: Permission denied
```sql
-- Grant all privileges
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO yu_ai_code_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO yu_ai_code_user;
```

### Issue: Password authentication failed
```bash
# Edit pg_hba.conf (location varies by OS)
# Change method from 'peer' to 'md5' or 'scram-sha-256'
sudo nano /etc/postgresql/15/main/pg_hba.conf

# Restart PostgreSQL
sudo systemctl restart postgresql
```

## References

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Hibernate PostgreSQL Dialect](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#database-postgresql)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [pgloader Migration Tool](https://pgloader.io/)

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review PostgreSQL logs: `tail -f /var/log/postgresql/postgresql-15-main.log`
3. Enable Spring Boot SQL logging for query debugging

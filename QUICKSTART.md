# 🚀 Quick Start Guide - Australian Tech Stack

Get up and running with the refactored Yu AI Code Mother platform in minutes!

## Prerequisites

- **Java 21** or higher
- **PostgreSQL 15** or higher
- **Node.js 18.17** or higher
- **npm 9** or higher
- **Maven 3.8** or higher

## 🗄️ Step 1: Set Up PostgreSQL

### Option A: Docker (Recommended for Quick Start)

```bash
docker run --name postgres-yuai \
  -e POSTGRES_PASSWORD=changeme \
  -e POSTGRES_DB=yu_ai_code_mother \
  -p 5432:5432 \
  -d postgres:15-alpine

# Wait a few seconds for PostgreSQL to start
sleep 5

# Run migrations
docker exec -i postgres-yuai psql -U postgres -d yu_ai_code_mother < sql/postgresql/V1__create_tables.sql

# (Optional) Load sample data
docker exec -i postgres-yuai psql -U postgres -d yu_ai_code_mother < sql/postgresql/V2__seed_data.sql
```

### Option B: Local Installation

```bash
# Install PostgreSQL (Ubuntu/Debian)
sudo apt update && sudo apt install postgresql-15

# Create database and user
sudo -u postgres psql << EOF
CREATE DATABASE yu_ai_code_mother;
CREATE USER yu_ai_code_user WITH PASSWORD 'changeme';
GRANT ALL PRIVILEGES ON DATABASE yu_ai_code_mother TO yu_ai_code_user;
GRANT ALL PRIVILEGES ON SCHEMA public TO yu_ai_code_user;
EOF

# Run migrations
psql -U postgres -d yu_ai_code_mother -f sql/postgresql/V1__create_tables.sql
psql -U postgres -d yu_ai_code_mother -f sql/postgresql/V2__seed_data.sql
```

## 🔧 Step 2: Configure Backend

### Update Application Configuration

```bash
# Create local configuration
cp src/main/resources/application.yml src/main/resources/application-local.yml

# Edit database credentials (or use environment variable)
export DB_PASSWORD=changeme
```

### Update AI API Keys

Edit `src/main/resources/application.yml` and add your API keys:

```yaml
langchain4j:
  open-ai:
    chat-model:
      api-key: <Your DeepSeek API Key>
    streaming-chat-model:
      api-key: <Your DeepSeek API Key>
```

## ▶️ Step 3: Start Backend

```bash
# Clean and build
./mvnw clean package -DskipTests

# Run the application
./mvnw spring-boot:run

# Or use the JAR
# java -jar target/yu-ai-code-mother-0.0.1-SNAPSHOT.jar
```

The backend API will be available at: **http://localhost:8123/api**

### Verify Backend

```bash
# Health check
curl http://localhost:8123/api/health

# API documentation (Swagger)
open http://localhost:8123/api/doc.html
```

## 🎨 Step 4: Set Up Frontend

```bash
# Navigate to frontend directory
cd yu-ai-code-nextjs

# Install dependencies
npm install

# Create environment file
cp .env.local.example .env.local

# Edit .env.local if needed (defaults should work)
# NEXT_PUBLIC_API_URL=http://localhost:8123/api
```

## ▶️ Step 5: Start Frontend

```bash
# Run development server
npm run dev
```

The frontend will be available at: **http://localhost:3000**

## ✅ Step 6: Test the Application

### 1. **Visit the Homepage**

Open http://localhost:3000 in your browser

### 2. **Register a New User**

- Click "Get Started" or "Register"
- Create an account with:
  - Account: `testuser`
  - Password: `password123`

### 3. **Login**

- Use your credentials to log in

### 4. **Create Your First App**

- Click "Create Your First App"
- Enter a prompt like:
  ```
  Create a simple todo list with add, delete, and mark as complete features
  ```
- Watch as AI generates your application in real-time!

### 5. **View Generated Code**

- Once generated, you can:
  - Preview the app in the iframe
  - Download the source code
  - Deploy the app
  - Continue chatting to refine it

## 🔍 Troubleshooting

### Backend Issues

**Database Connection Failed**

```bash
# Check PostgreSQL is running
docker ps | grep postgres
# or
sudo systemctl status postgresql

# Test connection
psql -U postgres -d yu_ai_code_mother -c "SELECT 1;"
```

**Port 8123 Already in Use**

```bash
# Find and kill the process
lsof -ti:8123 | xargs kill -9

# Or change port in application.yml
server:
  port: 8124
```

### Frontend Issues

**Port 3000 Already in Use**

```bash
# Run on different port
PORT=3001 npm run dev
```

**API Connection Error**

- Verify backend is running: http://localhost:8123/api/health
- Check CORS configuration in backend
- Verify `.env.local` has correct API URL

## 📚 Next Steps

### Learn the Tech Stack

- **Backend**: Read [Spring Data JPA Guide](https://spring.io/guides/gs/accessing-data-jpa/)
- **Frontend**: Read [Next.js Tutorial](https://nextjs.org/learn)
- **React Query**: Read [TanStack Query Docs](https://tanstack.com/query/latest)
- **Tailwind**: Read [Tailwind CSS Docs](https://tailwindcss.com/docs)

### Explore Features

1. **AI Chat**: Real-time streaming with Server-Sent Events (SSE)
2. **Code Generation**: Multiple types (HTML, Multi-file, Vue project)
3. **App Deployment**: One-click deployment to cloud
4. **Screenshot Generation**: Automatic previews with Selenium
5. **Admin Panel**: User and app management

### Production Deployment

See [AUSTRALIAN_STACK_REFACTORING.md](AUSTRALIAN_STACK_REFACTORING.md) for production deployment guides:

- Docker deployment
- Environment configuration
- Security best practices
- Performance optimization

## 🎯 Sample Data

If you ran `V2__seed_data.sql`, you can login with:

- **Admin**: `admin` / `12345678`
- **Demo User**: `demo_user` / `12345678`

## 🛠️ Development Tools

### Backend

```bash
# Run tests
./mvnw test

# Generate code coverage
./mvnw clean verify

# View API docs
open http://localhost:8123/api/doc.html
```

### Frontend

```bash
# Type checking
npm run type-check

# Linting
npm run lint

# Code formatting
npm run format

# React Query DevTools
# Automatically available in dev mode at bottom of screen
```

## 📊 Monitoring

### Backend

- **Health**: http://localhost:8123/api/health
- **Prometheus Metrics**: http://localhost:8123/api/actuator/prometheus

### Frontend

- **React Query DevTools**: Bottom-left corner in dev mode
- **Browser DevTools**: Network tab for API calls

## 🎨 Customization

### Frontend Theme

Edit `yu-ai-code-nextjs/src/app/globals.css`:

```css
:root {
  --primary: 221.2 83.2% 53.3%; /* Your brand color */
  /* ... */
}
```

### Backend Configuration

Edit `src/main/resources/application.yml`:

```yaml
# Change server port
server:
  port: 8080

# Modify AI model
langchain4j:
  open-ai:
    chat-model:
      model-name: gpt-4  # Use different model
```

## 🤝 Need Help?

- **Documentation**: [AUSTRALIAN_STACK_REFACTORING.md](AUSTRALIAN_STACK_REFACTORING.md)
- **Database Guide**: [sql/postgresql/README.md](sql/postgresql/README.md)
- **Frontend README**: [yu-ai-code-nextjs/README.md](yu-ai-code-nextjs/README.md)

---

**Ready to build? Start creating amazing apps with AI! 🚀**

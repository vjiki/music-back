# Environment Variables Setup

This application requires the following environment variables to be set:

## Required Environment Variables

- `DB_URL` - PostgreSQL database connection URL
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password

## Setup Instructions

### Option 1: Using .env file (Local Development)

1. Copy the example file:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` and fill in your actual values:
   ```bash
   DB_URL=jdbc:postgresql://vjiki-vjiki.h.aivencloud.com:23946/defaultdb?sslmode=require
   DB_USERNAME=music
   DB_PASSWORD=your_actual_password
   ```

3. The `.env` file is already in `.gitignore` and will not be committed.

### Option 2: Export Environment Variables (Unix/Mac)

```bash
export DB_URL="jdbc:postgresql://vjiki-vjiki.h.aivencloud.com:23946/defaultdb?sslmode=require"
export DB_USERNAME="music"
export DB_PASSWORD="your_actual_password"
```

### Option 3: Set in IDE (IntelliJ IDEA / DataGrip)

1. Go to Run → Edit Configurations
2. Add environment variables in the "Environment variables" section
3. Add: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`

### Option 4: For Production/Deployment

Set environment variables in your deployment platform:
- **Render**: Add in Environment section
- **Docker**: Use `-e` flags or docker-compose.yml
- **Kubernetes**: Use ConfigMap or Secrets
- **GitHub Actions**: Use repository secrets

## Spring Boot Configuration

Spring Boot automatically loads environment variables. The `application.properties` file uses the syntax:
```properties
spring.datasource.password=${DB_PASSWORD:}
```

This means: use `DB_PASSWORD` environment variable, or default to empty string if not set.

## Security Note

⚠️ **Never commit actual passwords or secrets to version control!**

The `.env` file is already in `.gitignore` to prevent accidental commits.


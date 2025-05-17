# Bilkom Backend

This is the backend for the Bilkom application, a social platform for Bilkent University students.

## Environment Variables

For security and flexibility, the application uses environment variables. You can set these variables in your development environment or in a production deployment.

### Required Environment Variables

The following environment variables should be set:

```
# Database Configuration
BILKOM_DB_URL=jdbc:mysql://192.168.231.145:3306/bilkom_db
BILKOM_DB_USERNAME=your_db_username
BILKOM_DB_PASSWORD=your_db_password

# JWT Configuration
BILKOM_JWT_SECRET=your_very_secure_jwt_secret_key

# Email Configuration
BILKOM_EMAIL_USERNAME=your_email@gmail.com
BILKOM_EMAIL_PASSWORD=your_email_app_password

# Admin Email
BILKOM_ADMIN_EMAIL=admin@example.com

# Base URL
BILKOM_BASE_URL=http://your-production-url
```

### Setting Environment Variables

#### Windows:

```
set BILKOM_DB_URL=jdbc:mysql://192.168.231.145:3306/bilkom_db
set BILKOM_DB_USERNAME=your_db_username
set BILKOM_DB_PASSWORD=your_db_password
set BILKOM_JWT_SECRET=your_very_secure_jwt_secret_key
set BILKOM_EMAIL_USERNAME=your_email@gmail.com
set BILKOM_EMAIL_PASSWORD=your_email_app_password
set BILKOM_ADMIN_EMAIL=admin@example.com
set BILKOM_BASE_URL=http://your-production-url
```

#### Linux/MacOS:

```
export BILKOM_DB_URL=jdbc:mysql://192.168.231.145:3306/bilkom_db
export BILKOM_DB_USERNAME=your_db_username
export BILKOM_DB_PASSWORD=your_db_password
export BILKOM_JWT_SECRET=your_very_secure_jwt_secret_key
export BILKOM_EMAIL_USERNAME=your_email@gmail.com
export BILKOM_EMAIL_PASSWORD=your_email_app_password
export BILKOM_ADMIN_EMAIL=admin@example.com
export BILKOM_BASE_URL=http://your-production-url
```

## Development

For local development, the application provides default values in the application.properties file, but it's recommended to set these environment variables with your own values for security. 
#!/bin/bash

# Start SSH server
/usr/sbin/sshd

# Wait for MySQL to be ready
echo "Waiting for MySQL..."
for i in $(seq 1 30); do
    if mysqladmin ping -h db -u root -proot --silent 2>/dev/null; then
        echo "MySQL is ready!"
        break
    fi
    echo "Waiting for MySQL... attempt $i"
    sleep 2
done

# Start NGINX
nginx -g "daemon off;" &

# Start the Spring Boot application
echo "Starting Spring Boot application..."
java -jar /app/app.jar &

# Keep container running
wait
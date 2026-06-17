#!/bin/bash
# Backup MySQL database from mysql-db container
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_DIR="/root/backups"
DB_NAME="A-NETH_Tithyarita-db"
DB_USER="root"
DB_PASS="Hello@123"
CONTAINER="mysql-db"

# Create backup directory inside container
docker exec "$CONTAINER" mkdir -p "$BACKUP_DIR"

# Run mysqldump inside the container and save
docker exec "$CONTAINER" mysqldump -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" > "${BACKUP_DIR}/${DB_NAME}_${TIMESTAMP}.sql" 2>&1

echo "Backup saved to ${BACKUP_DIR}/${DB_NAME}_${TIMESTAMP}.sql"
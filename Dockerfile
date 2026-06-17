FROM ubuntu:22.04

# Prevent interactive prompts
ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Asia/Bangkok

# Install dependencies: JDK 21, NGINX, SSH, curl
RUN apt-get update && apt-get install -y \
    openjdk-21-jdk \
    nginx \
    openssh-server \
    curl \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Create app directory
WORKDIR /app

# Copy the pre-built JAR from local target folder
COPY demo/target/demo-0.0.1-SNAPSHOT.jar /app/app.jar

# Copy nginx config
COPY nginx/default.conf /etc/nginx/sites-available/default

# Enable nginx site
RUN ln -sf /etc/nginx/sites-available/default /etc/nginx/sites-enabled/ && \
    rm -f /etc/nginx/sites-enabled/default

# SSH setup - allow root login with password
RUN mkdir /var/run/sshd && \
    echo 'root:root' | chpasswd && \
    sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config && \
    sed -i 's/#PasswordAuthentication yes/PasswordAuthentication yes/' /etc/ssh/sshd_config

# Copy startup script
COPY start.sh /start.sh
RUN chmod +x /start.sh

# Expose ports: 8080 (NGINX web proxy), 2222 (SSH)
EXPOSE 8080 2222

CMD ["/start.sh"]
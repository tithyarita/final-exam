FROM ubuntu:22.04

# Prevent interactive prompts
ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Asia/Bangkok

# Install dependencies: JDK 21, NGINX, SSH, MySQL client, Git, Maven, Python, Ansible
RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -y tzdata && \
    apt-get install -y \
    openjdk-21-jdk maven \
    nginx \
    openssh-server \
    mysql-client \
    git curl python3 python3-pip python3-venv \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* && \
    ln -fs /usr/share/zoneinfo/Asia/Bangkok /etc/localtime && \
    pip3 install ansible --break-system-packages 2>/dev/null || true

# Create app directory and copy pre-built JAR
WORKDIR /app
COPY demo/target/demo-0.0.1-SNAPSHOT.jar /app/app.jar

# Remove default nginx config (port 80)
RUN rm -f /etc/nginx/sites-enabled/default /etc/nginx/sites-available/default

# Copy nginx config (listens on 8080, proxies to Spring Boot 8081)
COPY nginx/default.conf /etc/nginx/conf.d/default.conf

# SSH setup - allow root login with password
RUN mkdir -p /var/run/sshd && \
    echo 'root:root' | chpasswd && \
    sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config && \
    sed -i 's/#PasswordAuthentication yes/PasswordAuthentication yes/' /etc/ssh/sshd_config

# Copy Ansible playbook and inventory
COPY ansible /app/ansible

# Copy startup script
COPY start.sh /start.sh
RUN chmod +x /start.sh

# Expose ports: 8080 (NGINX web proxy), 2222 (SSH)
EXPOSE 8080 2222

CMD ["/start.sh"]
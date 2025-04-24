# 使用 OpenJDK 17 Alpine 镜像
FROM openjdk:17-jdk-alpine

# 安装必要的包，包括中文字体支持和 locale
RUN apk add --no-cache \
    freetype \
    fontconfig \
    tzdata \
    curl \
    bash \
    musl-locales \
    musl-locales-lang

# 设置时区为上海
RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 设置 locale 环境变量
ENV LANG=zh_CN.UTF-8
ENV LANGUAGE=zh_CN:zh
ENV LC_ALL=zh_CN.UTF-8

# 创建字体目录
RUN mkdir -p /usr/share/fonts/simsun

# 复制宋体字体文件到容器中
COPY src/main/resources/fonts/SimSun.ttf /usr/share/fonts/simsun/

# 更新字体缓存
RUN fc-cache -f -v

# 设置工作目录
WORKDIR /app

# 复制应用程序的 JAR 文件
COPY target/*.jar app.jar

# 暴露端口
EXPOSE 9001

# 启动应用，设置文件编码为 UTF-8
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]

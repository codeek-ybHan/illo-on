# ── build ──
FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
# 컨테이너에서는 nginx 프록시(/api)를 통해 백엔드로 감
ENV VITE_API_BASE_URL=/api
RUN npm run build

# ── serve ──
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80

FROM node:20 AS build
WORKDIR /app

# Dépendances
COPY package.json package-lock.json* ./
RUN npm ci --silent || npm i --silent

# Code + fichiers d'entrée Vite
COPY index.html ./
COPY src ./src
COPY public ./public
COPY vite.config.js ./

# Build
RUN npm run build

# Nginx pour servir le build
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx","-g","daemon off;"]

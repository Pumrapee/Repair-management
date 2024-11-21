FROM node:lts-alpine AS build
WORKDIR /code
COPY . .
RUN npm install
RUN npm run build

FROM nginx:alpine AS run
ENV TZ=UTC
COPY --from=build /code/dist/ /dist/
COPY default.conf /etc/nginx/conf.d/default.conf
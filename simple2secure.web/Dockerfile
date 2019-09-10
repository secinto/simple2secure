### STAGE 1: Build ###

# We label our stage as ‘builder’
FROM node:10-alpine AS builder

COPY package.json package-lock.json ./

## Storing node modules on a separate layer will prevent unnecessary npm installs at each build

RUN apk add --no-cache git python

RUN npm ci && mkdir /ng-app && mv ./node_modules ./ng-app 

WORKDIR /ng-app

COPY . .

## Build the angular app in production mode and store the artifacts in dist folder

RUN $(npm bin)/ng build --prod


### STAGE 2: Setup ###

FROM nginx:1.14.1-alpine

## Copy our default nginx config and certificates
COPY nginx/default.conf /etc/nginx/conf.d/
COPY certificates/s2sinfo.cer /etc/ssl/
COPY certificates/s2sinfo.p12 /etc/ssl

## Remove default nginx website
RUN rm -rf /usr/share/nginx/html/*

## From ‘builder’ stage copy over the artifacts in dist folder to default nginx public folder
COPY --from=builder /ng-app/dist /usr/share/nginx/html

CMD ["nginx", "-g", "daemon off;"]
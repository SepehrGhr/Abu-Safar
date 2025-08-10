
FROM node:lts-slim

# Set the working directory
WORKDIR /app

# 1. Copy package.json and package-lock.json (if it exists)
COPY package*.json ./

# 2. Install dependencies. This creates a node_modules layer in the image
#    that is separate from any local volume.
RUN npm install

# 3. Now copy the rest of your source code. The volume mount from
#    docker-compose will overwrite this for development, which is what we want.
COPY . .

# Vite's default port is 5173
EXPOSE 5173

# The command to run the dev server
CMD ["npm", "run", "dev"]
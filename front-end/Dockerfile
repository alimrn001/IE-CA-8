# Use an official Node.js runtime as a parent image
FROM node:latest as build

# Set the working directory to /app
WORKDIR /app

# Copy the package.json and package-lock.json files
COPY package*.json ./

# Install the required packages
RUN npm install

# Copy the application code
COPY . .

# Build the application
RUN npm run build

# Use an official Nginx runtime as a parent image
FROM nginx:latest

# Copy the build files to the Nginx directory
COPY --from=build /app/build /usr/share/nginx/html

# Expose port 80 to the outside world
EXPOSE 80

# Define the command to run when the container starts
CMD ["nginx", "-g", "daemon off;"]
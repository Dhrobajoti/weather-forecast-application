# Stage 1: Build
# Use Node.js 18 image as the build environment
FROM node:18 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json to the working directory
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application source code
COPY . .


# Stage 2: Runtime
# Use a lightweight Node.js 18 Alpine image for the runtime environment
FROM node:18-alpine

# Set the working directory inside the runtime container
WORKDIR /app

# Copy built files and dependencies from the builder stage
COPY --from=builder /app .

# Expose port 8085 for the application
EXPOSE 8085

# Start the application using npm
CMD ["npm", "start"]
#!/bin/bash

# --- Configuration ---
PROJECT_NAME=$1
echo "Running script for project" $PROJECT_NAME
cd $PROJECT_NAME
IMAGE_NAME=djaniga/$PROJECT_NAME
TAG=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

# --- Build the app ---
echo "🛠️ Building JAR..."
mvn clean verify || { echo "Build failed"; exit 1; }

# --- Build Docker image ---
echo "🐳 Building Docker image..."
docker build -t $IMAGE_NAME:$TAG . || { echo "Build failed"; exit 1; }
docker tag $IMAGE_NAME:$TAG $IMAGE_NAME:latest || { echo "Build failed"; exit 1; }

# --- Push to Docker Hub ---
echo "📤 Pushing to Docker Hub..."
docker push $IMAGE_NAME:$TAG || { echo "Push failed"; exit 1; }
docker push $IMAGE_NAME:latest || { echo "Push failed"; exit 1; }

# --- Info ---
echo "✅ Image pushed: $IMAGE_NAME:$TAG"
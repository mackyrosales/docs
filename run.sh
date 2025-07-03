#!/bin/bash

echo "🔮 Starting Teedy with Violet Theme..."

# Set environment variables
export JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-11-openjdk-amd64/}"
export MAVEN_OPTS="-Xmx1g"

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check dependencies
echo "📋 Checking dependencies..."

if ! command_exists java; then
    echo "❌ Java is required but not installed."
    exit 1
fi

if ! command_exists mvn; then
    echo "❌ Maven is required but not installed."
    exit 1
fi

if ! command_exists npm; then
    echo "❌ NPM is required but not installed."
    exit 1
fi

echo "✅ All dependencies found!"

# Clean and build the project
echo "🔨 Building Teedy with violet theme..."
mvn clean -DskipTests install

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "✅ Build completed successfully!"

# Start the application
echo "🚀 Starting Teedy server..."
echo "📝 The application will be available at: http://localhost:8080"
echo "👤 Default admin credentials: admin/admin"
echo "🎨 New violet theme is now active!"
echo "⭐ Check out the new 'Custom Page' in the navigation menu!"
echo ""

cd docs-web
mvn jetty:run

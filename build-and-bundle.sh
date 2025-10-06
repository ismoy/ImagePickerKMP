echo "ImagePickerKMP - Automated Build & Bundle"
echo "========================================="
echo

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m' 
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

print_step() {
    echo -e "${BLUE}Step: $1${NC}"
}

print_success() {
    echo -e "${GREEN}Success: $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}Warning: $1${NC}"
}

print_error() {
    echo -e "${RED}Error: $1${NC}"
}

# Function to verify if command was successful
check_success() {
    if [ $? -eq 0 ]; then
        print_success "$1"
    else
        print_error "$1 failed"
        exit 1
    fi
}

# Step 1: Clean previous build
print_step "Cleaning previous build..."
./gradlew clean
check_success "Clean completed"

# Step 2: Complete build (includes automatic NPM bundle)
print_step "Building project + automatic NPM bundle..."
./gradlew build
check_success "Build + NPM Bundle completed"

# Step 3: Verify that packages were created
JS_PACKAGE_DIR="build/js/packages/ImagePickerKMP-library"
WASM_PACKAGE_DIR="build/js/packages/ImagePickerKMP-library-wasm-js"

echo
echo "Verifying generated packages..."

if [ -d "$JS_PACKAGE_DIR" ]; then
    print_success "JavaScript package found: $JS_PACKAGE_DIR"
    
    # Verify package.json
    if [ -f "$JS_PACKAGE_DIR/package.json" ]; then
        VERSION=$(grep '"version":' "$JS_PACKAGE_DIR/package.json" | sed 's/.*"version": "\(.*\)".*/\1/')
        print_success "NPM package version: $VERSION (automatic from Gradle)"
    fi
    
    # Verify main bundle
    if [ -f "$JS_PACKAGE_DIR/kotlin/ImagePickerKMP-library.js" ]; then
        SIZE=$(du -h "$JS_PACKAGE_DIR/kotlin/ImagePickerKMP-library.js" | cut -f1)
        print_success "JavaScript bundle: $SIZE"
    fi
else
    print_error "JavaScript package NOT found"
fi

if [ -d "$WASM_PACKAGE_DIR" ]; then
    print_success "WebAssembly package found: $WASM_PACKAGE_DIR"
else
    print_warning "WebAssembly package not found (optional)"
fi

# Step 4: Show installation instructions
echo
echo "BUILD COMPLETED!"
echo "================"
echo
echo "Install in your project:"
echo -e "${YELLOW}cd your-web-project${NC}"
echo -e "${YELLOW}npm install $(pwd)/$JS_PACKAGE_DIR${NC}"
echo
echo "Or use npm link:"
echo -e "${YELLOW}cd $JS_PACKAGE_DIR && npm link${NC}"
echo -e "${YELLOW}cd your-web-project && npm link imagepickerkmp${NC}"
echo
echo "Usage example:"
echo -e "${BLUE}import ImagePickerKMP from 'imagepickerkmp';${NC}"
echo -e "${BLUE}ImagePickerKMP.ImagePickerLauncher(onSuccess, onError, onCancel);${NC}"
echo
echo "Available test files:"
echo "  - debug-permisos-imagepicker.html (complete diagnostics)"
echo "  - $JS_PACKAGE_DIR/test-zero-config.html (basic test)"
echo
echo "Your ImagePickerKMP is ready to use!"

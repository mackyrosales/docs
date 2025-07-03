# 🔮 Teedy Violet Theme

This document describes the violet theme implementation for the Teedy document management system.

## 🎨 Theme Overview

The violet theme transforms the default Teedy interface with:
- **Primary Color**: Violet (#6f42c1) 
- **Secondary Colors**: Various shades of violet for hover states and accents
- **Background**: Clean white (#ffffff) for optimal readability
- **Text**: Dark gray (#2d3748) for excellent contrast and readability

## ✨ Features Added

### 🎨 Visual Improvements
- Beautiful violet and white color scheme
- Modern rounded corners and improved shadows
- Better contrast for enhanced readability
- Consistent violet theming across all components

### 📄 New Custom Page
- Added a new "Custom Page" in the navigation menu
- Features a stunning gradient background
- Responsive card layout showcasing theme features
- Interactive buttons with violet styling

### 🔧 Technical Changes

#### Frontend Changes
- Updated `main.less` with comprehensive violet theme
- Added new route for custom page (`/custompage`)
- Created `CustomPage.js` controller
- Created `custompage.html` template
- Updated navigation in `index.html`

#### Backend Changes
- Modified `ThemeResource.java` to use violet as default color
- Enhanced CSS generation for consistent theming

## 🚀 Getting Started

### Prerequisites
- Java 11+
- Maven 3+
- NPM
- Tesseract 4 (for OCR functionality)

### Running the Application

1. **Quick Start**:
   ```bash
   ./run.sh
   ```

2. **Manual Build**:
   ```bash
   mvn clean -DskipTests install
   cd docs-web
   mvn jetty:run
   ```

3. **Access the Application**:
   - URL: http://localhost:8080
   - Default Admin: `admin` / `admin`

### Using Docker
```bash
docker-compose up
```

## 🎯 Navigation

The application now includes these main sections:
- **📚 Documents**: Manage your documents
- **🏷️ Tags**: Organize with tags
- **👥 Users & Groups**: User management
- **⭐ Custom Page**: New violet-themed showcase page (NEW!)
- **⚙️ Settings**: System configuration

## 🛠️ Customization

### Changing the Violet Color
To modify the primary violet color:

1. **CSS/LESS**: Update the color variables in `main.less`
2. **Backend**: Modify the default color in `ThemeResource.java`
3. **Theme Settings**: Use the admin interface at Settings > Configuration > Theme

### Adding More Custom Pages
1. Create a new controller in `app/docs/controller/`
2. Add a new template in `partial/docs/`
3. Register the route in `app.js`
4. Add navigation link in `index.html`

## 📱 Responsive Design

The violet theme is fully responsive and works beautifully on:
- 🖥️ Desktop computers
- 📱 Tablets
- 📱 Mobile phones

## 🎨 Color Palette

| Element | Color | Hex Code |
|---------|-------|----------|
| Primary Violet | Violet | `#6f42c1` |
| Hover Violet | Dark Violet | `#5a339a` |
| Active Violet | Darker Violet | `#4c2a85` |
| Background | White | `#ffffff` |
| Text | Dark Gray | `#2d3748` |
| Light Background | Light Gray | `#f8f9fa` |
| Border | Light Border | `#e9ecef` |

## 🔗 Key Files Modified

### Frontend
- `docs-web/src/main/webapp/src/style/main.less` - Main styling
- `docs-web/src/main/webapp/src/app/docs/app.js` - Routing
- `docs-web/src/main/webapp/src/app/docs/controller/CustomPage.js` - Controller
- `docs-web/src/main/webapp/src/partial/docs/custompage.html` - Template
- `docs-web/src/main/webapp/src/index.html` - Navigation

### Backend
- `docs-web/src/main/java/com/sismics/docs/rest/resource/ThemeResource.java` - Theme API

## 🎉 What's New

✅ Beautiful violet and white theme
✅ Enhanced readability with better contrast
✅ Modern UI components with rounded corners
✅ New custom page with gradient background
✅ Responsive design for all devices
✅ Consistent theming across all components

## 📞 Support

For questions or issues with the violet theme:
1. Check the original Teedy documentation
2. Review the theme-specific changes in this README
3. Examine the modified files listed above

---

**Enjoy your new violet-themed Teedy experience! 🔮✨** 
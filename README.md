# Insurance App - Authentication Module

A comprehensive Angular frontend application for an insurance system's authentication module, featuring user registration, login, password management, and role-based dashboards.

## Features

### 🔐 Authentication
- **User Registration**: Support for both Customer and Admin registration with KYC (PAN verification)
- **Login System**: Separate login flows for Customers and Admins
- **Password Management**: 
  - Change password functionality
  - Forgot password with PAN verification
- **JWT Token Management**: Secure authentication with JWT tokens

### 👥 Role-Based Access
- **Customer Dashboard**: Profile management and quick actions
- **Admin Dashboard**: User management, approval workflows, and administrative functions
- **Route Guards**: Protected routes based on user roles

### 🎨 User Interface
- **Modern Design**: Beautiful, responsive UI using Bootstrap 5
- **Gradient Backgrounds**: Eye-catching visual design
- **Form Validation**: Comprehensive client-side validation
- **Loading States**: User-friendly loading indicators
- **Error Handling**: Proper error messages and user feedback

## Technology Stack

- **Frontend Framework**: Angular 20
- **UI Framework**: Bootstrap 5.3
- **Icons**: Bootstrap Icons
- **HTTP Client**: Angular HttpClient
- **Routing**: Angular Router with Guards
- **State Management**: RxJS BehaviorSubject
- **Form Handling**: Angular Reactive Forms

## Project Structure

```
src/
├── app/
│   ├── components/
│   │   ├── login/                 # Login component
│   │   ├── register/              # Registration component
│   │   ├── forgot-password/       # Password reset component
│   │   ├── change-password/       # Change password component
│   │   ├── admin-dashboard/       # Admin dashboard
│   │   └── customer-dashboard/    # Customer dashboard
│   ├── models/
│   │   └── user.model.ts          # User interfaces and enums
│   ├── services/
│   │   └── auth.service.ts        # Authentication service
│   ├── guards/
│   │   └── auth.guard.ts          # Route guards
│   ├── app.routes.ts              # Application routing
│   ├── app.config.ts              # App configuration
│   └── app.ts                     # Main app component
├── environments/
│   ├── environment.ts             # Development environment
│   └── environment.prod.ts        # Production environment
└── styles.css                     # Global styles
```

## Installation & Setup

### Prerequisites
- Node.js (v18 or higher)
- npm or yarn
- Angular CLI

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd insurance_app
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Configure environment**
   - Update `src/environments/environment.ts` with your backend API URL
   - Default: `http://localhost:8080`

4. **Start development server**
   ```bash
   npm start
   ```

5. **Open application**
   - Navigate to `http://localhost:4200`
   - The application will automatically redirect to the login page

## Usage Guide

### For Customers

1. **Registration**
   - Navigate to `/register`
   - Select "Customer" role
   - Fill in personal details including PAN number
   - Submit registration form

2. **Login**
   - Navigate to `/login`
   - Select "Customer" role
   - Enter email and password
   - Access customer dashboard

3. **Dashboard Features**
   - View profile information
   - Access quick actions (Policies, Claims, Payments, Settings)
   - View recent activity

### For Admins

1. **Registration**
   - Navigate to `/register`
   - Select "Admin" role
   - Fill in administrative details
   - Submit registration (requires approval from existing admin)

2. **Login**
   - Navigate to `/login`
   - Select "Admin" role
   - Enter email and password
   - Access admin dashboard

3. **Dashboard Features**
   - View admin profile
   - Manage pending admin approvals
   - Manage pending customer approvals
   - Activate/deactivate users

### Password Management

1. **Change Password**
   - Navigate to `/change-password`
   - Enter current password and new password
   - Confirm new password

2. **Forgot Password**
   - Navigate to `/forgot-password`
   - Enter email and PAN number
   - Set new password

## API Integration

The application is designed to work with the provided Spring Boot backend. Key API endpoints:

- `POST /api/auth/register` - User registration
- `POST /api/auth/login/customer` - Customer login
- `POST /api/auth/login/admin` - Admin login
- `POST /api/auth/forgot-password` - Password reset
- `POST /api/auth/change-password` - Change password
- `GET /api/customer/profile` - Customer profile
- `GET /api/admin/profile` - Admin profile
- `GET /api/admin/pending-admins` - Pending admin approvals
- `GET /api/admin/pending-customers` - Pending customer approvals
- `POST /api/admin/activate-admin/{id}` - Activate admin
- `POST /api/admin/deactivate-user/{id}` - Deactivate user

## Security Features

- **JWT Token Authentication**: Secure token-based authentication
- **Route Guards**: Protected routes based on user roles
- **Form Validation**: Client-side validation for all forms
- **Password Security**: Secure password handling with validation
- **Session Management**: Automatic token storage and cleanup

## Styling & Design

- **Responsive Design**: Mobile-first approach with Bootstrap
- **Modern UI**: Clean, professional interface
- **Color Schemes**: 
  - Login: Blue gradient
  - Registration: Green gradient
  - Forgot Password: Pink gradient
  - Change Password: Blue gradient
- **Interactive Elements**: Hover effects and smooth transitions

## Development

### Building for Production
```bash
npm run build
```

### Running Tests
```bash
npm test
```

### Code Structure
- **Components**: Standalone Angular components
- **Services**: Injectable services for API communication
- **Guards**: Route protection based on authentication
- **Models**: TypeScript interfaces for type safety

## Troubleshooting

### Common Issues

1. **CORS Errors**
   - Ensure backend is configured to allow requests from `http://localhost:4200`
   - Check environment configuration

2. **Authentication Issues**
   - Verify JWT token format in backend
   - Check token expiration settings

3. **Styling Issues**
   - Ensure Bootstrap CSS is properly imported
   - Check for CSS conflicts

### Debug Mode
Enable debug logging by checking browser console for detailed error messages.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions, please contact the development team or create an issue in the repository.

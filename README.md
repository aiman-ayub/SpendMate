# SpendMate
SpendMate is an Android application designed to help users manage their personal finances efficiently. The app allows users to track income and expenses, set budgets, view analytics, and customize settings such as theme and currency.

## App Flow
- Splash Screen with app logo
- Login screen for existing users
- Signup screen for new users
- Dashboard screen after successful login

## Project Structure
- `app/src/main/java/com/example/spendsensei/` → All Kotlin source files.
- `app/src/main/res/` → App resources (layouts, drawables, etc.)
- `gradle/` → Gradle wrapper and build configurations.

## Core Features
### Authentication
- User Login
- User Signup
- Secure local user storage
- Logout option that closes the app
### Dashboard
- Displays:
  - Total Balance
  - Total Income
  - Total Expenses
- Quick access to:
  - Analytics
  - Budget
  - Recent Transactions
### Bottom Navigation
- Home (Dashboard)
- History
- Analytics
- Budget
- Add Transaction (center button)
### Add Transaction
- Add Expense
- Add Income
- Fields include:
  - Amount
  - Category
  - Date
  - Payment Method
  - Notes
### Budget Management
- Users can set a budget for categories
- Prevents adding further expenses if the budget limit is exceeded
- Budget must be updated to continue adding expenses
### Analytics
- Visual representation of expenses
- Pie chart view
- Category-wise expense breakdown
### History
- Displays all transactions
- Organized month-wise
- Includes both income and expenses
### Settings
- User Profile:
  - Username
  - Email
- App Version display
- Registered Users:
  - Shows names and emails of users registered on the same device
- Theme Selection:
  - Light Mode
  - Dark Mode
- Currency Settings:
  - Change currency across the entire app (INR, USD, EUR, etc.)
- Logout button

## Technology Stack
- Android (Kotlin)
- Jetpack Compose
- Room Database
- MVVM Architecture
- DataStore (for preferences)
- Material Design 3
  
## How to Run the App
1. Clone the repository:
   ```bash
   git clone https://github.com/aiman-ayub/SpendSense.git

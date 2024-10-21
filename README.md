# Food Ordering App

Welcome to the **Food Ordering App**! This project is designed to streamline the process of placing and managing food orders. The application consists of three main components:

- **Backend**: A robust Spring application handling business logic and database interactions.
- **Employee Frontend**: A JavaFX application for employees to manage orders efficiently.
- **Customer App**: An Android application that allows customers to place orders seamlessly.

## Features

- **Order Management**: Customers can create, view, and manage their orders directly from the Android app.
- **WebSocket Integration**: Real-time updates on order status (preparing, ready for pickup) via WebSockets.
- **Employee Dashboard**: JavaFX application for employees to view and update order statuses, ensuring smooth operations.
- **Order Tracking**: Customers receive notifications about their order status and estimated ready times.

## Technology Stack

- **Backend**: 
  - Spring Boot
  - Spring WebSocket
  - Supabase (for database and generated Private Token)
  
- **Frontend**: 
  - JavaFX (for the employee interface)
  - Android (for the customer application)

## Getting Started

### Prerequisites

- Java 23 or higher
- Maven
- Gradle
- Android Studio (for the customer app)
- Supabase Project

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/food-ordering-app.git
   cd food-ordering-app
2. **Set up the Backend**:
   - Navigate to the `backend` directory:
     ```bash
     cd backend
     ```
   - Configure your database connection in `.env` with .example.env. Make sure to set the correct database URL, username, and password.
   - Build and run the Spring Boot application:
     ```bash
     ./gradlew bootRun
     ```

3. **Set up the Employee Frontend**:
   - Navigate to the `employee-frontend` directory:
     ```bash
     cd gui/test
     ```
   - Ensure you have the necessary JavaFX libraries added to your project.
   - Build and run the JavaFX application. You can do this directly from your IDE or use a build tool if configured.

4. **Set up the Customer App**:
   - Open the `customer-app` directory in Android Studio.
   - Sync the project with Gradle files to ensure all dependencies are correctly set up.
   - Run the application on an Android device or emulator to start placing orders.

## Usage

- **For Customers**:
  - Open the Android app, and start placing orders.
  - Receive real-time updates on your order status on a board,

- **For Employees**:
  - Use the JavaFX application to manage incoming orders and update their statuses.

## Contributing

Contributions are welcome! If you’d like to contribute to this project, please follow these steps:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/YourFeature`).
3. Make your changes and commit them (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature/YourFeature`).
5. Open a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Inspired by modern food delivery applications.
- Thanks to the Spring and JavaFX communities for their support!

---

Feel free to reach out if you have any questions or need assistance!
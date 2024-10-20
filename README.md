Got it! Here's an updated README for your app:

# MindWell: Mental Health Test Application

## Overview
MindWell is an Android application designed to help users evaluate their mental health by answering a set of questions, after which they receive a score along with recommendations. The app also assists users in finding nearby psychiatrists by redirecting them to Google Maps with a pre-filled search query for nearby psychiatrists based on their location.

## Features
- **Mental Health Test**: Users answer a set of questions to assess their mental health, with the questions retrieved from the MindWell API.
- **Score & Recommendations**: After completing the test, users receive a score and personalized recommendations based on their results.
- **Nearby Psychiatrist Suggestions**: The app takes the user's location coordinates, displays a toast message with the coordinates, and redirects the user to Google Maps with a search query for nearby psychiatrists.
- **Survey Score History**: Users can view their previous scores and recommendations, with data stored securely in Firebase.

## API
The mental health questions are fetched from the following API endpoint:
- **Questions API**: [https://mindwell-api.vercel.app/api/questions](https://mindwell-api.vercel.app/api/questions)  
This API is built using Node.js and provides a set of questions for the mental health evaluation.

## Technologies Used
- **Android**: Developed for Android using Java/Kotlin.
- **Node.js API**: The questions for the mental health survey are provided by a custom-built Node.js API.
- **Firebase**: Utilized for user authentication and storing user data, including survey score history.
- **Google Maps Redirection**: Users are redirected to Google Maps with a search query to find nearby psychiatrists based on their location.

## Installation & Setup
1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-repo/mindwell-app.git
   ```
2. **Open the project** in Android Studio.

3. **Configure Firebase**:
   - Create a project in Firebase and add your Android app.
   - Download the `google-services.json` file from Firebase Console and place it in your project’s `app` directory.
   - Enable Firebase Authentication and Firestore Database in the Firebase console.

4. **Run the app**: Once all dependencies and configurations are set, run the application on your Android device or emulator.

## Usage
1. **Sign Up/Sign In**: Users must create an account or sign in using Firebase Authentication.
2. **Take the Test**: Once logged in, users can start the mental health test by answering questions retrieved from the API.
3. **View Results**: After completing the test, users receive a score with tailored recommendations.
4. **Find Nearby Psychiatrists**: The app retrieves the user’s location coordinates, shows a toast with the coordinates, and redirects the user to Google Maps with a pre-filled search query for nearby psychiatrists.
5. **View Score History**: Users can track their mental health progress by viewing past survey scores stored in Firebase.

## API Reference
The MindWell API provides the list of questions for the mental health survey. The API endpoint is:

- **Get Questions**: `GET /api/questions`
  - **Response**: A JSON object containing a list of mental health-related questions.

Example Response:
```json
[
  {
    "id": 1,
    "question": "How often do you feel overwhelmed?"
  },
  {
    "id": 2,
    "question": "Do you experience trouble sleeping?"
  }
  // more questions
]
```

## Firebase Structure
The following data is stored in Firebase Firestore:
- **User Data**: Stores user profile information (name, email, etc.).
- **Survey Scores**: Keeps a record of the user’s past survey scores, including the date and recommendations provided.

## Contact
For any questions or issues, don't hesitate to get in touch with us at mindwellSIH1@gmail.com.

---

MindWell – Your mental health companion, helping you stay on track!

# JMeter with Java Code for Rate Limit Testing

## Overview

This project is designed to offer a code-centric alternative to JMeter's UI for performance testing. Specifically, it focuses on conducting rate limit tests on APIs. The project is built using Java, and its main class is `RateLimitTest.java`, located in `app/src/main/java/jmeter/rate/limit/api/test`.

---

## 🎯 Purpose

The primary goal is to simulate multiple users sending requests to a targeted API and gather performance metrics. Users can customize test parameters such as:

- Number of Users
- Ramp-up Time
- Requests per Second
- Test Duration

---

## 🛠️ Prerequisites

- Java Development Kit (JDK)
- Gradle build tool

---

## 🚀 Getting Started

### Clone the Repository

`git clone git@github.com:jimmymaise/jmeter_with_javacode.git`

### Navigate to Project Directory

`cd jmeter_with_javacode`

### Build the Project

`gradle build`

### Run the Tests

`gradle run`

**Note**: Test results will be stored in the directory specified by the `RESULT_FOLDER_PATH` in `RateLimitTest.java`.

---

## 📝 Configuration

Customize the test parameters by modifying the following constants in `RateLimitTest.java`:

- `DOMAIN_NAME`: API's domain name
- `API_PATH`: API endpoint
- `NUMBER_OF_USERS`: Number of users to simulate
- `RAMP_UP_TIME`: Time in seconds for all threads to start
- `NUMBER_OF_REQUEST_PER_SECOND`: Rate of requests per second
- `DURATION_SECONDS`: Total test duration in seconds

---

## 📊 Test Results

The results will be stored in JTL format and an HTML dashboard will be generated. Both can be found in the directory specified by the `RESULT_FOLDER_PATH` variable.

---

## 📜 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

---

## 🤝 Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

---

## 📧 Contact

For any questions or concerns, please open an issue or submit a pull request.

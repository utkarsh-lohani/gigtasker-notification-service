# 📨 GigTasker Notification Service

This service is the **"Mailroom"** of the GigTasker microservice platform. It is a "headless" microservice, meaning it has **no API endpoints** or controllers.

Its *only* job is to be an asynchronous, event-driven consumer. It connects to RabbitMQ, listens for specific events published by other services (like `task-service` or `bid-service`), and performs an action, such as (in the future) sending an email, push notification, or SMS.

---

## ✨ Core Responsibility

The primary purpose of this service is to **decouple** your application's logic.

For example, when a user posts a task, the `task-service`'s job is *only* to save the task to the database. It shouldn't have to also know how to:
1.  Connect to an email server (like SendGrid).
2.  Connect to an SMS provider (like Twilio).
3.  Handle email template logic.
4.  Retry if the email server is down.

Instead, the `task-service` just publishes a simple `task.created` event to RabbitMQ and is *done*.

This `notification-service` is the one that subscribes to that event and takes on the (potentially slow) responsibility of sending the notification, all without blocking the user or the `task-service`.

---

## 🛠️ Tech Stack

* **Spring Boot 3**
* **Java 25**
* **Spring AMQP (RabbitMQ):** This is the core of the service. It's used for listening to queues.
* **Spring Cloud Config Client:** For getting its configuration (like RabbitMQ credentials) from the `config-server`.
* **Spring Cloud Netflix Eureka Client:** For registering itself with the `service-registry` so we can see that it's "ALIVE" on the dashboard.

---

## 🎧 Event-Driven Architecture

This service is a perfect example of an "Event Consumer." It uses `@RabbitListener` to subscribe to queues, which are bound to exchanges.

It is configured to listen for the following events:

| Exchange | Routing Key | Bound Queue | Purpose (What it does) |
| :--- | :--- | :--- | :--- |
| `task-exchange` | `task.created` | `notification.queue` | "A new task was posted. (Future: send confirmation email.)" |
| `bid-exchange` | `bid.placed` | `bid.notification.queue` | "A new bid was placed. (Future: notify task owner.)" |
| `bid-exchange` | `bid.accepted` | `bid.accepted.notification.queue` | "A bid was accepted. (Future: notify the *winner*.)" |
| `bid-exchange` | `bid.rejected` | `bid.rejected.notification.queue` | "A bid was rejected. (Future: notify the *loser*.)" |

### JSON Message Converter
This service uses a `Jackson2JsonMessageConverter` bean. This is a critical design choice.

It means this service *does not* depend on the Java class paths of the other services. It simply receives a JSON message and deserializes it into its *own* local `TaskDTO` or `BidDTO` objects. This ensures true decoupling—the `task-service` can be refactored, and as long as it sends valid JSON, this service will never break.

---

## 🚀 How to Run

1.  **Start Dependencies (CRITICAL):**
    * Run `docker-compose up -d` (for Postgres, and most importantly, **RabbitMQ**).
    * Start the `config-server`.
    * Start the `service-registry`.

2.  **Run this Service:**
    This service can be started any time after the `config-server` and `service-registry` are running.
    ```bash
    # From your IDE, run NotificationServiceApplication.java
    # Or, from the command line:
    java -jar target/notification-service-0.0.1.jar
    ```

This service will start on a **random port** (as defined by `server.port: 0`), connect to RabbitMQ, create its queues and bindings, and register itself with Eureka. You can then watch its console log to see events appear in real-time as you use the application.
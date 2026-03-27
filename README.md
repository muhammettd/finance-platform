# 🚀 Finance Platform - Real-Time Crypto Trading Engine

A full-stack, real-time cryptocurrency trading platform and matching engine built with a microservices-inspired architecture. This platform handles order matching, double-entry wallet settlements, and real-time market data broadcasting (Order Book & Trade History).

## ✨ Key Features

* **Real-Time Order Book:** Live Bids and Asks updated instantly using Redis and WebSockets.
* **Trade Matching Engine:** Efficiently matches BUY and SELL orders (Maker/Taker logic).
* **Double-Entry Wallet Settlement:** Secure transaction processing ensuring funds are properly locked and deducted during trades.
* **Live Market History:** Real-time broadcasting of executed trades using STOMP WebSockets.
* **Event-Driven Architecture:** Utilizes Apache Kafka for asynchronous and reliable trade settlement.
* **Monorepo Structure:** Both Frontend and Backend codebases are managed in a single repository for streamlined development.

## 🛠️ Tech Stack

**Frontend (Client)**
* **React (Vite):** Fast, modern UI development.
* **Redux Toolkit:** State management for real-time order lists and trade history.
* **Tailwind CSS:** Highly customizable, utility-first styling for a sleek dark-mode trading interface.
* **Custom Hooks:** Clean architecture for WebSocket (`useWebSocket`) management.

**Backend (Server)**
* **Java & Spring Boot:** Robust and scalable core REST API and business logic.
* **Spring Data JPA (Hibernate):** Object-Relational Mapping for database interactions.
* **WebSockets (STOMP):** Bi-directional communication to push market updates to the client without page reloads.

**Infrastructure & Data**
* **PostgreSQL:** Relational database for persistent storage of Wallets, Users, and Order/Trade History (ACID compliant).
* **Redis:** In-memory data structure store used for ultra-fast Order Book (Bids/Asks) retrieval.
* **Apache Kafka:** Distributed event streaming platform used to decouple the matching engine from the wallet settlement process.

## 🏗️ Architecture Flow

1.  **Order Placement:** User submits an order via React -> REST API.
2.  **Wallet Lock:** System locks the required funds (Base or Quote asset) in PostgreSQL.
3.  **Order Book (Redis):** Open orders are stored and sorted in Redis for instant retrieval.
4.  **Matching & Kafka:** When a match is found, a `TradeEvent` is published to a Kafka topic.
5.  **Settlement:** The `TradeSettlementService` consumes the Kafka message, executes the double-entry bookkeeping (updating balances in PostgreSQL), and finalizes the order.
6.  **Real-Time Broadcast:** The completed trade is pushed via WebSockets to all connected React clients, instantly updating the UI.

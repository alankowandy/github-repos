# GitHub Repos API Client

A Spring Boot application for interacting with the GitHub API to retrieve:
- Public repositories of a given user
- Branches within a specific repository

## Features
- Retrieve a list of repositories for a GitHub user
- Retrieve branches for a specific repository
- JSON mapping using Java `record` types
- Custom error handling for non-existent users or repositories
- Integration and unit testing using JUnit and Mockito

## Technology Stack
- Java 21
- Spring Boot 3.5.4
- RestTemplate (HTTP client)
- JUnit 5 & Mockito (testing)
- Maven

# AGENT.md

## Project Overview

AchoBeta Refine Jade Polishing System is a backend project for an AI learning assistant aimed at students. By uploading incorrect questions, the system can automatically recognize the content of the questions, analyze the reasons for errors, explain the knowledge points, and generate personalized exercises to help students quickly identify and fill gaps in their knowledge.

## Project Structure

### Root Directory
- **build.sh**: Build script.
- **LICENSE**: Project license.
- **pom.xml**: Maven project configuration file.
- **README.md**: Project introduction.
- **data/**: Stores log files.
- **docs/**: Documentation directory, including project and contribution guides.
- **draw/**: Stores design-related images.

### Module Directory

#### refine-api
- **Description**: Provides core API functionality.
- **Main Files**:
  - `src/main/java/`: Java source code.
  - `target/`: Compiled files.

#### refine-app
- **Description**: Application module containing the main business logic.
- **Main Files**:
  - `src/main/java/`: Java source code.
  - `src/main/resources/`: Configuration files (e.g., `application.yml`).
  - `target/`: Compiled files.

#### refine-domain
- **Description**: Domain layer module defining core business logic.
- **Main Files**:
  - `src/main/java/`: Java source code.
  - `target/`: Compiled files.

#### refine-infrastructure
- **Description**: Infrastructure layer module handling interactions with external systems.
- **Main Files**:
  - `src/main/java/`: Java source code.
  - `target/`: Compiled files.

#### refine-trigger
- **Description**: Trigger module handling event-driven functionality.
- **Main Files**:
  - `src/main/java/`: Java source code.
  - `target/`: Compiled files.

#### refine-types
- **Description**: Type definition module containing common types used in the project.
- **Main Files**:
  - `src/main/`: Java source code.
  - `target/`: Compiled files.

## Review Focus

1. **Code Style**:
   - Whether a unified code style is followed.
   - Whether there is redundant code.

2. **Module Division**:
   - Whether it complies with Domain-Driven Design (DDD) principles and whether module responsibilities are single.
   - Whether dependencies between modules are clear and whether there are circular dependencies.
   - Whether the Dependency Inversion Principle is followed to ensure high-level modules do not depend on the implementation of low-level modules.

3. **Log Management**:
   - Whether logs are stored by category (e.g., info, error).
   - Whether there is sensitive information leakage.

4. **Dependency Management**:
   - Whether there are unnecessary dependencies.
   - Whether there are version conflicts.

5. **Security**:
   - Whether there are potential security vulnerabilities (e.g., SQL injection, XSS attacks).
   - Whether sensitive data is encrypted.
   - Whether there is an access control mechanism to prevent unauthorized access.

6. **Performance Review**:
   - Whether there are performance bottlenecks (e.g., unnecessary loops, complex algorithms).
   - Whether database queries are optimized (e.g., indexes, pagination).
   - Whether there is a caching mechanism to improve response speed.

7. **Exception Handling**:
   - Whether possible exception scenarios are comprehensively handled.
   - Whether there is a global exception handling mechanism to prevent program crashes.
   - Whether exception information is recorded in logs for troubleshooting.

8. **Event-Driven Design**:
   - Whether event-driven architecture is reasonably used to avoid over-complication.
   - Whether event publishing and subscription are decoupled and whether there is a reliable message delivery mechanism.

9. **Design Patterns**:
   - Whether design patterns (e.g., Singleton, Factory, Strategy) are reasonably used.
   - Whether there is over-design, adding unnecessary complexity.
   - Whether SOLID principles are followed to ensure code maintainability and extensibility.

## Suggestions

- Regularly update documentation to ensure synchronization with the code.
- Optimize module dependencies to reduce coupling.

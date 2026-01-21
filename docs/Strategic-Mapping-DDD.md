# Strategic Mapping - DDD

This document details the strategic level of the architecture, identifying the Bounded Contexts and ensuring the separation of concerns according to the
Domain Driven Development Pattern.

## Architectural Layers

This diagram represents the internal structure of each context, following the principles of Clean Architecture and DDD Tactical Design.

```mermaid
flowchart TD
%% Define Layers
    subgraph API_Layer ["API Layer"]
        direction TB
        L1[REST Controllers]
        L2[DTOs & Mappers]
        L3[Exception Handlers]
    end

    subgraph App_Layer ["Application Layer"]
        direction TB
        A1[Use Cases / Services]
        A2[Event Listeners]
        A3[Application Ports]
    end

    subgraph Domain_Layer ["Domain Layer (Core)"]
        direction TB
        D1[Entities & Aggregates]
        D2[Value Objects]
        D3[Domain Services]
        D4[Repository Interfaces]
    end

    subgraph Infra_Layer ["Infrastructure Layer"]
        direction TB
        I1[Database Repositories]
        I2[MinIO Storage Adapters]
        I3[External API Adapters]
    end

%% Force Strict Vertical Stacking with thick arrows
    API_Layer ==> App_Layer
    App_Layer ==> Domain_Layer
    Domain_Layer ==> Infra_Layer
```

## Context Mapping

The diagram below illustrates the division of the project into isolated domain boundaries.

```mermaid
flowchart LR
    subgraph Support_Domain ["Support&nbsp;Domain:&nbsp;Administrative"]
        direction TB
        B([Administrative Context])
        
        subgraph Administrative_Internals ["Internals"]
            direction TB
            B1[Civil Police Synchronization]
            B2[Regional Catalog]
        end
        
        B --- Administrative_Internals
    end

    subgraph Core_Domain ["Core&nbsp;Domain:&nbsp;Discography"]
        direction TB
        A([Discography Context])
        
        subgraph Discography_Internals ["Internals"]
            direction TB
            A1[Artist Management]
            A2[Album Management]
            A3[Real-time Notifications]
        end
        
        A --- Discography_Internals
    end
```

## Tatical Modeling 

Tactical modeling defines how business objects behave and relate to each other. The diagram bellow show Aggregates and Value Objects for both contexts.

```mermaid
classDiagram
    %% Core Domain: Discography
    namespace Discography_Domain {
        class Artist {
            <<Aggregate Root>>
            +UUID id
            +String name
            +String genre
            +ImageReference profileImage
            +record(name, genre, profileImage)
            +update(name, genre)
            +delete()
            +uploadImage(image)
            +updateImage(image)
            +removeImage()
        }
        class Album {
            <<Aggregate Root>>
            +UUID id
            +String title
            +LocalDate releaseDate
            +ImageReference cover
            +Set~Artist~ artists
            +record(title, releaseDate, cover, artists)
            +update(title, releaseDate)
            +delete()
            +uploadImage(image)
            +updateImage(image)
            +removeImage()
            +addArtist(artist)
            +removeArtist(artist)
        }
        class ImageReference {
            <<Value Object>>
            +String bucket
            +String objectKey
            +String contentType
            +getPublicUrl() String
        }
    }

    %% Support Domain: Administrative
    namespace Administrative_Domain {
        class Regional {
            <<Aggregate Root>>
            +Long externalId
            +String name
            +String city
            +Boolean active
            +syncData(name, city)
            +deactivate()
            +remove()
        }
    }

    %% N:N Relationship
    Artist "*" -- "*" Album : collaborations
    Artist ..> ImageReference : uses
    Album ..> ImageReference : uses
```

## Details of Layer Relationships


* **API → Application**: Controllers receive HTTP requests, convert them to DTOs, and call the application layer methods (Use Cases). The API layer never accesses the database directly.
* **Application → Domain**: The application layer orchestrates the data flow. It uses domain entities and services to execute complex business rules. It also interacts with repository interfaces (Ports) defined in the domain.
* **Infrastructure → Domain (Dependency Inversion)**: The domain defines an interface (e.g., ArtistRepository). Infrastructure implements this interface using specific technologies (JPA, JDBC). This allows the database to be swapped without changing a single line of business logic.
* **Infrastructure → Application**: Infrastructure provides implementations for support services defined by the application, such as file storage (MinIO) or calls to external Civil Police APIs.

### Strategic Detailing

#### Bounded Contexts

* **Discography Context (Core Domain)**:
  * **Focus**: Where the business value resides for the end user. Manages the complexity of the $N:N$ relationship between artists and albums.
  * **Ubiquitous Language**: Artist, Album, Album Cover, Profile Image.


* **Administrative Context (Support Domain)**:
  * **Focus**: Provides auxiliary data required by the system (Police Regionals).
  * **Ubiquitous Language**: Regional, Synchronization, External ID, Active/Inactive Status.


#### Integration and Isolation Strategy
   * **Domain Isolation**: Contexts are independent, ensuring that changes in Civil Police synchronization rules do not directly affect the core of discography management.
   * **Anti-Corruption Layer (ACL)**: ImageReference acts as an infrastructure pattern that protects the domain from technical details of MinIO, and the PoliceApiAdapter protects the administrative domain from changes in the Civil Police API.
   * **Domain Events**: Communication between persistence in the Core Domain and the user interface is mediated by events, ensuring the domain does not directly depend on WebSocket technology.
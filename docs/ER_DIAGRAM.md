# Jeevadaana — Database Design & ER Diagram

## Entities

| Entity | Description |
|--------|-------------|
| **Donor** | A person who registers to donate blood. |
| **Organizer** | An organization/NGO/hospital that organizes donation camps. |
| **Camp** | A blood donation camp organized by an Organizer in a district. |
| **CampRegistration** | A donor signing up for a particular camp (many-to-many link). |
| **Donation** | A blood donation record created during post-camp management. |

## Relationships

- **Organizer (1) — (N) Camp** : one organizer organizes many camps.
- **Camp (1) — (N) CampRegistration (N) — (1) Donor** : a donor registers for many camps and a camp has many registered donors (resolved via the `camp_registrations` junction table, unique on `(camp_id, donor_id)`).
- **Donor (1) — (N) Donation** : a donor has a donation history.
- **Camp (1) — (N) Donation** : donations are collected at a camp.

## ER Diagram

```mermaid
erDiagram
    ORGANIZER ||--o{ CAMP : organizes
    CAMP ||--o{ CAMP_REGISTRATION : has
    DONOR ||--o{ CAMP_REGISTRATION : makes
    DONOR ||--o{ DONATION : gives
    CAMP ||--o{ DONATION : collects

    DONOR {
        bigint id PK
        varchar name
        varchar email UK
        varchar password
        varchar phone
        varchar blood_group
        varchar gender
        int age
        varchar district
        varchar address
        datetime created_at
    }

    ORGANIZER {
        bigint id PK
        varchar organization_name
        varchar contact_person
        varchar email UK
        varchar password
        varchar phone
        varchar district
        varchar address
        datetime created_at
    }

    CAMP {
        bigint id PK
        varchar name
        bigint organizer_id FK
        varchar district
        varchar venue
        date camp_date
        time start_time
        time end_time
        int capacity
        varchar description
        varchar status
        datetime created_at
    }

    CAMP_REGISTRATION {
        bigint id PK
        bigint camp_id FK
        bigint donor_id FK
        varchar status
        datetime registered_at
    }

    DONATION {
        bigint id PK
        bigint donor_id FK
        bigint camp_id FK
        varchar blood_group
        int units_ml
        date donation_date
        varchar remarks
        datetime created_at
    }
```

## Enumerations

- **BloodGroup**: `A+, A-, B+, B-, AB+, AB-, O+, O-`
- **Gender**: `MALE, FEMALE, OTHER`
- **CampStatus**: `UPCOMING, COMPLETED, CANCELLED`
- **RegistrationStatus**: `REGISTERED, ATTENDED, CANCELLED`

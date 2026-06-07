---
name: testing-jeevadaana
description: Test the Jeevadaana blood donation app end-to-end (donor/organizer flows, camps, post-camp stats, CRUD). Use when verifying UI or REST changes against MySQL.
---

# Testing Jeevadaana

Spring Boot 3 + Thymeleaf + REST + MySQL 8. Server runs on `localhost:8080`.

## Run the app against MySQL
- DB: MySQL 8, db `jeevadaana`, user `jeevadaana` / password `jeevadaana` (local dev only).
- Build + run: `mvn clean package -q && java -jar target/*.jar` (or `mvn spring-boot:run`).
- If port 8080 is taken by a stale jar from a previous session: `pkill -f jeevadaana` (or `pkill -f '\.jar'`), then restart.
- For a clean run, reset/reseed the DB first (drop+recreate from `db/schema.sql`); the app auto-seeds demo data on boot via `DataSeeder`.
- Verify health quickly: `curl -s localhost:8080/api/camps` should return JSON.

## Seeded logins (after fresh seed)
- Organizer: `organizer@jeevadaana.org` / `password` (org `Red Cross Bengaluru`, ID `ORG-000001`).
- Donor: `donor@jeevadaana.org` / `password`.
- Newly registered donors get auto IDs like `DNR-000002` (format `DNR-%06d` from numeric id); organizers get `ORG-%06d`.

## Key flows to exercise
1. **Auto IDs**: register a new donor → success banner + dashboard badge show `DNR-xxxxxx`; verify `donors.registration_code` in MySQL.
2. **Guidelines**: `/guidelines` (donor instructions + eligibility) and `/organizer-info` render; both linked in navbar.
3. **Camp detail (REST)**: `/camps/{id}` is rendered from `GET /api/camps/{id}` (date/time/venue/district/organizer contact/registration count).
4. **Camp registration**: donor registers from dashboard/camps → `My Registrations` increments and camp detail count goes up; row in `camp_registrations`.
5. **Post-camp stats**: organizer Manage page → record a donation (units + remarks) → stats card updates (total donors, units, blood-group-wise table). Cross-check `GET /api/camps/{id}/stats` and `donations` table.
6. **FK-aware delete**: Delete on Manage page is blocked if the camp has donations (error flash "Cannot delete a camp that already has recorded donations."); succeeds for camps with no donations.

## Verify persistence (no mock data)
Always cross-check the UI against MySQL and/or REST:
```
mysql -h127.0.0.1 -ujeevadaana -pjeevadaana jeevadaana -e "SELECT ..."
curl -s localhost:8080/api/camps/1/stats
```

## Gotchas
- **Delete uses a native `confirm()` dialog.** A single click won't POST — the dialog must be accepted. With computer-use, click the Delete button then press `Return` to confirm (the page navigates back to the dashboard with a flash on success). If a click seems to do nothing, a native confirm/cancel dialog is the likely cause (same for Mark Completed / Cancel Camp).
- Browser may hold a **stale logged-in session** from a prior session before a DB reset — log out and start from `/` to get current state.
- `mvn` tests run against H2; runtime testing must use MySQL to prove real persistence.

## Devin Secrets Needed
- None. Local MySQL credentials are dev-only (`jeevadaana`/`jeevadaana`) and seeded logins use `password`.

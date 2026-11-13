# Sprint 2 checklist

## Required functionality

- [x] nginx webserver integrated
- [x] website with user-input validation
- [x] dashboard served by nginx
- [x] document detail page served by nginx
- [x] website communicates with the REST API
- [x] frontend runs in an additional Docker Compose container
- [x] `GET http://localhost/` returns the Paperless frontend

## Verification before submission

```bash
./mvnw clean verify
docker compose build
docker compose up -d
curl -f http://localhost/
curl -f http://localhost/api/documents
docker compose down
```

Manual browser check:

1. Create a category.
2. Try to submit an empty document form and verify that validation messages appear.
3. Create a document with a title, file name and content type.
4. Verify that the dashboard counters and list update.
5. Open the document detail page.

## Team workflow

- [ ] one assigned issue per team member
- [ ] one feature branch and meaningful commit per team member
- [ ] one pull request to `develop` per team member
- [ ] all three pull requests merged to `develop`
- [ ] final pull request from `develop` to `main`
- [ ] final ZIP created from `main`

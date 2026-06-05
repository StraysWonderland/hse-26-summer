# Todo-app Postgres shared-volume demo

15-minute narrated demo that reuses the Spring Boot Todo app from
`StraysWonderland/hse-26-summer`. Shows what happens when a single app is
backed by two Postgres containers sharing one Docker volume.

## Files

- `compose_volumeconflict.yml` — one `app`, two postgres (`db1`, `db2`), one shared volume

## Demo arc

| Step | Beat |
|---|---|---|
| 1 | Show the broken compose: two DBs, same volume |
| 2 | Bring up `app` + `db1`, working baseline |
| 3 | Add real todos through the app, show data in db1 |
| 4 | Display `postmaster.pid` — the exclusivity contract |
| 5 | Start `db2` → it refuses, read the actual error |
| 6 | Force the conflict: stop db1, start db2, then both |
| 7 | Concurrent writes from app (via db1) and direct SQL (via db2) |
| 8 | Compare row counts, show divergence + log errors |
| 9 | Cold restart — WAL/checksum errors |

### TEST LOCALLY

1. Run the app and db1:

```bash
docker-compose -f compose_volumeconflict.yml -p todo_vol_demo up -d app db1
```

1. CHECK READYNESS OF DB

```bash
docker-compose -f compose_volumeconflict.yml -p todo_vol_demo exec db1 pg_isready -U todo
```

1. CHECK TODOS IN BROWSER and CREATE NEW TODOS

```bash
curl -X POST http://localhost:8080/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "My Todo", "description": "Todo description", "completed": "true"}'
```

```bash
curl -s http://localhost:8080/todos | jq .
```

```bash
docker-compose -f compose_volumeconflict.yml -p todo_vol_demo \
  exec db1 psql -U todo -d todos -c "SELECT id, description, completed FROM todo_item;"


docker-compose -f compose_volumeconflict.yml -p todo_vol_demo \
  exec db1 cat /var/lib/postgresql/data/postmaster.pid


docker exec todo_db2 psql -U todo -d todos -c "SELECT COUNT(*) FROM todo_item;"

```

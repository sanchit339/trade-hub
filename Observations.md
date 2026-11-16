# Observations

## Kafka outage (broker down while a file is dropped)

Three failures, exactly 60s apart — one per valid row (C-0001, C-0002, C-0007). Each row's Kafka send blocks for 60 seconds before giving up, and only after all rows are attempted does the file move.

### Why you didn't see the desired result
Two things conspire:

1. **The 60s block.** Kafka's client default `max.block.ms = 60000`. With the broker down, `producer.send()` waits the full 60s for topic metadata before throwing `TimeoutException`. During that whole window the file just sits in `data/inbox/` with a `.camelLock` file next to it — it looks like nothing is happening.

2. **Restarting Kafka too early.** If Kafka is restarted while a send is still blocked (within its 60s window), the send then succeeds, the row is accepted, no exception is thrown — and the file ends up in `.processed`, not `.error`.

### Observed timeline
| Time | Event |
|---|---|
| 19:30:14 | `Received file trades-outage.csv` |
| 19:31:14 | `Failed delivery ... at publish-trader ... TimeoutException: Topic trades.inbound not present in metadata after 60000 ms` |
| 19:32:14 | same failure (next row) |
| 19:33:14 | same failure (next row) |
| ~19:33:15 | file moved to `data/inbox/.error/` |

### Outcome
The design rule works, but only if Kafka stays down long enough for the sends to actually time out (~60s × number of valid rows):
```
data/inbox/.error/trades-outage.csv      <- landed here (as intended)
data/inbox/.processed/trades-small.csv   <- old file, untouched
```

### Making the check fast and reliable
Lower the producer's metadata timeout so a dead broker fails in seconds instead of a minute. In `PublishRoute.java`:
```java
.to("kafka:{{tradehub.topics.inbound}}?enableIdempotence=true&maxBlockMs=3000")
```
Then each row fails in ~3s and the file hits `.error` within a few seconds.

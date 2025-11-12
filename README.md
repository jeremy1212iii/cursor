2025-11-1202:40:04 Initialized README for tracking the ActiveMQ message retention inquiry.

Plan:
- Capture the specific question about automatic message deletion on `queue://game.update`.
- Summarize how ActiveMQ manages message lifecycle, including memory limits and TTL.
- Provide guidance on verifying broker configuration for message expiration policies.

2025-11-1202:40:20 Documented that ActiveMQ does not auto-delete messages unless a TTL or per-destination policy is configured, and recommended checking broker policies and message expirations.

2025-11-1202:43:12 Clarified that the broker's default destination policy retains messages indefinitely unless the producer sets a TTL or the broker applies an explicit expiration policy; only non-persistent in-memory buffers may be evicted when limits are exceeded.

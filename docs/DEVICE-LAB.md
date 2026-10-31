Shared measurements and Android adapter are maintained in the canonical Device Lab workspace.
SOURCE.json records vendored file hashes; the sync tool verifies all three standalone projects.
Raw means the value exposed by Android, before this app applies its display filter.
Event interval is not hardware or end-to-end latency. Vibration requires controller support.
Session reports are local JSON; there is no CRM integration.

Canonical upstream: [remteh-by/Device-Lab](https://github.com/remteh-by/Device-Lab). The adapter and source record are checked with `tools/sync-testers.py --check` from that repository. The package and existing signing identity remain independent.

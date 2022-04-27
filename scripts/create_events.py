#!/usr/bin/env python
import random, datetime, uuid, subprocess, time

while True:
    #timestamp = datetime.datetime.fromtimestamp(random.randint(1642222222,1652462222)).strftime("%Y-%m-%dT%H:%M:%S")
    timestamp = datetime.datetime.now().strftime("%Y-%m-%dT%H:%M:%SZ")
    event_str=f"INSERT INTO iri_events (iri_id, timestamp) VALUES ({random.randint(0,1000000)},'{timestamp}');"
    print(event_str)
    args_run = ["docker", "exec", "-it", "ksqldb-cli", "ksql", "http://ksqldb-server:8088", "--execute", event_str]
    print(args_run)
    subprocess.run(args_run)
    time.sleep(random.randint(0,5))

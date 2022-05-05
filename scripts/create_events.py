#!/usr/bin/env python
import random, datetime, uuid, subprocess, json, time, sys, requests
from random import randint
from datetime import timezone

try:
    c = int(sys.argv[1])
except IndexError:
    c = 0

# in DB directly
# INSERT INTO public.product (product_id,cell_id,longitude,latitude,iritimestamp) VALUES (1,'212-1223-123-1',10.37156293656584,46.44352198898034,'2022-05-02T16:25:57Z');

BBOX_CH = (
    (5.8358140744676303, 45.659168946713827),
    (10.979311848153316, 47.869910020393519),
)


try:
    while True:
        iri_id = c
        url = f"http://localhost:8099/products/"

        # timestamp = datetime.datetime.fromtimestamp(random.randint(1642222222,1652462222)).strftime("%Y-%m-%dT%H:%M:%S")
        iritimestamp = datetime.datetime.now(timezone.utc).strftime(
            "%Y-%m-%dT%H:%M:%SZ"
        )
        # event_str=f"INSERT INTO iri_events (iri_id, timestamp) VALUES ({random.randint(0,1000000)},'{timestamp}');"
        product_payload = {
            "cell_id": "%03d-%02d-%05d-%01d"
            % (randint(0, 999), randint(0, 99), randint(0, 99999), randint(0, 9)),
            "product_id": randint(1, 2),
            "iritimestamp": iritimestamp,
            "longitude": random.uniform(BBOX_CH[0][0], BBOX_CH[1][0]),
            "latitude": random.uniform(BBOX_CH[0][1], BBOX_CH[1][1]),
        }
        print(product_payload)
        r = requests.post(
            url,
            data=json.dumps(product_payload),
            headers={"Content-Type": "application/json"},
        )
        print(r.content)
        time.sleep(random.randint(0, 1))
        c += 1

except KeyboardInterrupt:
    exit(0)

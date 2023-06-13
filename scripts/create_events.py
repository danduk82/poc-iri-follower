#!/usr/bin/env python
import random, datetime, uuid, subprocess, json, time, sys, requests
from random import randint
from datetime import timezone
import psycopg2

try:
    nb_items = int(sys.argv[1])
except IndexError:
    nb_items = 1000

# in DB directly
# INSERT INTO public.product (product_id,cell_id,longitude,latitude,iritimestamp) VALUES (1,'212-1223-123-1',10.37156293656584,46.44352198898034,'2022-05-02T16:25:57Z');

BBOX_CH = (
    (5.8358140744676303, 45.659168946713827),
    (10.979311848153316, 47.869910020393519),
)

conn = psycopg2.connect(
    dbname="postgres", user="postgres", password="postgres", host="localhost", port=61544
)
#conn = psycopg2.connect(service="adn_flicc_geodata")


cur = conn.cursor()

# get the last id
cur.execute("select max(iri_id) from  public.product;")
try:
    nb_id = cur.fetchall()[0][0] + 1
except TypeError:
    nb_id = 1


# sql = """INSERT INTO geodata_yadn.t_location (IRI_ID, WKB_GEOMETRY, PRODUCT_ID, TARGET_LIID, TARGET_ADDRESS, CELL_ID, IRI_TIMESTAMP, AZIMUTH,
# 						CTL_DATE_CREATED, CTL_CREATED_BY)
# VALUES ({iri_id}, ST_SetSRID(ST_MakePoint({longitude}, {latitude}), 4326), {product_id},
# 		NULL, '+4131333284 XY_16_AB_CD_IRI DD_GG_6655111223', '{cell_id}', '{iritimestamp}', 290, NOW(),
# 		'geodata_yadn');"""
sql = """INSERT INTO public.product (product_id,cell_id,longitude,latitude,iritimestamp)
         VALUES ({product_id},'{cell_id}',{longitude}, {latitude},'{iritimestamp}');"""
try:
    for c in range(nb_id, nb_id + nb_items):
        iri_id = c
        iritimestamp = datetime.datetime.now(timezone.utc).strftime(
            "%Y-%m-%dT%H:%M:%SZ"
        )
        product_payload = {
            "iri_id": c,
            "cell_id": "%03d-%02d-%05d-%01d"
            % (randint(0, 999), randint(0, 99), randint(0, 99999), randint(0, 9)),
            "product_id": randint(1, 2),
            "iritimestamp": iritimestamp,
            "longitude": random.uniform(BBOX_CH[0][0], BBOX_CH[1][0]),
            "latitude": random.uniform(BBOX_CH[0][1], BBOX_CH[1][1]),
        }
        cur.execute(sql.format(**product_payload))

    conn.commit()

except KeyboardInterrupt:
    exit(0)

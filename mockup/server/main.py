from typing import Optional

from fastapi import FastAPI
import random
from random import randint
import datetime

app = FastAPI()

@app.get("/")
def read_root():
    return {"products":"/products/{iri_id}"}

BBOX_CH = ((5.8358140744676303, 45.659168946713827), (10.979311848153316, 47.869910020393519))

@app.get("/products/{iri_id}")
def read_item(iri_id: int, q: Optional[str] = None):
    now = datetime.datetime.now()
    iso_instant_fmt = "%Y-%m-%dT%H:%M:%SZ"

    return {
            "id": iri_id,
            "cell_id": '%03d-%02d-%05d-%01d' % (randint(0,999), randint(0,99), randint(0,99999), randint(0,9)) ,
            "product_id": randint(0,1000),
            "iritimestamp": now.strftime(iso_instant_fmt),
            "longitude": random.uniform(BBOX_CH[0][0],BBOX_CH[1][0]),
            "latitude": random.uniform(BBOX_CH[0][1],BBOX_CH[1][1])
        }
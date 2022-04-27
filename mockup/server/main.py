from typing import Optional

from fastapi import FastAPI
import random

app = FastAPI()

@app.get("/")
def read_root():
    return {[]}

BBOX_CH = ((5.8358140744676303, 45.659168946713827), (10.979311848153316, 47.869910020393519))

@app.get("/products/{iri_id}")
def read_item(iri_id: int, q: Optional[str] = None):

    return {
            "iri_id": iri_id,
            "product_id": random.randint(0,10000000),
            "LON": random.uniform(BBOX_CH[0][0],BBOX_CH[1][0]),
            "LAT": random.uniform(BBOX_CH[0][1],BBOX_CH[1][1])
        }
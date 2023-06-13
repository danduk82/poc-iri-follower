import datetime
import databases
import sqlalchemy
from fastapi import FastAPI, status
import os
import urllib
from pydantic import BaseModel


class ProductIn(BaseModel):
    product_id: int
    cell_id: str
    longitude: float
    latitude: float
    iritimestamp: datetime.datetime


class Product(BaseModel):
    iri_id: int
    product_id: int
    cell_id: str
    longitude: float
    latitude: float
    iritimestamp: datetime.datetime


host_server = os.environ.get("PG_HOST", "products-db")
db_server_port = urllib.parse.quote_plus(str(os.environ.get("PG_PORT", "5432")))
database_name = os.environ.get("PG_DATABASE", "postgres")
db_username = urllib.parse.quote_plus(str(os.environ.get("PG_USERNAME", "postgres")))
db_password = urllib.parse.quote_plus(str(os.environ.get("PG_PASSWORD", "postgres")))
ssl_mode = urllib.parse.quote_plus(str(os.environ.get("PG_SSL_MODE", "prefer")))
DATABASE_URL = "postgresql://{}:{}@{}:{}/{}?sslmode={}".format(
    db_username, db_password, host_server, db_server_port, database_name, ssl_mode
)

database = databases.Database(DATABASE_URL)

metadata = sqlalchemy.MetaData()

products = sqlalchemy.Table(
    "product",
    metadata,
    sqlalchemy.Column("iri_id", sqlalchemy.Integer, primary_key=True),
    sqlalchemy.Column("product_id", sqlalchemy.BIGINT),
    sqlalchemy.Column("cell_id", sqlalchemy.String),
    sqlalchemy.Column("longitude", sqlalchemy.FLOAT),
    sqlalchemy.Column("latitude", sqlalchemy.FLOAT),
    sqlalchemy.Column("iritimestamp", sqlalchemy.TIMESTAMP),
)
engine = sqlalchemy.create_engine(DATABASE_URL, pool_size=3, max_overflow=0)
metadata.create_all(engine)


app = FastAPI()
BBOX_CH = (
    (5.8358140744676303, 45.659168946713827),
    (10.979311848153316, 47.869910020393519),
)


@app.get("/")
def read_root():
    return {"products": "/products/{iri_id}"}


@app.post("/products/", response_model=Product, status_code=status.HTTP_201_CREATED)
async def create_product(product: ProductIn):
    query = products.insert().values(
        product_id=product.product_id,
        cell_id=product.cell_id,
        longitude=product.longitude,
        latitude=product.latitude,
        iritimestamp=product.iritimestamp,
    )
    last_record_id = await database.execute(query)
    return {**product.dict(), "iri_id": last_record_id}


@app.put("/products/{iri_id}/", response_model=Product, status_code=status.HTTP_200_OK)
async def update_product(iri_id: int, payload: ProductIn):
    query = (
        products.update()
        .where(products.c.iri_id == iri_id)
        .values(
            product_id=payload.product_id,
            cell_id=payload.cell_id,
            longitude=payload.longitude,
            latitude=payload.latitude,
            iritimestamp=payload.iritimestamp,
        )
    )
    await database.execute(query)
    return {**payload.dict(), "iri_id": iri_id}


@app.get("/products/{iri_id}/", response_model=Product, status_code=status.HTTP_200_OK)
async def read_products(iri_id: int):
    if iri_id % 2 == 0:
        raise Exception
    query = products.select().where(products.c.iri_id == iri_id)
    return await database.fetch_one(query)


@app.delete("/products/{iri_id}/", status_code=status.HTTP_204_NO_CONTENT)
async def update_note(iri_id: int):
    query = products.delete().where(products.c.id == iri_id)
    await database.execute(query)
    return {"message": "Product with id: {} deleted successfully!".format(iri_id)}


@app.on_event("startup")
async def startup():
    await database.connect()


@app.on_event("shutdown")
async def shutdown():
    await database.disconnect()
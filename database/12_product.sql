SET standard_conforming_strings = OFF;
BEGIN;
CREATE TABLE "public"."product" ( "iri_id" BIGSERIAL, CONSTRAINT "product_pk" PRIMARY KEY ("iri_id") );
ALTER TABLE "public"."product" ADD COLUMN "product_id" BIGINT;
ALTER TABLE "public"."product" ADD COLUMN "cell_id" VARCHAR;
ALTER TABLE "public"."product" ADD COLUMN "longitude" DOUBLE PRECISION ;
ALTER TABLE "public"."product" ADD COLUMN "latitude" DOUBLE PRECISION ;
ALTER TABLE "public"."product" ADD COLUMN "iritimestamp" TIMESTAMP WITH TIME ZONE;
COMMIT;

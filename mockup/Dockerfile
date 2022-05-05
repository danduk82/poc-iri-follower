FROM python:3.8

RUN pip install fastapi "uvicorn[standard]" "databases[postgresql]" databases 
RUN pip install psycopg2
COPY server /server
WORKDIR /server
ENV UVICORN_PORT=8099
CMD uvicorn main:app --reload --host 0.0.0.0

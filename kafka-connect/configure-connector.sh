#!/bin/sh

echo "Waiting for Kafka Connect to start..."
while [ $(curl -s -o /dev/null -w %{http_code} http://kafka-connect:8083/connectors) -ne 200 ]; do
  echo "Kafka Connect is not yet available. Retrying in 5 seconds..."
  sleep 5
done

echo "Kafka Connect is up! Configuring the connector."

curl -i -X PUT -H "Accept:application/json" -H "Content-Type:application/json" \
http://kafka-connect:8083/connectors/ticket-connector/config \
-d @- <<EOF
{
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "plugin.name": "pgoutput",
    "database.hostname": "db",
    "database.port": "5432",
    "database.user": "${DB_USERNAME}",
    "database.password": "${DB_PASSWORD}",
    "database.dbname": "${DB_NAME}",
    "topic.prefix": "abusafar-db-changes",
    "table.include.list": "public.tickets,public.trips,public.companies",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter.schemas.enable": "false"
}
EOF

echo "Connector configuration complete."
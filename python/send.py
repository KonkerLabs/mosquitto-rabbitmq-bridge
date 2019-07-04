#!/usr/bin/env python
import pika

connection = pika.BlockingConnection(pika.ConnectionParameters(
        host='localhost'))
channel = connection.channel()

#channel.queue_declare(queue='data.sub')

channel.basic_publish(exchange='',
                      routing_key='data.sub',
                      properties=pika.BasicProperties(
                          headers={'apiKey': 'apiKey' , 'channel': 'temp'}
                      ),
                      body='Hello World!')
print(" [x] Sent 'Hello World!'")
connection.close()
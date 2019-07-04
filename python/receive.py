#!/usr/bin/env python
import pika

connection = pika.BlockingConnection(pika.ConnectionParameters(
        host='localhost'))
channel = connection.channel()

channel.queue_declare(queue='data.pub')

def callback(ch, method, properties, body):
    print(" [x] Received %r" % body)
    print(" [x] Properties %r" % properties.headers)

channel.basic_consume(callback,
                      queue='data.pub',
                      no_ack=True)

print(' [*] Waiting for messages. To exit press CTRL+C')
channel.start_consuming()
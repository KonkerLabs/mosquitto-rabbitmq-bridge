FROM rabbitmq:3.6.9-alpine

RUN rabbitmq-plugins enable --offline rabbitmq_management

ADD init.sh /init.sh

CMD ["/init.sh"]

EXPOSE 15671 15672
#!/bin/sh

# Create Rabbitmq user
( sleep 10 ; \
echo ""
echo "*** Creating vhosts ***" ; \
rabbitmqctl add_vhost /devices ; \
echo "" ; \
rabbitmqctl add_user konker yelloW\@2017 ; \
rabbitmqctl set_user_tags konker administrator ; \
rabbitmqctl set_permissions -p /devices konker ".*" ".*" ".*" ; \
rabbitmqctl set_permissions -p /                konker ".*" ".*" ".*" ; \
echo "" ; \
rabbitmqctl add_user device x9Agz2YPea7X ; \
rabbitmqctl set_permissions -p /devices device ".*" ".*" ".*" ; \
echo "" ; \
rabbitmqctl delete_user guest ; \
echo "" ; \
echo "*** Done! ***") &

# $@ is used to pass arguments to the rabbitmq-server command.
# For example if you use it like this: docker run -d rabbitmq arg1 arg2,
# it will be as you run in the container rabbitmq-server arg1 arg2
rabbitmq-server $@

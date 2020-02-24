import os

import flask_injector
import injector
import jar_invoker
import pika
import providers
import requests
from flask import Flask, escape, request
from redis import StrictRedis

INJECTOR_DEFAULT_MODULES = dict(
    redis_client=providers.RedisClientModule(),
)


def _configure_dependency_injection(
        flask_app, injector_modules, custom_injector
) -> None:
    modules = dict(INJECTOR_DEFAULT_MODULES)

    if injector_modules:
        modules.update(injector_modules)

    flask_injector.FlaskInjector(
        app=flask_app,
        injector=custom_injector,
        modules=modules.values()
    )


def create_app(
        *,
        custom_injector: injector.Injector=None,
        injector_modules=None
):
    app = Flask(__name__)
    app.config.update({
        'REDIS_HOST': os.environ['REDIS_HOST'],
        'REDIS_PORT': os.environ['REDIS_PORT'],
        'REDIS_DB': 0
    })

    @app.route('/')
    def hello_world():
        return 'Hello World!'

    @app.route('/invoke/batch', methods=['POST'])
    def invoke_batch():
        customer_name = request.get_json(force=True)['customer_name']
        return str(jar_invoker.run_all_batch(escape(customer_name))) + '\n', 200

    @app.route('/invoke/streaming', methods=['POST'])
    def invoke_streaming():
        customer_name = request.get_json(force=True)['customer_name']
        return str(jar_invoker.run_all_streaming(escape(customer_name))) + '\n', 200

    @app.route('/kill_all/streaming', methods=['POST'])
    def kill_all_streaming_apps():
        customer_name = request.get_json(force=True)['customer_name']
        return str(jar_invoker.kill_all_streaming(customer_name)) + '\n', 200

    @app.route('/kill_all/batch', methods=['POST'])
    def kill_all_batch_apps():
        customer_name = request.get_json(force=True)['customer_name']
        return str(jar_invoker.kill_all_batch(customer_name)) + '\n', 200

    @app.route('/register/queue', methods=['POST'])
    def register_queue():
        queue_name = request.get_json(force=True)['queue_name']
        credentials = pika.PlainCredentials(
            username='guest', password='guest')
        connection = pika.BlockingConnection(
            pika.ConnectionParameters(host=os.environ['BROKER_HOST'], credentials=credentials))
        channel = connection.channel()
        channel.queue_declare(queue=queue_name, durable=True)
        return 'OK', 201

    @injector.inject
    @app.route('/report-metrics', methods=['POST'])
    def report_metrics(redis_client: StrictRedis):
        report = request.get_json(force=True)
        processing_rate = report['processingRate']
        customer_id = report['customerId']
        # existing_rates = redis_client.lrange(customer_id, 0, -1)

        response = requests.get("http://rabbitmq:15672/api/queues/%2F/" + customer_id, auth=('guest', 'guest'))
        queue_metrics = response.json()

        nr_of_messages = queue_metrics['messages']
        avg_egress_rate = queue_metrics['backing_queue_status']['avg_egress_rate']
        nr_of_consumers = queue_metrics['consumers']

        print("nr_of_messages: ", nr_of_messages, "avg_egress_rate: ", avg_egress_rate, "nr_of_consumers: ", nr_of_consumers)

        # if nr_of_messages != 0 and processing_rate > int(os.environ['MIN_RATE']) and nr_of_consumers < int(os.environ['MAX_WORKERS']):
        #     jar_invoker.run_all(customer_id)

        # redis_client.lpushx(customer_id, processing_rate)
        # redis_client.ltrim(customer_id, 1, -1)

        return str('OK'), 200

    _configure_dependency_injection(app, injector_modules, custom_injector)

    return app


if __name__ == '__main__':
    app = create_app()
    app.run(debug=True, host='0.0.0.0')

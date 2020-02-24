import flask
import flask_injector
import injector
import redis


class RedisClientModule(injector.Module):

    def configure(self, binder):
        binder.bind(redis.StrictRedis,
                    to=self.create,
                    scope=flask_injector.request)

    @injector.inject
    def create(
            self,
            config: flask.Config,
    ) -> redis.StrictRedis:
        return redis.StrictRedis(host=config['REDIS_HOST'],
                                 port=config['REDIS_PORT'])
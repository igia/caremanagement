function() {
  var env = karate.env;
  karate.log('karate.env system property was:', env);
  if (!env) {
    env = 'dev';
  }
  var config = {
    baseUrl: 'http://localhost:8091/api',
    camundaUrl: 'http://localhost:9085/engine-rest',
    tokenUrl: 'http://localhost:9080/auth/realms/igia/protocol/openid-connect/token',
    clientId: 'internal',
    clientSecret: 'internal'
  };

  karate.configure('connectTimeout', 50000);
  karate.configure('readTimeout', 50000);
  karate.configure('logPrettyRequest', true);
  return config;
}

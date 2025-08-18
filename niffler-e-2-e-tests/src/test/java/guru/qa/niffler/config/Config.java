package guru.qa.niffler.config;

public interface Config {

  static Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
        ? DockerConfig.instance
        : LocalConfig.instance;
  }

  String frontUrl();

  String authUrl();

  String authJdbcUrl();

  String gatewayUrl();

  String userdataUrl();

  String userdataJdbcUrl();

  String spendUrl();

  String spendJdbcUrl();

  String currencyGrpcAddress();

  default int currencyGrpcPort() {
    return 8092;
  }

  String currencyJdbcUrl();

  default String ghUrl() {
    return "https://api.github.com/";
  }
}

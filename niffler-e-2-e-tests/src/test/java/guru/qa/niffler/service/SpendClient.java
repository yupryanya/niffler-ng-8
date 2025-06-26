package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.api.SpendApiClient;
import guru.qa.niffler.service.db.SpendDbClient;

public interface SpendClient {
  static SpendClient getInstance() {
    return "api".equals(System.getProperty("client.impl"))
        ? new SpendApiClient()
        : new SpendDbClient();
  }

  SpendJson createSpend(SpendJson spend);

  CategoryJson createCategory(CategoryJson category);

  CategoryJson updateCategory(CategoryJson category);
}

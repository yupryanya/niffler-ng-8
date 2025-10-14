package guru.qa.niffler.service;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.ex.SpendNotFoundException;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class SpendServiceTest {

  @Test
  void getSpendForUserShouldThrowForInvalidId(
      @Mock SpendRepository spendRepository,
      @Mock CategoryService categoryService
  ) {
    final String invalidId = "invalid-id";
    SpendService spendService = new SpendService(spendRepository, categoryService);

    SpendNotFoundException ex = assertThrows(
        SpendNotFoundException.class,
        () -> spendService.getSpendForUser(invalidId, "any-username")
    );
    assertEquals("Can`t find spend by given id: " + invalidId, ex.getMessage());
  }

  @Test
  void getSpendForUserShouldThrowForInexistentId(
      @Mock SpendRepository spendRepository,
      @Mock CategoryService categoryService
  ) {
    final UUID id = UUID.randomUUID();
    final String username = "username";

    Mockito.when(spendRepository.findByIdAndUsername(eq(id), eq(username)))
        .thenReturn(java.util.Optional.empty());

    SpendService spendService = new SpendService(spendRepository, categoryService);

    SpendNotFoundException ex = assertThrows(
        SpendNotFoundException.class,
        () -> spendService.getSpendForUser(id.toString(), username)
    );
    assertEquals("Can`t find spend by given id: " + id, ex.getMessage());
  }

  @Test
  void getSpendForUserShouldReturnCorrectJsonObject(
      @Mock SpendRepository spendRepository,
      @Mock CategoryService categoryService
  ) {
    final UUID id = UUID.randomUUID();
    final String username = "username";
    final SpendEntity spendEntity = new SpendEntity();
    final CategoryEntity categoryEntity = new CategoryEntity();
    categoryEntity.setId(UUID.randomUUID());
    categoryEntity.setUsername(username);
    categoryEntity.setName("category-name");
    categoryEntity.setArchived(false);

    spendEntity.setId(id);
    spendEntity.setUsername(username);
    spendEntity.setAmount(100.0);
    spendEntity.setCurrency(CurrencyValues.USD);
    spendEntity.setDescription("description");
    spendEntity.setSpendDate(new Date());
    spendEntity.setCategory(categoryEntity);

    Mockito.when(spendRepository.findByIdAndUsername(eq(id), eq(username)))
        .thenReturn(java.util.Optional.of(spendEntity));

    SpendService spendService = new SpendService(spendRepository, categoryService);
    SpendJson result = spendService.getSpendForUser(id.toString(), username);

    assertEquals(id, result.id());
    assertEquals(username, result.username());
    assertEquals(100.0, result.amount());
    assertEquals(CurrencyValues.USD, result.currency());
    assertEquals("description", result.description());
    assertEquals(categoryEntity.getId(), result.category().id());
    assertEquals(categoryEntity.getName(), result.category().name());
    assertEquals(categoryEntity.getUsername(), result.category().username());
    assertFalse(result.category().archived());
  }
}